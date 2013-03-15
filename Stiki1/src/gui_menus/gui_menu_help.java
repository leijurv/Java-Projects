/*     */ package gui_menus;
/*     */ 
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class gui_menu_help extends JMenu
/*     */   implements ActionListener
/*     */ {
/*     */   private JMenuItem item_full;
/*     */   private JMenuItem item_queue;
/*     */   private JMenuItem item_stiki_s;
/*     */   private JMenuItem item_filters;
/*     */   private JMenuItem item_browser;
/*     */   private JMenuItem item_metadata;
/*     */   private JMenuItem item_class;
/*     */   private JMenuItem item_login;
/*     */   private JMenuItem item_comment;
/*     */   private JMenuItem item_lastrv;
/*     */   private JMenuItem item_pform;
/*     */   private JMenuItem item_ort;
/*     */ 
/*     */   public gui_menu_help()
/*     */   {
/*  49 */     setText("Help");
/*  50 */     setFont(gui_globals.PLAIN_NORMAL_FONT);
/*  51 */     setMnemonic(72);
/*     */ 
/*  54 */     initialize_menu_items();
/*     */ 
/*  57 */     add(this.item_full);
/*  58 */     addSeparator();
/*  59 */     add(this.item_queue);
/*  60 */     add(this.item_stiki_s);
/*  61 */     add(this.item_filters);
/*  62 */     add(this.item_browser);
/*  63 */     add(this.item_metadata);
/*  64 */     add(this.item_class);
/*  65 */     add(this.item_login);
/*  66 */     add(this.item_comment);
/*  67 */     add(this.item_lastrv);
/*  68 */     add(this.item_pform);
/*  69 */     add(this.item_ort);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*     */     try
/*     */     {
/*  83 */       if (paramActionEvent.getSource().equals(this.item_full))
/*  84 */         gui_help_doc.show_help(this, "sec_top");
/*  85 */       else if (paramActionEvent.getSource().equals(this.item_queue))
/*  86 */         gui_help_doc.show_help(this, "sec_queue");
/*  87 */       else if (paramActionEvent.getSource().equals(this.item_stiki_s))
/*  88 */         gui_help_doc.show_help(this, "sec_stiki_scoring");
/*  89 */       else if (paramActionEvent.getSource().equals(this.item_filters))
/*  90 */         gui_help_doc.show_help(this, "sec_filter");
/*  91 */       else if (paramActionEvent.getSource().equals(this.item_browser))
/*  92 */         gui_help_doc.show_help(this, "sec_browser");
/*  93 */       else if (paramActionEvent.getSource().equals(this.item_metadata))
/*  94 */         gui_help_doc.show_help(this, "sec_metadata");
/*  95 */       else if (paramActionEvent.getSource().equals(this.item_class))
/*  96 */         gui_help_doc.show_help(this, "sec_class");
/*  97 */       else if (paramActionEvent.getSource().equals(this.item_login))
/*  98 */         gui_help_doc.show_help(this, "sec_login");
/*  99 */       else if (paramActionEvent.getSource().equals(this.item_comment))
/* 100 */         gui_help_doc.show_help(this, "sec_comment");
/* 101 */       else if (paramActionEvent.getSource().equals(this.item_lastrv))
/* 102 */         gui_help_doc.show_help(this, "sec_lastrevert");
/* 103 */       else if (paramActionEvent.getSource().equals(this.item_pform))
/* 104 */         gui_help_doc.show_help(this, "sec_performance");
/* 105 */       else if (paramActionEvent.getSource().equals(this.item_ort))
/* 106 */         gui_help_doc.show_help(this, "sec_ort");
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 110 */       JOptionPane.showMessageDialog(this, "Help file cannot be opened\nPlease consult on-line documentation", "Error: Help file inaccessible", 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initialize_menu_items()
/*     */   {
/* 126 */     this.item_full = gui_menu_bar.create_menu_item("Show All Help ...", 72);
/*     */ 
/* 128 */     this.item_queue = gui_menu_bar.create_menu_item("Help: Revision Queues", 81);
/*     */ 
/* 130 */     this.item_stiki_s = gui_menu_bar.create_menu_item("Help: Metadata Scoring", 83);
/*     */ 
/* 132 */     this.item_filters = gui_menu_bar.create_menu_item("Help: Revision Filters", 70);
/*     */ 
/* 134 */     this.item_browser = gui_menu_bar.create_menu_item("Help: Diff Browser", 68);
/*     */ 
/* 136 */     this.item_metadata = gui_menu_bar.create_menu_item("Help: Edit Properties", 69);
/*     */ 
/* 138 */     this.item_class = gui_menu_bar.create_menu_item("Help: Classification", 67);
/*     */ 
/* 140 */     this.item_login = gui_menu_bar.create_menu_item("Help: Login Panel", 76);
/*     */ 
/* 142 */     this.item_comment = gui_menu_bar.create_menu_item("Help: Reversion Comment", 82);
/*     */ 
/* 144 */     this.item_lastrv = gui_menu_bar.create_menu_item("Help: Last Revert Panel", 76);
/*     */ 
/* 146 */     this.item_pform = gui_menu_bar.create_menu_item("Help: STiki Performance", 80);
/*     */ 
/* 148 */     this.item_ort = gui_menu_bar.create_menu_item("Help: Offline Review Tool", 79);
/*     */ 
/* 151 */     this.item_full.addActionListener(this);
/* 152 */     this.item_queue.addActionListener(this);
/* 153 */     this.item_stiki_s.addActionListener(this);
/* 154 */     this.item_filters.addActionListener(this);
/* 155 */     this.item_browser.addActionListener(this);
/* 156 */     this.item_metadata.addActionListener(this);
/* 157 */     this.item_class.addActionListener(this);
/* 158 */     this.item_login.addActionListener(this);
/* 159 */     this.item_comment.addActionListener(this);
/* 160 */     this.item_lastrv.addActionListener(this);
/* 161 */     this.item_pform.addActionListener(this);
/* 162 */     this.item_ort.addActionListener(this);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_menu_help
 * JD-Core Version:    0.6.0
 */