/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class NamedPipeSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*     */   private static final String NAMED_PIPE_PROP_NAME = "namedPipePath";
/*     */   private Socket namedPipeSocket;
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 190 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 197 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */   public Socket connect(String host, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 205 */     String namedPipePath = props.getProperty("namedPipePath");
/*     */ 
/* 207 */     if (namedPipePath == null)
/* 208 */       namedPipePath = "\\\\.\\pipe\\MySQL";
/* 209 */     else if (namedPipePath.length() == 0) {
/* 210 */       throw new SocketException(Messages.getString("NamedPipeSocketFactory.2") + "namedPipePath" + Messages.getString("NamedPipeSocketFactory.3"));
/*     */     }
/*     */ 
/* 216 */     this.namedPipeSocket = new NamedPipeSocket(namedPipePath);
/*     */ 
/* 218 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */   class RandomAccessFileOutputStream extends OutputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */ 
/*     */     RandomAccessFileOutputStream(RandomAccessFile file)
/*     */     {
/* 144 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 151 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */     public void write(byte[] b)
/*     */       throws IOException
/*     */     {
/* 158 */       this.raFile.write(b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 165 */       this.raFile.write(b, off, len);
/*     */     }
/*     */ 
/*     */     public void write(int b)
/*     */       throws IOException
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   class RandomAccessFileInputStream extends InputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */ 
/*     */     RandomAccessFileInputStream(RandomAccessFile file)
/*     */     {
/*  98 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 105 */       return -1;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 112 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 119 */       return this.raFile.read();
/*     */     }
/*     */ 
/*     */     public int read(byte[] b)
/*     */       throws IOException
/*     */     {
/* 126 */       return this.raFile.read(b);
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 133 */       return this.raFile.read(b, off, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   class NamedPipeSocket extends Socket
/*     */   {
/*  48 */     private boolean isClosed = false;
/*     */     private RandomAccessFile namedPipeFile;
/*     */ 
/*     */     NamedPipeSocket(String filePath)
/*     */       throws IOException
/*     */     {
/*  53 */       if ((filePath == null) || (filePath.length() == 0)) {
/*  54 */         throw new IOException(Messages.getString("NamedPipeSocketFactory.4"));
/*     */       }
/*     */ 
/*  58 */       this.namedPipeFile = new RandomAccessFile(filePath, "rw");
/*     */     }
/*     */ 
/*     */     public synchronized void close()
/*     */       throws IOException
/*     */     {
/*  65 */       this.namedPipeFile.close();
/*  66 */       this.isClosed = true;
/*     */     }
/*     */ 
/*     */     public InputStream getInputStream()
/*     */       throws IOException
/*     */     {
/*  73 */       return new NamedPipeSocketFactory.RandomAccessFileInputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
/*     */     }
/*     */ 
/*     */     public OutputStream getOutputStream()
/*     */       throws IOException
/*     */     {
/*  80 */       return new NamedPipeSocketFactory.RandomAccessFileOutputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
/*     */     }
/*     */ 
/*     */     public boolean isClosed()
/*     */     {
/*  87 */       return this.isClosed;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.NamedPipeSocketFactory
 * JD-Core Version:    0.6.0
 */