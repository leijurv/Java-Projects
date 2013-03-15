/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
/*     */ 
/*     */ abstract class WrapperBase
/*     */ {
/*     */   protected MysqlPooledConnection pooledConnection;
/*  71 */   protected Map unwrappedInterfaces = null;
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   protected void checkAndFireConnectionError(SQLException sqlEx)
/*     */     throws SQLException
/*     */   {
/*  60 */     if ((this.pooledConnection != null) && 
/*  61 */       ("08S01".equals(sqlEx.getSQLState())))
/*     */     {
/*  63 */       this.pooledConnection.callConnectionEventListeners(1, sqlEx);
/*     */     }
/*     */ 
/*  68 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   protected WrapperBase(MysqlPooledConnection pooledConnection)
/*     */   {
/*  75 */     this.pooledConnection = pooledConnection;
/*  76 */     this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
/*     */   }
/*     */ 
/*     */   protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler {
/*  80 */     Object invokeOn = null;
/*     */ 
/*     */     public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
/*  83 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */ 
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */     {
/*  88 */       Object result = null;
/*     */       try
/*     */       {
/*  91 */         result = method.invoke(this.invokeOn, args);
/*     */ 
/*  93 */         if (result != null)
/*  94 */           result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/*  98 */         if ((e.getTargetException() instanceof SQLException)) {
/*  99 */           WrapperBase.this.checkAndFireConnectionError((SQLException)e.getTargetException());
/*     */         }
/*     */         else {
/* 102 */           throw e;
/*     */         }
/*     */       }
/*     */ 
/* 106 */       return result;
/*     */     }
/*     */ 
/*     */     private Object proxyIfInterfaceIsJdbc(Object toProxy, Class clazz)
/*     */     {
/* 118 */       Class[] interfaces = clazz.getInterfaces();
/*     */ 
/* 120 */       int i = 0; if (i < interfaces.length) {
/* 121 */         String packageName = interfaces[i].getPackage().getName();
/*     */ 
/* 123 */         if (("java.sql".equals(packageName)) || ("javax.sql".equals(packageName)))
/*     */         {
/* 125 */           return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(WrapperBase.this, toProxy));
/*     */         }
/*     */ 
/* 130 */         return proxyIfInterfaceIsJdbc(toProxy, interfaces[i]);
/*     */       }
/*     */ 
/* 133 */       return toProxy;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.WrapperBase
 * JD-Core Version:    0.6.0
 */