/*     */ package executables;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.pair;
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.QUEUE_TYPE;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import db_client.client_interface;
/*     */ import db_client.qmanager_client;
/*     */ import db_client.stiki_con_client;
/*     */ import gui_edit_queue.edit_queue;
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import gui_menus.gui_menu_bar;
/*     */ import gui_menus.gui_menu_options;
/*     */ import gui_panels.gui_button_panel;
/*     */ import gui_panels.gui_comment_panel;
/*     */ import gui_panels.gui_comment_panel.COMMENT_TAB;
/*     */ import gui_panels.gui_diff_panel;
/*     */ import gui_panels.gui_login_panel;
/*     */ import gui_panels.gui_metadata_panel;
/*     */ import gui_panels.gui_revert_panel;
/*     */ import gui_support.gui_fb_handler;
/*     */ import gui_support.gui_filesys_images;
/*     */ import gui_support.gui_globals;
/*     */ import gui_support.gui_ping_server;
/*     */ import gui_support.gui_settings;
/*     */ import gui_support.gui_settings.SETTINGS_INT;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class stiki_frontend_driver extends JFrame
/*     */ {
/*     */   public gui_diff_panel diff_browser;
/*     */   public gui_login_panel login_panel;
/*     */   public gui_metadata_panel metadata_panel;
/*     */   public gui_button_panel button_panel;
/*     */   public gui_comment_panel comment_panel;
/*     */   public gui_menu_bar menu_bar;
/*     */   public gui_revert_panel revert_panel;
/*     */   public client_interface client_interface;
/*     */   public edit_queue edit_queue;
/*     */   private static final int NUM_NON_GUI_THREADS = 10;
/*     */   private static ExecutorService WORKER_THREADS;
/*     */   private static stiki_utils.QUEUE_TYPE last_q_type;
/* 139 */   private static pair<FB_TYPE, Long> last_classification = new pair(FB_TYPE.PASS, Long.valueOf(0L));
/*     */ 
/* 147 */   private static boolean secondary_review = false;
/*     */   private static int passes_in_career;
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */     throws Exception
/*     */   {
/* 168 */     client_interface localclient_interface = new client_interface();
/* 169 */     if (localclient_interface.con_client.con != null) {
/* 170 */       new stiki_frontend_driver(localclient_interface);
/*     */     } else {
/* 172 */       JFrame localJFrame = new JFrame();
/* 173 */       localJFrame.setIconImage(gui_filesys_images.ICON_64);
/* 174 */       JOptionPane.showMessageDialog(localJFrame, "Unable to connect to the STiki back-end:\nThis is likely the result of one of four things:\n\n(1) You are not connected to the Internet.\n\n(2) Port 3306 is not open (MySQL), due to a\n    firewall or your network's admin. settings\n\n(3) The STiki server is down. Check [[WP:STiki]]\n\n(4) A required software upgrade has been issued,\n    breaking this version. See [[WP:STiki]]\n\n", "Error: Backend connection is required", 0);
/*     */ 
/* 185 */       System.exit(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public stiki_frontend_driver(client_interface paramclient_interface)
/*     */     throws Exception
/*     */   {
/* 202 */     gui_settings.set_parent(this);
/* 203 */     gui_settings.load_properties();
/* 204 */     passes_in_career = gui_settings.get_int_def(gui_settings.SETTINGS_INT.passes_used, 0);
/*     */ 
/* 208 */     this.client_interface = paramclient_interface;
/*     */ 
/* 211 */     stiki_utils.SCORE_SYS localSCORE_SYS = this.client_interface.default_queue();
/* 212 */     stiki_utils.QUEUE_TYPE localQUEUE_TYPE = stiki_utils.queue_to_type(localSCORE_SYS);
/* 213 */     last_q_type = localQUEUE_TYPE;
/* 214 */     WORKER_THREADS = Executors.newFixedThreadPool(10);
/* 215 */     WORKER_THREADS.submit(new gui_ping_server(this));
/* 216 */     this.edit_queue = new edit_queue(this, WORKER_THREADS, this.client_interface, localSCORE_SYS);
/*     */ 
/* 222 */     this.button_panel = new gui_button_panel(this, localQUEUE_TYPE);
/* 223 */     this.metadata_panel = new gui_metadata_panel();
/* 224 */     this.revert_panel = new gui_revert_panel();
/* 225 */     this.login_panel = new gui_login_panel(this);
/* 226 */     this.comment_panel = new gui_comment_panel(localQUEUE_TYPE);
/* 227 */     this.diff_browser = new gui_diff_panel();
/* 228 */     this.menu_bar = new gui_menu_bar(this, localSCORE_SYS);
/* 229 */     setJMenuBar(this.menu_bar);
/*     */ 
/* 232 */     JPanel localJPanel = new JPanel(new BorderLayout(0, 0));
/* 233 */     this.diff_browser.setBorder(gui_globals.produce_titled_border("DIFF-Browser"));
/*     */ 
/* 235 */     localJPanel.add(this.diff_browser, "Center");
/* 236 */     localJPanel.add(initialize_bottom_panel(), "South");
/*     */ 
/* 239 */     getContentPane().add(create_left_panel(), "West");
/* 240 */     getContentPane().add(localJPanel, "Center");
/* 241 */     window_size_position();
/*     */ 
/* 244 */     setTitle("STiki: A Vandalism Detection Tool for Wikipedia");
/* 245 */     setIconImage(gui_filesys_images.ICON_64);
/* 246 */     setVisible(true);
/* 247 */     setDefaultCloseOperation(0);
/* 248 */     addWindowListener(get_exit_handler());
/* 249 */     advance_revision(false);
/*     */   }
/*     */ 
/*     */   public void exit_handler(boolean paramBoolean)
/*     */   {
/* 262 */     gui_settings.save_properties();
/*     */     try
/*     */     {
/* 265 */       WORKER_THREADS.shutdown();
/* 266 */       this.edit_queue.shutdown();
/* 267 */       this.client_interface.shutdown();
/* 268 */       WORKER_THREADS.shutdownNow();
/*     */     } catch (Exception localException) {
/* 270 */       System.out.println("Error during STiki shutdown:");
/* 271 */       localException.printStackTrace();
/*     */     }
/*     */ 
/* 274 */     if (paramBoolean)
/* 275 */       System.exit(0);
/* 276 */     else setVisible(false);
/*     */   }
/*     */ 
/*     */   public void reset_connection(boolean paramBoolean)
/*     */     throws Exception
/*     */   {
/* 289 */     this.client_interface = new client_interface();
/* 290 */     if (this.client_interface.con_client.con == null) {
/* 291 */       JOptionPane.showMessageDialog(this, "Unable to connect to the STiki back-end:\nThe program will now exit. Try to restart STiki.\nMore information will be given if that fails.", "Error: Back-end connectivity is required", 0);
/*     */ 
/* 297 */       System.exit(1);
/* 298 */     } else if (paramBoolean) {
/* 299 */       JOptionPane.showMessageDialog(this, "Your connection to the STiki database was\nlost. A new connection has been obtained.", "Warning: Connection was reset", 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void class_action(FB_TYPE paramFB_TYPE)
/*     */     throws Exception
/*     */   {
/* 312 */     if (!this.login_panel.is_state_stable()) {
/* 313 */       JOptionPane.showMessageDialog(this, "To classify using STiki, you must either log-in\nor choose to edit as an anonymous user.", "Warning: Must set user-mode", 2);
/*     */ 
/* 318 */       return;
/*     */     }
/*     */ 
/* 322 */     if (paramFB_TYPE.equals(FB_TYPE.PASS)) {
/* 323 */       passes_in_career += 1;
/* 324 */       if (gui_globals.set_pass_warn_points().contains(Integer.valueOf(passes_in_career))) {
/* 325 */         gui_globals.pop_overused_pass_warning(this.diff_browser, passes_in_career);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 330 */     gui_display_pkg localgui_display_pkg = this.edit_queue.get_cur_edit();
/* 331 */     metadata localmetadata = (metadata)localgui_display_pkg.page_hist.get(0);
/* 332 */     if (((paramFB_TYPE.equals(FB_TYPE.AGF)) || (paramFB_TYPE.equals(FB_TYPE.GUILTY))) && (this.menu_bar.get_options_menu().get_dttr_policy()) && (!secondary_review) && (localgui_display_pkg.get_user_edit_count() >= 50))
/*     */     {
/* 335 */       gui_globals.pop_dttr_warning(this.diff_browser);
/* 336 */       secondary_review = true;
/* 337 */       return;
/*     */     }
/*     */ 
/* 340 */     secondary_review = false;
/*     */ 
/* 342 */     boolean bool = this.login_panel.check_and_reset_state_change();
/* 343 */     if (((paramFB_TYPE.equals(FB_TYPE.GUILTY)) || (paramFB_TYPE.equals(FB_TYPE.AGF))) && (bool))
/*     */     {
/* 345 */       this.edit_queue.refresh_edit_token(this.login_panel.get_session_cookie());
/* 346 */       if (this.login_panel.editor_using_native_rb()) {
/* 347 */         this.edit_queue.refresh_rb_token(this.login_panel.get_session_cookie());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 352 */     if ((((Long)last_classification.snd).longValue() == localmetadata.rid) && (paramFB_TYPE.equals(FB_TYPE.PASS)) && (((FB_TYPE)last_classification.fst).equals(FB_TYPE.INNOCENT)))
/*     */     {
/* 354 */       this.client_interface.queues.queue_resurrect(localmetadata.rid, localmetadata.pid);
/*     */     }
/*     */ 
/* 360 */     if (localgui_display_pkg.get_token() == null)
/* 361 */       localgui_display_pkg.refresh_edit_token(this.login_panel.get_session_cookie());
/* 362 */     if ((this.login_panel.editor_using_native_rb()) && (localmetadata.rb_token == null))
/* 363 */       localgui_display_pkg.refresh_rb_token(this.login_panel.get_session_cookie());
/*     */     gui_comment_panel.COMMENT_TAB localCOMMENT_TAB;
/* 369 */     if ((paramFB_TYPE.equals(FB_TYPE.GUILTY)) && (stiki_utils.queue_to_type(localgui_display_pkg.source_queue).equals(stiki_utils.QUEUE_TYPE.VANDALISM)))
/*     */     {
/* 371 */       localCOMMENT_TAB = gui_comment_panel.COMMENT_TAB.VAND;
/* 372 */     } else if ((paramFB_TYPE.equals(FB_TYPE.GUILTY)) && (stiki_utils.queue_to_type(localgui_display_pkg.source_queue).equals(stiki_utils.QUEUE_TYPE.LINK_SPAM)))
/*     */     {
/* 374 */       localCOMMENT_TAB = gui_comment_panel.COMMENT_TAB.SPAM;
/* 375 */     } else if (paramFB_TYPE.equals(FB_TYPE.AGF))
/* 376 */       localCOMMENT_TAB = gui_comment_panel.COMMENT_TAB.AGF;
/* 377 */     else localCOMMENT_TAB = gui_comment_panel.COMMENT_TAB.VOID;
/* 378 */     gui_fb_handler localgui_fb_handler = new gui_fb_handler(this, paramFB_TYPE, localgui_display_pkg, this.login_panel.get_editing_user(), this.comment_panel.get_comment(localmetadata, localCOMMENT_TAB), this.login_panel.get_session_cookie(), this.login_panel.editor_has_native_rb(), this.login_panel.rb_checkbox_selected(), this.login_panel.watchlist_checkbox_selected(), this.comment_panel.get_warn_status(localCOMMENT_TAB), this.revert_panel, WORKER_THREADS);
/*     */ 
/* 387 */     WORKER_THREADS.submit(localgui_fb_handler);
/*     */ 
/* 390 */     if ((paramFB_TYPE.equals(FB_TYPE.GUILTY)) || (paramFB_TYPE.equals(FB_TYPE.AGF)))
/* 391 */       this.button_panel.back_button_enabled(false);
/* 392 */     else this.button_panel.back_button_enabled(true);
/* 393 */     last_classification = new pair(paramFB_TYPE, Long.valueOf(this.edit_queue.get_cur_edit().metadata.rid));
/*     */ 
/* 396 */     advance_revision(false);
/*     */   }
/*     */ 
/*     */   public void back_button_pressed()
/*     */     throws Exception
/*     */   {
/* 404 */     advance_revision(true);
/* 405 */     this.button_panel.back_button_enabled(false);
/*     */   }
/*     */ 
/*     */   public int get_passes_in_career()
/*     */   {
/* 415 */     return passes_in_career;
/*     */   }
/*     */ 
/*     */   private void advance_revision(boolean paramBoolean)
/*     */     throws Exception
/*     */   {
/* 429 */     switch_mode_if_needed();
/*     */ 
/* 432 */     this.edit_queue.next_rid(this.login_panel.get_editing_user(), this.login_panel.get_session_cookie(), this.login_panel.editor_using_native_rb(), this.menu_bar.selected_queue(), paramBoolean, false);
/*     */ 
/* 436 */     this.diff_browser.display_content(this.edit_queue.get_cur_edit());
/* 437 */     this.metadata_panel.set_displayed_rid(this.edit_queue.get_cur_edit());
/*     */   }
/*     */ 
/*     */   private void switch_mode_if_needed()
/*     */   {
/* 445 */     stiki_utils.QUEUE_TYPE localQUEUE_TYPE = this.menu_bar.selected_type();
/* 446 */     if (localQUEUE_TYPE != last_q_type) {
/* 447 */       this.button_panel.change_type_setup(localQUEUE_TYPE);
/* 448 */       this.comment_panel.change_queue_type(localQUEUE_TYPE);
/* 449 */       last_q_type = localQUEUE_TYPE;
/* 450 */       if (localQUEUE_TYPE == stiki_utils.QUEUE_TYPE.LINK_SPAM)
/* 451 */         this.menu_bar.get_options_menu().set_hyperlink_policy(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private JPanel create_left_panel()
/*     */   {
/* 459 */     JPanel localJPanel = new JPanel();
/* 460 */     localJPanel.setLayout(new BoxLayout(localJPanel, 1));
/*     */ 
/* 464 */     this.login_panel.setBorder(gui_globals.produce_titled_border("Login Panel"));
/*     */ 
/* 466 */     this.button_panel.setBorder(gui_globals.produce_titled_border("Classification"));
/*     */ 
/* 468 */     this.comment_panel.setBorder(gui_globals.produce_titled_border("Comments"));
/*     */ 
/* 472 */     localJPanel.add(gui_globals.center_comp_with_glue(this.login_panel));
/* 473 */     localJPanel.add(Box.createVerticalGlue());
/* 474 */     localJPanel.add(gui_globals.center_comp_with_glue(this.button_panel));
/* 475 */     localJPanel.add(Box.createVerticalGlue());
/* 476 */     localJPanel.add(gui_globals.center_comp_with_glue(this.comment_panel));
/*     */ 
/* 481 */     localJPanel.setPreferredSize(new Dimension(225, 2147483647));
/*     */ 
/* 483 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private JPanel initialize_bottom_panel()
/*     */   {
/* 493 */     this.metadata_panel.setBorder(gui_globals.produce_titled_border("Edit Properties"));
/*     */ 
/* 495 */     this.revert_panel.setBorder(gui_globals.produce_titled_border("Last Revert"));
/*     */ 
/* 499 */     JPanel localJPanel = new JPanel(new BorderLayout(0, 0));
/* 500 */     localJPanel.add(this.revert_panel, "West");
/* 501 */     localJPanel.add(this.metadata_panel, "Center");
/* 502 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private void window_size_position()
/*     */   {
/* 510 */     int i = gui_settings.get_int_def(gui_settings.SETTINGS_INT.win_width, -2147483648);
/*     */ 
/* 512 */     int j = gui_settings.get_int_def(gui_settings.SETTINGS_INT.win_height, -2147483648);
/*     */ 
/* 514 */     int k = gui_settings.get_int_def(gui_settings.SETTINGS_INT.win_locx, -2147483648);
/*     */ 
/* 516 */     int m = gui_settings.get_int_def(gui_settings.SETTINGS_INT.win_loxy, -2147483648);
/*     */ 
/* 519 */     if ((m == -2147483648) || (k == -2147483648) || (j == -2147483648) || (i == -2147483648))
/*     */     {
/* 522 */       Dimension localDimension = getToolkit().getScreenSize();
/* 523 */       i = localDimension.width * 8 / 10;
/* 524 */       j = localDimension.height * 8 / 10;
/* 525 */       k = (localDimension.width - i) / 2;
/* 526 */       m = (localDimension.height - j) / 2;
/*     */     }
/* 528 */     setBounds(k, m, i, j);
/*     */   }
/*     */ 
/*     */   private WindowAdapter get_exit_handler()
/*     */   {
/* 536 */     1 local1 = new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent paramWindowEvent) {
/* 538 */         stiki_frontend_driver.this.exit_handler(true);
/*     */       }
/*     */     };
/* 541 */     return local1;
/*     */   }
/*     */ 
/*     */   public static enum FB_TYPE
/*     */   {
/*  55 */     INNOCENT, PASS, AGF, GUILTY;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     executables.stiki_frontend_driver
 * JD-Core Version:    0.6.0
 */