/*    */ package gui_menus;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import javax.swing.JEditorPane;
/*    */ import javax.swing.JMenuItem;
/*    */ import javax.swing.JPopupMenu;
/*    */ 
/*    */ public class gui_text_menu extends MouseAdapter
/*    */   implements ActionListener
/*    */ {
/*    */   private JPopupMenu menu;
/*    */   private JMenuItem item_copy;
/*    */   private JEditorPane parent;
/*    */ 
/*    */   public gui_text_menu(JEditorPane paramJEditorPane)
/*    */   {
/* 46 */     this.parent = paramJEditorPane;
/* 47 */     this.item_copy = gui_menu_bar.create_menu_item("Copy", 67);
/* 48 */     this.menu = new JPopupMenu();
/* 49 */     this.menu.add(this.item_copy);
/* 50 */     this.item_copy.addActionListener(this);
/*    */   }
/*    */ 
/*    */   public void mouseReleased(MouseEvent paramMouseEvent)
/*    */   {
/* 60 */     show_popup(paramMouseEvent);
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent paramMouseEvent)
/*    */   {
/* 67 */     show_popup(paramMouseEvent);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent paramActionEvent)
/*    */   {
/* 75 */     if (paramActionEvent.getSource().equals(this.item_copy))
/* 76 */       this.parent.copy();
/*    */   }
/*    */ 
/*    */   private void show_popup(MouseEvent paramMouseEvent)
/*    */   {
/* 90 */     if ((this.menu != null) && (paramMouseEvent.isPopupTrigger()) && (this.parent.getSelectedText() != null) && (!this.parent.getSelectedText().isEmpty()))
/*    */     {
/* 93 */       this.menu.show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_text_menu
 * JD-Core Version:    0.6.0
 */