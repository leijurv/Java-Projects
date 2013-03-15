/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class StandardSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*     */   public static final String TCP_NO_DELAY_PROPERTY_NAME = "tcpNoDelay";
/*     */   public static final String TCP_KEEP_ALIVE_DEFAULT_VALUE = "true";
/*     */   public static final String TCP_KEEP_ALIVE_PROPERTY_NAME = "tcpKeepAlive";
/*     */   public static final String TCP_RCV_BUF_PROPERTY_NAME = "tcpRcvBuf";
/*     */   public static final String TCP_SND_BUF_PROPERTY_NAME = "tcpSndBuf";
/*     */   public static final String TCP_TRAFFIC_CLASS_PROPERTY_NAME = "tcpTrafficClass";
/*     */   public static final String TCP_RCV_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_SND_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_TRAFFIC_CLASS_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_NO_DELAY_DEFAULT_VALUE = "true";
/*     */   private static Method setTraficClassMethod;
/*  82 */   protected String host = null;
/*     */ 
/*  85 */   protected int port = 3306;
/*     */ 
/*  88 */   protected Socket rawSocket = null;
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 102 */     return this.rawSocket;
/*     */   }
/*     */ 
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 117 */     return this.rawSocket;
/*     */   }
/*     */ 
/*     */   private void configureSocket(Socket sock, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/*     */     try
/*     */     {
/* 131 */       sock.setTcpNoDelay(Boolean.valueOf(props.getProperty("tcpNoDelay", "true")).booleanValue());
/*     */ 
/* 135 */       String keepAlive = props.getProperty("tcpKeepAlive", "true");
/*     */ 
/* 138 */       if ((keepAlive != null) && (keepAlive.length() > 0)) {
/* 139 */         sock.setKeepAlive(Boolean.valueOf(keepAlive).booleanValue());
/*     */       }
/*     */ 
/* 143 */       int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */ 
/* 146 */       if (receiveBufferSize > 0) {
/* 147 */         sock.setReceiveBufferSize(receiveBufferSize);
/*     */       }
/*     */ 
/* 150 */       int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */ 
/* 153 */       if (sendBufferSize > 0) {
/* 154 */         sock.setSendBufferSize(sendBufferSize);
/*     */       }
/*     */ 
/* 157 */       int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */ 
/* 161 */       if ((trafficClass > 0) && (setTraficClassMethod != null))
/* 162 */         setTraficClassMethod.invoke(sock, new Object[] { new Integer(trafficClass) });
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 166 */       unwrapExceptionToProperClassAndThrowIt(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Socket connect(String hostname, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 176 */     if (props != null) {
/* 177 */       this.host = hostname;
/*     */ 
/* 179 */       this.port = portNumber;
/*     */ 
/* 181 */       Method connectWithTimeoutMethod = null;
/* 182 */       Method socketBindMethod = null;
/* 183 */       Class socketAddressClass = null;
/*     */ 
/* 185 */       String localSocketHostname = props.getProperty("localSocketAddress");
/*     */ 
/* 188 */       String connectTimeoutStr = props.getProperty("connectTimeout");
/*     */ 
/* 190 */       int connectTimeout = 0;
/*     */ 
/* 192 */       boolean wantsTimeout = (connectTimeoutStr != null) && (connectTimeoutStr.length() > 0) && (!connectTimeoutStr.equals("0"));
/*     */ 
/* 196 */       boolean wantsLocalBind = (localSocketHostname != null) && (localSocketHostname.length() > 0);
/*     */ 
/* 199 */       boolean needsConfigurationBeforeConnect = socketNeedsConfigurationBeforeConnect(props);
/*     */ 
/* 201 */       if ((wantsTimeout) || (wantsLocalBind) || (needsConfigurationBeforeConnect))
/*     */       {
/* 203 */         if (connectTimeoutStr != null) {
/*     */           try {
/* 205 */             connectTimeout = Integer.parseInt(connectTimeoutStr);
/*     */           } catch (NumberFormatException nfe) {
/* 207 */             throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 215 */           socketAddressClass = Class.forName("java.net.SocketAddress");
/*     */ 
/* 218 */           connectWithTimeoutMethod = class$java$net$Socket.getMethod("connect", new Class[] { socketAddressClass, Integer.TYPE });
/*     */ 
/* 222 */           socketBindMethod = class$java$net$Socket.getMethod("bind", new Class[] { socketAddressClass });
/*     */         }
/*     */         catch (NoClassDefFoundError noClassDefFound)
/*     */         {
/*     */         }
/*     */         catch (NoSuchMethodException noSuchMethodEx)
/*     */         {
/*     */         }
/*     */         catch (Throwable catchAll)
/*     */         {
/*     */         }
/* 233 */         if ((wantsLocalBind) && (socketBindMethod == null)) {
/* 234 */           throw new SocketException("Can't specify \"localSocketAddress\" on JVMs older than 1.4");
/*     */         }
/*     */ 
/* 238 */         if ((wantsTimeout) && (connectWithTimeoutMethod == null)) {
/* 239 */           throw new SocketException("Can't specify \"connectTimeout\" on JVMs older than 1.4");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 244 */       if (this.host != null) {
/* 245 */         if ((!wantsLocalBind) && (!wantsTimeout) && (!needsConfigurationBeforeConnect)) {
/* 246 */           InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/* 249 */           Throwable caughtWhileConnecting = null;
/*     */ 
/* 254 */           for (int i = 0; i < possibleAddresses.length; i++) {
/*     */             try {
/* 256 */               this.rawSocket = new Socket(possibleAddresses[i], this.port);
/*     */ 
/* 259 */               configureSocket(this.rawSocket, props);
/*     */             }
/*     */             catch (Exception ex)
/*     */             {
/* 263 */               caughtWhileConnecting = ex;
/*     */             }
/*     */           }
/*     */ 
/* 267 */           if (this.rawSocket == null) {
/* 268 */             unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 275 */             InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/* 278 */             Throwable caughtWhileConnecting = null;
/*     */ 
/* 280 */             Object localSockAddr = null;
/*     */ 
/* 282 */             Class inetSocketAddressClass = null;
/*     */ 
/* 284 */             Constructor addrConstructor = null;
/*     */             try
/*     */             {
/* 287 */               inetSocketAddressClass = Class.forName("java.net.InetSocketAddress");
/*     */ 
/* 290 */               addrConstructor = inetSocketAddressClass.getConstructor(new Class[] { InetAddress.class, Integer.TYPE });
/*     */ 
/* 294 */               if (wantsLocalBind) {
/* 295 */                 localSockAddr = addrConstructor.newInstance(new Object[] { InetAddress.getByName(localSocketHostname), new Integer(0) });
/*     */               }
/*     */ 
/*     */             }
/*     */             catch (Throwable ex)
/*     */             {
/* 306 */               unwrapExceptionToProperClassAndThrowIt(ex);
/*     */             }
/*     */ 
/* 312 */             for (int i = 0; i < possibleAddresses.length; i++) {
/*     */               try
/*     */               {
/* 315 */                 this.rawSocket = new Socket();
/*     */ 
/* 317 */                 configureSocket(this.rawSocket, props);
/*     */ 
/* 319 */                 Object sockAddr = addrConstructor.newInstance(new Object[] { possibleAddresses[i], new Integer(this.port) });
/*     */ 
/* 326 */                 socketBindMethod.invoke(this.rawSocket, new Object[] { localSockAddr });
/*     */ 
/* 329 */                 connectWithTimeoutMethod.invoke(this.rawSocket, new Object[] { sockAddr, new Integer(connectTimeout) });
/*     */               }
/*     */               catch (Exception ex)
/*     */               {
/* 335 */                 this.rawSocket = null;
/*     */ 
/* 337 */                 caughtWhileConnecting = ex;
/*     */               }
/*     */             }
/*     */ 
/* 341 */             if (this.rawSocket == null)
/* 342 */               unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 346 */             unwrapExceptionToProperClassAndThrowIt(t);
/*     */           }
/*     */         }
/*     */ 
/* 350 */         return this.rawSocket;
/*     */       }
/*     */     }
/*     */ 
/* 354 */     throw new SocketException("Unable to create socket");
/*     */   }
/*     */ 
/*     */   private boolean socketNeedsConfigurationBeforeConnect(Properties props)
/*     */   {
/* 363 */     int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */ 
/* 366 */     if (receiveBufferSize > 0) {
/* 367 */       return true;
/*     */     }
/*     */ 
/* 370 */     int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */ 
/* 373 */     if (sendBufferSize > 0) {
/* 374 */       return true;
/*     */     }
/*     */ 
/* 377 */     int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */ 
/* 382 */     return (trafficClass > 0) && (setTraficClassMethod != null);
/*     */   }
/*     */ 
/*     */   private void unwrapExceptionToProperClassAndThrowIt(Throwable caughtWhileConnecting)
/*     */     throws SocketException, IOException
/*     */   {
/* 391 */     if ((caughtWhileConnecting instanceof InvocationTargetException))
/*     */     {
/* 395 */       caughtWhileConnecting = ((InvocationTargetException)caughtWhileConnecting).getTargetException();
/*     */     }
/*     */ 
/* 399 */     if ((caughtWhileConnecting instanceof SocketException)) {
/* 400 */       throw ((SocketException)caughtWhileConnecting);
/*     */     }
/*     */ 
/* 403 */     if ((caughtWhileConnecting instanceof IOException)) {
/* 404 */       throw ((IOException)caughtWhileConnecting);
/*     */     }
/*     */ 
/* 407 */     throw new SocketException(caughtWhileConnecting.toString());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  72 */       setTraficClassMethod = class$java$net$Socket.getMethod("setTrafficClass", new Class[] { Integer.TYPE });
/*     */     }
/*     */     catch (SecurityException e) {
/*  75 */       setTraficClassMethod = null;
/*     */     } catch (NoSuchMethodException e) {
/*  77 */       setTraficClassMethod = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.StandardSocketFactory
 * JD-Core Version:    0.6.0
 */