/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionPropertiesImpl;
/*    */ import java.io.PrintStream;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class PropertiesDocGenerator extends ConnectionPropertiesImpl
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws SQLException
/*    */   {
/* 41 */     System.out.println(new PropertiesDocGenerator().exposeAsXml());
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.PropertiesDocGenerator
 * JD-Core Version:    0.6.0
 */