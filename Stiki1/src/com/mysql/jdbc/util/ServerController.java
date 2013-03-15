/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ServerController
/*     */ {
/*     */   public static final String BASEDIR_KEY = "basedir";
/*     */   public static final String DATADIR_KEY = "datadir";
/*     */   public static final String DEFAULTS_FILE_KEY = "defaults-file";
/*     */   public static final String EXECUTABLE_NAME_KEY = "executable";
/*     */   public static final String EXECUTABLE_PATH_KEY = "executablePath";
/*  81 */   private Process serverProcess = null;
/*     */ 
/*  86 */   private Properties serverProps = null;
/*     */ 
/*  91 */   private Properties systemProps = null;
/*     */ 
/*     */   public ServerController(String baseDir)
/*     */   {
/* 102 */     setBaseDir(baseDir);
/*     */   }
/*     */ 
/*     */   public ServerController(String basedir, String datadir)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setBaseDir(String baseDir)
/*     */   {
/* 124 */     getServerProps().setProperty("basedir", baseDir);
/*     */   }
/*     */ 
/*     */   public void setDataDir(String dataDir)
/*     */   {
/* 134 */     getServerProps().setProperty("datadir", dataDir);
/*     */   }
/*     */ 
/*     */   public Process start()
/*     */     throws IOException
/*     */   {
/* 147 */     if (this.serverProcess != null) {
/* 148 */       throw new IllegalArgumentException("Server already started");
/*     */     }
/* 150 */     this.serverProcess = Runtime.getRuntime().exec(getCommandLine());
/*     */ 
/* 152 */     return this.serverProcess;
/*     */   }
/*     */ 
/*     */   public void stop(boolean forceIfNecessary)
/*     */     throws IOException
/*     */   {
/* 166 */     if (this.serverProcess != null)
/*     */     {
/* 168 */       String basedir = getServerProps().getProperty("basedir");
/*     */ 
/* 170 */       StringBuffer pathBuf = new StringBuffer(basedir);
/*     */ 
/* 172 */       if (!basedir.endsWith(File.separator)) {
/* 173 */         pathBuf.append(File.separator);
/*     */       }
/*     */ 
/* 176 */       String defaultsFilePath = getServerProps().getProperty("defaults-file");
/*     */ 
/* 179 */       pathBuf.append("bin");
/* 180 */       pathBuf.append(File.separator);
/* 181 */       pathBuf.append("mysqladmin shutdown");
/*     */ 
/* 183 */       System.out.println(pathBuf.toString());
/*     */ 
/* 185 */       Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
/*     */ 
/* 187 */       int exitStatus = -1;
/*     */       try
/*     */       {
/* 190 */         exitStatus = mysqladmin.waitFor();
/*     */       }
/*     */       catch (InterruptedException ie)
/*     */       {
/*     */       }
/*     */ 
/* 199 */       if ((exitStatus != 0) && (forceIfNecessary))
/* 200 */         forceStop();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void forceStop()
/*     */   {
/* 209 */     if (this.serverProcess != null) {
/* 210 */       this.serverProcess.destroy();
/* 211 */       this.serverProcess = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Properties getServerProps()
/*     */   {
/* 222 */     if (this.serverProps == null) {
/* 223 */       this.serverProps = new Properties();
/*     */     }
/*     */ 
/* 226 */     return this.serverProps;
/*     */   }
/*     */ 
/*     */   private String getCommandLine()
/*     */   {
/* 236 */     StringBuffer commandLine = new StringBuffer(getFullExecutablePath());
/* 237 */     commandLine.append(buildOptionalCommandLine());
/*     */ 
/* 239 */     return commandLine.toString();
/*     */   }
/*     */ 
/*     */   private String getFullExecutablePath()
/*     */   {
/* 248 */     StringBuffer pathBuf = new StringBuffer();
/*     */ 
/* 250 */     String optionalExecutablePath = getServerProps().getProperty("executablePath");
/*     */ 
/* 253 */     if (optionalExecutablePath == null)
/*     */     {
/* 255 */       String basedir = getServerProps().getProperty("basedir");
/* 256 */       pathBuf.append(basedir);
/*     */ 
/* 258 */       if (!basedir.endsWith(File.separator)) {
/* 259 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */ 
/* 262 */       if (runningOnWindows())
/* 263 */         pathBuf.append("bin");
/*     */       else {
/* 265 */         pathBuf.append("libexec");
/*     */       }
/*     */ 
/* 268 */       pathBuf.append(File.separatorChar);
/*     */     } else {
/* 270 */       pathBuf.append(optionalExecutablePath);
/*     */ 
/* 272 */       if (!optionalExecutablePath.endsWith(File.separator)) {
/* 273 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */     }
/*     */ 
/* 277 */     String executableName = getServerProps().getProperty("executable", "mysqld");
/*     */ 
/* 280 */     pathBuf.append(executableName);
/*     */ 
/* 282 */     return pathBuf.toString();
/*     */   }
/*     */ 
/*     */   private String buildOptionalCommandLine()
/*     */   {
/* 292 */     StringBuffer commandLineBuf = new StringBuffer();
/*     */ 
/* 294 */     if (this.serverProps != null)
/*     */     {
/* 296 */       Iterator iter = this.serverProps.keySet().iterator();
/* 297 */       while (iter.hasNext()) {
/* 298 */         String key = (String)iter.next();
/* 299 */         String value = this.serverProps.getProperty(key);
/*     */ 
/* 301 */         if (!isNonCommandLineArgument(key)) {
/* 302 */           if ((value != null) && (value.length() > 0)) {
/* 303 */             commandLineBuf.append(" \"");
/* 304 */             commandLineBuf.append("--");
/* 305 */             commandLineBuf.append(key);
/* 306 */             commandLineBuf.append("=");
/* 307 */             commandLineBuf.append(value);
/* 308 */             commandLineBuf.append("\"");
/*     */           } else {
/* 310 */             commandLineBuf.append(" --");
/* 311 */             commandLineBuf.append(key);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 317 */     return commandLineBuf.toString();
/*     */   }
/*     */ 
/*     */   private boolean isNonCommandLineArgument(String propName)
/*     */   {
/* 326 */     return (propName.equals("executable")) || (propName.equals("executablePath"));
/*     */   }
/*     */ 
/*     */   private synchronized Properties getSystemProperties()
/*     */   {
/* 336 */     if (this.systemProps == null) {
/* 337 */       this.systemProps = System.getProperties();
/*     */     }
/*     */ 
/* 340 */     return this.systemProps;
/*     */   }
/*     */ 
/*     */   private boolean runningOnWindows()
/*     */   {
/* 349 */     return StringUtils.indexOfIgnoreCase(getSystemProperties().getProperty("os.name"), "WINDOWS") != -1;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.ServerController
 * JD-Core Version:    0.6.0
 */