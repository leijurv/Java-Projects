/*     */ package gui_menus;
/*     */ 
/*     */ import core_objects.stiki_utils.QUEUE_TYPE;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import executables.stiki_frontend_driver;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.Component;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ 
/*     */ public class gui_menu_bar extends JMenuBar
/*     */ {
/*     */   private JMenu file_menu;
/*     */   private gui_menu_queue queue_menu;
/*     */   private JMenu filter_menu;
/*     */   private gui_menu_options options_menu;
/*     */   private JMenu help_menu;
/*     */   private JMenu about_menu;
/*     */ 
/*     */   public gui_menu_bar(stiki_frontend_driver paramstiki_frontend_driver, stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */   {
/*  75 */     this.filter_menu = create_top_menu("Revision Filters", 82);
/*  76 */     build_filter_menu();
/*     */ 
/*  79 */     this.file_menu = new gui_menu_file(paramstiki_frontend_driver);
/*  80 */     this.queue_menu = new gui_menu_queue(paramstiki_frontend_driver, paramSCORE_SYS);
/*  81 */     this.options_menu = new gui_menu_options(paramstiki_frontend_driver);
/*  82 */     this.help_menu = new gui_menu_help();
/*  83 */     this.about_menu = new gui_menu_about();
/*     */ 
/*  86 */     setLayout(new BoxLayout(this, 0));
/*  87 */     add(horiz_menubar_spacer());
/*  88 */     add(this.file_menu);
/*  89 */     add(horiz_menubar_spacer());
/*  90 */     add(gui_globals.create_vert_separator());
/*  91 */     add(horiz_menubar_spacer());
/*  92 */     add(this.queue_menu);
/*  93 */     add(horiz_menubar_spacer());
/*  94 */     add(gui_globals.create_vert_separator());
/*  95 */     add(horiz_menubar_spacer());
/*  96 */     add(this.filter_menu);
/*  97 */     add(horiz_menubar_spacer());
/*  98 */     add(gui_globals.create_vert_separator());
/*  99 */     add(horiz_menubar_spacer());
/* 100 */     add(this.options_menu);
/* 101 */     add(horiz_menubar_spacer());
/* 102 */     add(gui_globals.create_vert_separator());
/* 103 */     add(horiz_menubar_spacer());
/* 104 */     add(this.help_menu);
/* 105 */     add(horiz_menubar_spacer());
/* 106 */     add(gui_globals.create_vert_separator());
/* 107 */     add(horiz_menubar_spacer());
/* 108 */     add(this.about_menu);
/* 109 */     add(horiz_menubar_spacer());
/* 110 */     add(gui_globals.create_vert_separator());
/* 111 */     add(Box.createHorizontalGlue());
/*     */   }
/*     */ 
/*     */   public static JMenu create_top_menu(String paramString, int paramInt)
/*     */   {
/* 123 */     JMenu localJMenu = new JMenu(paramString);
/* 124 */     localJMenu.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 125 */     localJMenu.setMnemonic(paramInt);
/* 126 */     return localJMenu;
/*     */   }
/*     */ 
/*     */   public static JMenuItem create_menu_item(String paramString, int paramInt)
/*     */   {
/* 136 */     JMenuItem localJMenuItem = new JMenuItem(paramString);
/* 137 */     localJMenuItem.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 138 */     localJMenuItem.setMnemonic(paramInt);
/* 139 */     return localJMenuItem;
/*     */   }
/*     */ 
/*     */   public stiki_utils.SCORE_SYS selected_queue()
/*     */   {
/* 147 */     return this.queue_menu.selected_queue();
/*     */   }
/*     */ 
/*     */   public stiki_utils.QUEUE_TYPE selected_type()
/*     */   {
/* 155 */     return this.queue_menu.selected_type();
/*     */   }
/*     */ 
/*     */   public gui_menu_options get_options_menu()
/*     */   {
/* 164 */     return this.options_menu;
/*     */   }
/*     */ 
/*     */   private void build_filter_menu()
/*     */   {
/* 174 */     this.filter_menu.add(gui_globals.checkbox_item("Namespace-Zero (NS0)", 90, false, true));
/*     */ 
/* 176 */     this.filter_menu.add(gui_globals.checkbox_item("Anonymous User Edits", 65, false, true));
/*     */ 
/* 178 */     this.filter_menu.add(gui_globals.checkbox_item("Registered User Edits", 82, false, true));
/*     */ 
/* 180 */     this.filter_menu.add(gui_globals.checkbox_item("Only Most Recent on Page", 77, false, true));
/*     */   }
/*     */ 
/*     */   private Component horiz_menubar_spacer()
/*     */   {
/* 188 */     return Box.createHorizontalStrut(10);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_menu_bar
 * JD-Core Version:    0.6.0
 */