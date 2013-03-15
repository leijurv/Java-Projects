/*    */ package gui_support;
/*    */ 
/*    */ import db_client.client_interface;
/*    */ import executables.stiki_frontend_driver;
/*    */ 
/*    */ public class gui_ping_server
/*    */   implements Runnable
/*    */ {
/*    */   public static final int PING_INTERVAL_MS = 5000;
/*    */   private stiki_frontend_driver parent;
/*    */ 
/*    */   public gui_ping_server(stiki_frontend_driver paramstiki_frontend_driver)
/*    */   {
/* 39 */     this.parent = paramstiki_frontend_driver;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     while (true)
/*    */       try
/*    */       {
/* 51 */         this.parent.client_interface.ping();
/* 52 */         Thread.sleep(5000L);
/*    */ 
/* 56 */         continue;
/*    */       }
/*    */       catch (Exception localException1)
/*    */       {
/*    */         try
/*    */         {
/* 54 */           this.parent.reset_connection(false);
/*    */         }
/*    */         catch (Exception localException2)
/*    */         {
/*    */         }
/*    */       }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_ping_server
 * JD-Core Version:    0.6.0
 */