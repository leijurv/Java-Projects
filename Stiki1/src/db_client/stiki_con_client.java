/*    */ package db_client;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ 
/*    */ public class stiki_con_client
/*    */ {
/*    */   public final Connection con;
/*    */   private static final String CON_STR = "jdbc:mysql://armstrong.cis.upenn.edu:3306/presta_stiki?user=#&password=#";
/*    */ 
/*    */   public stiki_con_client()
/*    */   {
/* 55 */     this.con = get_con();
/*    */   }
/*    */ 
/*    */   private static Connection get_con()
/*    */   {
/* 65 */     String str = "";
/* 66 */     Connection localConnection = null;
/*    */     try { str = "jdbc:mysql://armstrong.cis.upenn.edu:3306/presta_stiki?user=#&password=#";
/* 68 */       str = str.replaceFirst("#", "stiki_client");
/* 69 */       str = str.replaceFirst("#", "offtoaivyougo");
/* 70 */       str = str + "&noAccessToProcedureBodies=true";
/* 71 */       Class.forName("com.mysql.jdbc.Driver").newInstance();
/* 72 */       localConnection = DriverManager.getConnection(str);
/*    */     } catch (Exception localException) {
/* 74 */       System.err.println("Error opening DB connection");
/* 75 */       localException.printStackTrace();
/*    */     }
/* 77 */     return localConnection;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     db_client.stiki_con_client
 * JD-Core Version:    0.6.0
 */