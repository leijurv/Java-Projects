/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class LogFactory
/*     */ {
/*     */   public static Log getLogger(String className, String instanceName, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  61 */     if (className == null) {
/*  62 */       throw SQLError.createSQLException("Logger class can not be NULL", "S1009", exceptionInterceptor);
/*     */     }
/*     */ 
/*  66 */     if (instanceName == null) {
/*  67 */       throw SQLError.createSQLException("Logger instance name can not be NULL", "S1009", exceptionInterceptor);
/*     */     }
/*     */     SQLException sqlEx;
/*     */     try
/*     */     {
/*  73 */       Class loggerClass = null;
/*     */       try
/*     */       {
/*  76 */         loggerClass = Class.forName(className);
/*     */       } catch (ClassNotFoundException nfe) {
/*  78 */         loggerClass = Class.forName(Log.class.getPackage().getName() + "." + className);
/*     */       }
/*     */ 
/*  82 */       Constructor constructor = loggerClass.getConstructor(new Class[] { String.class });
/*     */ 
/*  85 */       return (Log)constructor.newInstance(new Object[] { instanceName });
/*     */     } catch (ClassNotFoundException cnfe) {
/*  87 */       SQLException sqlEx = SQLError.createSQLException("Unable to load class for logger '" + className + "'", "S1009", exceptionInterceptor);
/*     */ 
/*  90 */       sqlEx.initCause(cnfe);
/*     */ 
/*  92 */       throw sqlEx;
/*     */     } catch (NoSuchMethodException nsme) {
/*  94 */       SQLException sqlEx = SQLError.createSQLException("Logger class does not have a single-arg constructor that takes an instance name", "S1009", exceptionInterceptor);
/*     */ 
/*  98 */       sqlEx.initCause(nsme);
/*     */ 
/* 100 */       throw sqlEx;
/*     */     } catch (InstantiationException inse) {
/* 102 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
/*     */ 
/* 106 */       sqlEx.initCause(inse);
/*     */ 
/* 108 */       throw sqlEx;
/*     */     } catch (InvocationTargetException ite) {
/* 110 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
/*     */ 
/* 114 */       sqlEx.initCause(ite);
/*     */ 
/* 116 */       throw sqlEx;
/*     */     } catch (IllegalAccessException iae) {
/* 118 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', constructor not public", "S1009", exceptionInterceptor);
/*     */ 
/* 122 */       sqlEx.initCause(iae);
/*     */ 
/* 124 */       throw sqlEx;
/*     */     } catch (ClassCastException cce) {
/* 126 */       sqlEx = SQLError.createSQLException("Logger class '" + className + "' does not implement the '" + Log.class.getName() + "' interface", "S1009", exceptionInterceptor);
/*     */ 
/* 130 */       sqlEx.initCause(cce);
/*     */     }
/* 132 */     throw sqlEx;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.LogFactory
 * JD-Core Version:    0.6.0
 */