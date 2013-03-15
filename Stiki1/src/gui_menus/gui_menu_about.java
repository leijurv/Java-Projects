/*     */ package gui_menus;
/*     */ 
/*     */ import gui_support.gui_filesys_images;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class gui_menu_about extends JMenu
/*     */   implements ActionListener
/*     */ {
/*     */   private static final int ABOUT_HEIGHT = 350;
/*     */   private static final int ABOUT_WIDTH = 200;
/*     */   private JMenuItem item_about;
/*     */   private JMenuItem item_website;
/*     */ 
/*     */   public gui_menu_about()
/*     */   {
/*  62 */     setText("About STiki");
/*  63 */     setFont(gui_globals.PLAIN_NORMAL_FONT);
/*  64 */     setMnemonic(83);
/*     */ 
/*  67 */     this.item_about = gui_menu_bar.create_menu_item("About STiki", 65);
/*     */ 
/*  69 */     this.item_about.addActionListener(this);
/*  70 */     this.item_website = gui_menu_bar.create_menu_item("Visit Website", 87);
/*     */ 
/*  72 */     this.item_website.addActionListener(this);
/*     */ 
/*  75 */     add(this.item_about);
/*  76 */     add(this.item_website);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*  86 */     if (paramActionEvent.getSource().equals(this.item_about)) {
/*     */       try { open_about_panel(); } catch (Exception localException) {
/*     */       }
/*  89 */     } else if (paramActionEvent.getSource().equals(this.item_website)) {
/*  90 */       String str = "http://en.wikipedia.org/wiki/Wikipedia:STiki";
/*  91 */       gui_globals.open_url(this, str);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void open_about_panel()
/*     */     throws Exception
/*     */   {
/* 104 */     JFrame localJFrame = new JFrame();
/* 105 */     Dimension localDimension = localJFrame.getToolkit().getScreenSize();
/* 106 */     int i = (localDimension.width - 200) / 2;
/* 107 */     int j = (localDimension.height - 350) / 2;
/* 108 */     localJFrame.setBounds(i, j, 200, 350);
/* 109 */     localJFrame.setTitle("About STiki");
/* 110 */     localJFrame.setIconImage(gui_filesys_images.ICON_64);
/*     */ 
/* 113 */     Image localImage = gui_filesys_images.ICON_128;
/* 114 */     JLabel localJLabel1 = new JLabel(new ImageIcon(localImage));
/* 115 */     localJLabel1.setBorder(BorderFactory.createBevelBorder(0));
/*     */ 
/* 118 */     JLabel localJLabel2 = gui_globals.plain_centered_multiline_label("Version 2.1");
/*     */ 
/* 120 */     JLabel localJLabel3 = gui_globals.plain_centered_multiline_label("by Andrew G. West<BR>westand@cis.upenn.edu");
/*     */ 
/* 122 */     JLabel localJLabel4 = gui_globals.plain_centered_multiline_label("Copyright&#169 2010-12");
/*     */ 
/* 124 */     JLabel localJLabel5 = gui_globals.plain_centered_multiline_label("Development of STiki was supported in part by ONR MURI N00014-07-1-0907");
/*     */ 
/* 130 */     JPanel localJPanel = new JPanel();
/* 131 */     localJPanel.setLayout(new BoxLayout(localJPanel, 1));
/* 132 */     localJPanel.add(Box.createVerticalGlue());
/* 133 */     localJPanel.add(gui_globals.center_comp_with_glue(localJLabel1));
/* 134 */     localJPanel.add(Box.createVerticalGlue());
/* 135 */     localJPanel.add(gui_globals.center_comp_with_glue(localJLabel2));
/* 136 */     localJPanel.add(Box.createVerticalGlue());
/* 137 */     localJPanel.add(gui_globals.center_comp_with_glue(localJLabel3));
/* 138 */     localJPanel.add(Box.createVerticalGlue());
/* 139 */     localJPanel.add(gui_globals.center_comp_with_glue(localJLabel4));
/* 140 */     localJPanel.add(Box.createVerticalGlue());
/* 141 */     localJPanel.add(gui_globals.center_comp_with_glue(localJLabel5));
/* 142 */     localJPanel.add(Box.createVerticalGlue());
/* 143 */     localJPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
/*     */ 
/* 147 */     localJFrame.add(localJPanel);
/* 148 */     localJFrame.setVisible(true);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_menu_about
 * JD-Core Version:    0.6.0
 */