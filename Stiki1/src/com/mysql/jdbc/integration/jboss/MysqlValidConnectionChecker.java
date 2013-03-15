/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import org.jboss.resource.adapter.jdbc.ValidConnectionChecker;
/*    */ 
/*    */ public final class MysqlValidConnectionChecker
/*    */   implements ValidConnectionChecker, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8909421133577519177L;
/*    */ 
/*    */   public SQLException isValidConnection(Connection conn)
/*    */   {
/* 60 */     Statement pingStatement = null;
/*    */     try
/*    */     {
/* 63 */       pingStatement = conn.createStatement();
/*    */ 
/* 65 */       pingStatement.executeQuery("/* ping */ SELECT 1").close();
/*    */ 
/* 67 */       localObject1 = null;
/*    */     }
/*    */     catch (SQLException sqlEx)
/*    */     {
/*    */       Object localObject1;
/* 69 */       return sqlEx;
/*    */     } finally {
/* 71 */       if (pingStatement != null)
/*    */         try {
/* 73 */           pingStatement.close();
/*    */         }
/*    */         catch (SQLException sqlEx)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.integration.jboss.MysqlValidConnectionChecker
 * JD-Core Version:    0.6.0
 */