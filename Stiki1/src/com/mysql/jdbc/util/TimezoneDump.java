/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import com.mysql.jdbc.TimeUtil;
/*    */ import java.io.PrintStream;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class TimezoneDump
/*    */ {
/*    */   private static final String DEFAULT_URL = "jdbc:mysql:///test";
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 69 */     String jdbcUrl = "jdbc:mysql:///test";
/*    */ 
/* 71 */     if ((args.length == 1) && (args[0] != null)) {
/* 72 */       jdbcUrl = args[0];
/*    */     }
/*    */ 
/* 75 */     Class.forName("com.mysql.jdbc.Driver").newInstance();
/*    */ 
/* 77 */     ResultSet rs = DriverManager.getConnection(jdbcUrl).createStatement().executeQuery("SHOW VARIABLES LIKE 'timezone'");
/*    */ 
/* 80 */     while (rs.next()) {
/* 81 */       String timezoneFromServer = rs.getString(2);
/* 82 */       System.out.println("MySQL timezone name: " + timezoneFromServer);
/*    */ 
/* 84 */       String canonicalTimezone = TimeUtil.getCanoncialTimezone(timezoneFromServer, null);
/*    */ 
/* 86 */       System.out.println("Java timezone name: " + canonicalTimezone);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.TimezoneDump
 * JD-Core Version:    0.6.0
 */