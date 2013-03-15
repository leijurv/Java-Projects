/*     */ package gui_menus;
/*     */ 
/*     */ import executables.stiki_frontend_driver;
/*     */ import gui_panels.gui_diff_panel;
/*     */ import gui_support.gui_globals;
/*     */ import gui_support.gui_settings;
/*     */ import gui_support.gui_settings.SETTINGS_BOOL;
/*     */ import gui_support.gui_settings.SETTINGS_INT;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JRadioButtonMenuItem;
/*     */ 
/*     */ public class gui_menu_options extends JMenu
/*     */   implements ActionListener
/*     */ {
/*     */   private stiki_frontend_driver parent;
/*     */   private JMenu submenu_browser_font;
/*     */   private JRadioButtonMenuItem browser_font_10;
/*     */   private JRadioButtonMenuItem browser_font_11;
/*     */   private JRadioButtonMenuItem browser_font_12;
/*     */   private JRadioButtonMenuItem browser_font_13;
/*     */   private JRadioButtonMenuItem browser_font_14;
/*     */   private JRadioButtonMenuItem browser_font_15;
/*     */   private JRadioButtonMenuItem browser_font_16;
/*     */   private JCheckBoxMenuItem xlink_cb;
/*     */   private JCheckBoxMenuItem https_cb;
/*     */   private JCheckBoxMenuItem dttr_cb;
/*     */ 
/*     */   public gui_menu_options(stiki_frontend_driver paramstiki_frontend_driver)
/*     */   {
/*  56 */     this.parent = paramstiki_frontend_driver;
/*     */ 
/*  59 */     setText("Options");
/*  60 */     setFont(gui_globals.PLAIN_NORMAL_FONT);
/*  61 */     setMnemonic(84);
/*     */ 
/*  64 */     initialize_subitems();
/*  65 */     add(this.submenu_browser_font);
/*  66 */     add(this.xlink_cb);
/*  67 */     add(this.https_cb);
/*  68 */     add(this.dttr_cb);
/*     */ 
/*  71 */     selected_browser_font(gui_settings.get_int_def(gui_settings.SETTINGS_INT.options_fontsize, gui_globals.DEFAULT_BROWSER_FONT.getSize()));
/*     */ 
/*  74 */     set_hyperlink_policy(gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.options_hyperlinks, paramstiki_frontend_driver.diff_browser.get_hyperlink_policy()));
/*     */ 
/*  77 */     set_https_policy(gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.options_https, false));
/*     */ 
/*  79 */     set_dttr_policy(gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.options_dttr, true));
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*  91 */     if (paramActionEvent.getSource().equals(this.xlink_cb))
/*  92 */       set_hyperlink_policy(this.xlink_cb.isSelected());
/*  93 */     else if (!paramActionEvent.getSource().equals(this.https_cb))
/*     */     {
/*  99 */       if (!paramActionEvent.getSource().equals(this.dttr_cb))
/*     */       {
/* 108 */         int i = -1;
/* 109 */         if (paramActionEvent.getSource().equals(this.browser_font_10))
/* 110 */           i = 10;
/* 111 */         else if (paramActionEvent.getSource().equals(this.browser_font_11))
/* 112 */           i = 11;
/* 113 */         else if (paramActionEvent.getSource().equals(this.browser_font_12))
/* 114 */           i = 12;
/* 115 */         else if (paramActionEvent.getSource().equals(this.browser_font_13))
/* 116 */           i = 13;
/* 117 */         else if (paramActionEvent.getSource().equals(this.browser_font_14))
/* 118 */           i = 14;
/* 119 */         else if (paramActionEvent.getSource().equals(this.browser_font_15))
/* 120 */           i = 15;
/* 121 */         else if (paramActionEvent.getSource().equals(this.browser_font_16)) {
/* 122 */           i = 16;
/*     */         }
/*     */ 
/* 125 */         selected_browser_font(i);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int get_browser_fontsize()
/*     */   {
/* 137 */     if (this.browser_font_10.isSelected()) return 10;
/* 138 */     if (this.browser_font_11.isSelected()) return 11;
/* 139 */     if (this.browser_font_12.isSelected()) return 12;
/* 140 */     if (this.browser_font_13.isSelected()) return 13;
/* 141 */     if (this.browser_font_14.isSelected()) return 14;
/* 142 */     if (this.browser_font_15.isSelected()) return 15;
/* 143 */     if (this.browser_font_16.isSelected()) return 16;
/* 144 */     return gui_globals.DEFAULT_BROWSER_FONT.getSize();
/*     */   }
/*     */ 
/*     */   public void set_hyperlink_policy(boolean paramBoolean)
/*     */   {
/* 153 */     this.xlink_cb.setSelected(paramBoolean);
/* 154 */     this.parent.diff_browser.set_hyperlink_policy(paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean get_hyperlink_policy()
/*     */   {
/* 162 */     return this.xlink_cb.isSelected();
/*     */   }
/*     */ 
/*     */   public void set_https_policy(boolean paramBoolean)
/*     */   {
/* 171 */     this.https_cb.setSelected(paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean get_https_policy()
/*     */   {
/* 179 */     return this.https_cb.isSelected();
/*     */   }
/*     */ 
/*     */   public void set_dttr_policy(boolean paramBoolean)
/*     */   {
/* 188 */     this.dttr_cb.setSelected(paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean get_dttr_policy()
/*     */   {
/* 196 */     return this.dttr_cb.isSelected();
/*     */   }
/*     */ 
/*     */   private void initialize_subitems()
/*     */   {
/* 208 */     this.submenu_browser_font = new JMenu("Browser Font Size");
/* 209 */     this.submenu_browser_font.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 210 */     this.submenu_browser_font.setMnemonic(70);
/*     */ 
/* 213 */     this.submenu_browser_font.add(this.browser_font_10 = create_rb_item("10 point", 49));
/*     */ 
/* 215 */     this.submenu_browser_font.add(this.browser_font_11 = create_rb_item("11 point", 49));
/*     */ 
/* 217 */     this.submenu_browser_font.add(this.browser_font_12 = create_rb_item("12 point", 50));
/*     */ 
/* 219 */     this.submenu_browser_font.add(this.browser_font_13 = create_rb_item("13 point", 51));
/*     */ 
/* 221 */     this.submenu_browser_font.add(this.browser_font_14 = create_rb_item("14 point", 52));
/*     */ 
/* 223 */     this.submenu_browser_font.add(this.browser_font_15 = create_rb_item("15 point", 53));
/*     */ 
/* 225 */     this.submenu_browser_font.add(this.browser_font_16 = create_rb_item("16 point", 54));
/*     */ 
/* 229 */     this.xlink_cb = create_cb_item("Activate Ext-Links", 88);
/*     */ 
/* 232 */     this.https_cb = create_cb_item("Use HTTPS (restart reqd.)", 72);
/*     */ 
/* 235 */     this.dttr_cb = create_cb_item("Warn if templating regular", 87);
/*     */   }
/*     */ 
/*     */   private void selected_browser_font(int paramInt)
/*     */   {
/* 245 */     this.browser_font_10.setSelected(false);
/* 246 */     this.browser_font_11.setSelected(false);
/* 247 */     this.browser_font_12.setSelected(false);
/* 248 */     this.browser_font_13.setSelected(false);
/* 249 */     this.browser_font_14.setSelected(false);
/* 250 */     this.browser_font_15.setSelected(false);
/* 251 */     this.browser_font_16.setSelected(false);
/*     */ 
/* 253 */     switch (paramInt) { case 10:
/* 254 */       this.browser_font_10.setSelected(true); break;
/*     */     case 11:
/* 255 */       this.browser_font_11.setSelected(true); break;
/*     */     case 12:
/* 256 */       this.browser_font_12.setSelected(true); break;
/*     */     case 13:
/* 257 */       this.browser_font_13.setSelected(true); break;
/*     */     case 14:
/* 258 */       this.browser_font_14.setSelected(true); break;
/*     */     case 15:
/* 259 */       this.browser_font_15.setSelected(true); break;
/*     */     case 16:
/* 260 */       this.browser_font_16.setSelected(true);
/*     */     }
/*     */ 
/* 264 */     Font localFont = new Font(gui_globals.DEFAULT_BROWSER_FONT.getName(), gui_globals.DEFAULT_BROWSER_FONT.getStyle(), paramInt);
/*     */ 
/* 266 */     this.parent.diff_browser.change_browser_font(localFont);
/*     */   }
/*     */ 
/*     */   private JRadioButtonMenuItem create_rb_item(String paramString, int paramInt)
/*     */   {
/* 279 */     JRadioButtonMenuItem localJRadioButtonMenuItem = new JRadioButtonMenuItem(paramString);
/* 280 */     localJRadioButtonMenuItem.setMnemonic(paramInt);
/* 281 */     localJRadioButtonMenuItem.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 282 */     localJRadioButtonMenuItem.addActionListener(this);
/* 283 */     return localJRadioButtonMenuItem;
/*     */   }
/*     */ 
/*     */   private JCheckBoxMenuItem create_cb_item(String paramString, int paramInt)
/*     */   {
/* 293 */     JCheckBoxMenuItem localJCheckBoxMenuItem = new JCheckBoxMenuItem(paramString);
/* 294 */     localJCheckBoxMenuItem.setMnemonic(paramInt);
/* 295 */     localJCheckBoxMenuItem.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 296 */     localJCheckBoxMenuItem.addActionListener(this);
/* 297 */     return localJCheckBoxMenuItem;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_menu_options
 * JD-Core Version:    0.6.0
 */