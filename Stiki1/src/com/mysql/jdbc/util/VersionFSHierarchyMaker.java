/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.NonRegisteringDriver;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public class VersionFSHierarchyMaker
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  45 */     if (args.length < 3) {
/*  46 */       usage();
/*  47 */       System.exit(1);
/*     */     }
/*     */ 
/*  50 */     String jdbcUrl = null;
/*     */ 
/*  52 */     String jvmVersion = removeWhitespaceChars(System.getProperty("java.version"));
/*  53 */     String jvmVendor = removeWhitespaceChars(System.getProperty("java.vendor"));
/*  54 */     String osName = removeWhitespaceChars(System.getProperty("os.name"));
/*  55 */     String osArch = removeWhitespaceChars(System.getProperty("os.arch"));
/*  56 */     String osVersion = removeWhitespaceChars(System.getProperty("os.version"));
/*     */ 
/*  58 */     jdbcUrl = System.getProperty("com.mysql.jdbc.testsuite.url");
/*     */ 
/*  60 */     String mysqlVersion = "not-available";
/*     */     try
/*     */     {
/*  63 */       Connection conn = new NonRegisteringDriver().connect(jdbcUrl, null);
/*     */ 
/*  65 */       ResultSet rs = conn.createStatement().executeQuery("SELECT VERSION()");
/*  66 */       rs.next();
/*  67 */       mysqlVersion = removeWhitespaceChars(rs.getString(1));
/*     */     } catch (Throwable t) {
/*  69 */       mysqlVersion = "no-server-running-on-" + removeWhitespaceChars(jdbcUrl);
/*     */     }
/*     */ 
/*  72 */     String jvmSubdirName = jvmVendor + "-" + jvmVersion;
/*  73 */     String osSubdirName = osName + "-" + osArch + "-" + osVersion;
/*     */ 
/*  75 */     File baseDir = new File(args[1]);
/*  76 */     File mysqlVersionDir = new File(baseDir, mysqlVersion);
/*  77 */     File osVersionDir = new File(mysqlVersionDir, osSubdirName);
/*  78 */     File jvmVersionDir = new File(osVersionDir, jvmSubdirName);
/*     */ 
/*  80 */     jvmVersionDir.mkdirs();
/*     */ 
/*  83 */     FileOutputStream pathOut = null;
/*     */     try
/*     */     {
/*  86 */       String propsOutputPath = args[2];
/*  87 */       pathOut = new FileOutputStream(propsOutputPath);
/*  88 */       String baseDirStr = baseDir.getAbsolutePath();
/*  89 */       String jvmVersionDirStr = jvmVersionDir.getAbsolutePath();
/*     */ 
/*  91 */       if (jvmVersionDirStr.startsWith(baseDirStr)) {
/*  92 */         jvmVersionDirStr = jvmVersionDirStr.substring(baseDirStr.length() + 1);
/*     */       }
/*     */ 
/*  95 */       pathOut.write(jvmVersionDirStr.getBytes());
/*     */     } finally {
/*  97 */       if (pathOut != null) {
/*  98 */         pathOut.flush();
/*  99 */         pathOut.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String removeWhitespaceChars(String input) {
/* 105 */     if (input == null) {
/* 106 */       return input;
/*     */     }
/*     */ 
/* 109 */     int strLen = input.length();
/*     */ 
/* 111 */     StringBuffer output = new StringBuffer(strLen);
/*     */ 
/* 113 */     for (int i = 0; i < strLen; i++) {
/* 114 */       char c = input.charAt(i);
/* 115 */       if ((!Character.isDigit(c)) && (!Character.isLetter(c))) {
/* 116 */         if (Character.isWhitespace(c))
/* 117 */           output.append("_");
/*     */         else
/* 119 */           output.append(".");
/*     */       }
/*     */       else {
/* 122 */         output.append(c);
/*     */       }
/*     */     }
/*     */ 
/* 126 */     return output.toString();
/*     */   }
/*     */ 
/*     */   private static void usage() {
/* 130 */     System.err.println("Creates a fs hierarchy representing MySQL version, OS version and JVM version.");
/* 131 */     System.err.println("Stores the full path as 'outputDirectory' property in file 'directoryPropPath'");
/* 132 */     System.err.println();
/* 133 */     System.err.println("Usage: java VersionFSHierarchyMaker unit|compliance baseDirectory directoryPropPath");
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.VersionFSHierarchyMaker
 * JD-Core Version:    0.6.0
 */