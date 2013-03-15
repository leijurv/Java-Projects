/*    */ package gui_menus;
/*    */ 
/*    */ import executables.offline_review_driver;
/*    */ import executables.stiki_frontend_driver;
/*    */ import gui_support.gui_globals;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.io.PrintStream;
/*    */ import javax.swing.JMenu;
/*    */ import javax.swing.JMenuItem;
/*    */ 
/*    */ public class gui_menu_file extends JMenu
/*    */   implements ActionListener
/*    */ {
/*    */   private stiki_frontend_driver parent;
/*    */   private JMenuItem item_ort;
/*    */   private JMenuItem item_close;
/*    */ 
/*    */   public gui_menu_file(stiki_frontend_driver paramstiki_frontend_driver)
/*    */   {
/* 48 */     this.parent = paramstiki_frontend_driver;
/*    */ 
/* 51 */     setText("File");
/* 52 */     setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 53 */     setMnemonic(70);
/*    */ 
/* 56 */     this.item_ort = gui_menu_bar.create_menu_item("Launch ORT", 76);
/*    */ 
/* 58 */     this.item_ort.addActionListener(this);
/* 59 */     this.item_close = gui_menu_bar.create_menu_item("Close STiki", 67);
/*    */ 
/* 61 */     this.item_close.addActionListener(this);
/*    */ 
/* 64 */     add(this.item_ort);
/* 65 */     add(this.item_close);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent paramActionEvent)
/*    */   {
/* 75 */     if (paramActionEvent.getSource().equals(this.item_ort)) {
/* 76 */       this.parent.exit_handler(false);
/*    */       try { new offline_review_driver();
/*    */       } catch (Exception localException) {
/* 79 */         System.out.println("Error in launching STiki-ORT:");
/* 80 */         localException.printStackTrace();
/*    */       }
/* 82 */     } else if (paramActionEvent.getSource().equals(this.item_close)) {
/* 83 */       this.parent.exit_handler(true);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_menu_file
 * JD-Core Version:    0.6.0
 */