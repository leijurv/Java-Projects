/*     */ package gui_panels;
/*     */ 
/*     */ import core_objects.stiki_utils;
/*     */ import db_client.client_interface;
/*     */ import executables.stiki_frontend_driver;
/*     */ import gui_support.gui_globals;
/*     */ import gui_support.gui_settings;
/*     */ import gui_support.gui_settings.SETTINGS_BOOL;
/*     */ import gui_support.gui_settings.SETTINGS_STR;
/*     */ import gui_support.url_browse;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.net.URL;
/*     */ import java.util.Set;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.UIDefaults;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import javax.swing.event.HyperlinkEvent.EventType;
/*     */ import javax.swing.event.HyperlinkListener;
/*     */ import mediawiki_api.api_post;
/*     */ import mediawiki_api.api_retrieve;
/*     */ import mediawiki_api.api_xml_user_perm;
/*     */ 
/*     */ public class gui_login_panel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   public static final boolean ANON_ENABLED = false;
/*     */   public static final boolean ANON_VISIBLE = false;
/*     */   public static final boolean ROLLBACK_OPTION = false;
/*     */   public static final boolean ROLLBACK_REQUIRED = false;
/*     */   private String session_cookie;
/*     */   private String editing_user;
/*  97 */   private boolean editor_has_native_rb = false;
/*     */   private boolean is_state_stable;
/*     */   private boolean state_changed;
/*     */   private JCheckBox anon_checkbox;
/*     */   private JButton anon_link;
/*     */   private JTextField field_user;
/*     */   private JPasswordField field_pass;
/*     */   private JButton button_login;
/*     */   private JButton button_logout;
/*     */   private JLabel label_status;
/*     */   private JCheckBox rollback_checkbox;
/*     */   private JCheckBox watchlist_checkbox;
/*     */   private JButton watchlist_link;
/*     */   private stiki_frontend_driver parent;
/*     */ 
/*     */   public gui_login_panel(stiki_frontend_driver paramstiki_frontend_driver)
/*     */     throws Exception
/*     */   {
/* 183 */     this.parent = paramstiki_frontend_driver;
/*     */ 
/* 186 */     setLayout(new BoxLayout(this, 3));
/*     */ 
/* 193 */     add(Box.createVerticalGlue());
/* 194 */     add(get_field_subpanel());
/* 195 */     add(Box.createVerticalGlue());
/* 196 */     add(get_button_subpanel());
/* 197 */     add(Box.createVerticalGlue());
/* 198 */     add(get_status_subpanel());
/* 199 */     add(Box.createVerticalGlue());
/*     */ 
/* 202 */     get_rb_subpanel();
/* 203 */     add(get_watchlist_subpanel());
/* 204 */     add(Box.createVerticalGlue());
/*     */ 
/* 215 */     anonymous_unchecked();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*     */     try
/*     */     {
/* 228 */       if (paramActionEvent.getSource().equals(this.anon_checkbox)) {
/* 229 */         if (this.anon_checkbox.isSelected())
/* 230 */           anonymous_checked();
/*     */         else
/* 232 */           anonymous_unchecked();
/* 233 */       } else if (paramActionEvent.getSource().equals(this.button_login))
/* 234 */         login_clicked();
/* 235 */       else if (paramActionEvent.getSource().equals(this.button_logout))
/* 236 */         logout_clicked();
/* 237 */       else if (paramActionEvent.getSource().equals(this.anon_link))
/* 238 */         anon_dialog();
/* 239 */       else if (paramActionEvent.getSource().equals(this.watchlist_link))
/* 240 */         watchlist_dialog();
/* 241 */       else if (paramActionEvent.getSource().equals(this.rollback_checkbox))
/* 242 */         this.state_changed = true;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 246 */       JOptionPane.showMessageDialog(this, "Error in the user login interface,\nlikely caused by network error \n" + localException.getMessage(), "Error: Problem in log-in pane", 0);
/*     */ 
/* 251 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean check_and_reset_state_change()
/*     */   {
/* 263 */     boolean bool = this.state_changed;
/* 264 */     this.state_changed = false;
/* 265 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean is_state_stable()
/*     */   {
/* 276 */     return this.is_state_stable;
/*     */   }
/*     */ 
/*     */   public String get_session_cookie()
/*     */   {
/* 286 */     return this.session_cookie;
/*     */   }
/*     */ 
/*     */   public String get_editing_user()
/*     */   {
/* 294 */     return this.editing_user;
/*     */   }
/*     */ 
/*     */   public String get_login_user_field()
/*     */   {
/* 305 */     return this.field_user.getText();
/*     */   }
/*     */ 
/*     */   public boolean editor_has_native_rb()
/*     */   {
/* 313 */     return this.editor_has_native_rb;
/*     */   }
/*     */ 
/*     */   public boolean rb_checkbox_selected()
/*     */   {
/* 321 */     return this.rollback_checkbox.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean editor_using_native_rb()
/*     */   {
/* 329 */     return (this.editor_has_native_rb) && (rb_checkbox_selected());
/*     */   }
/*     */ 
/*     */   public boolean watchlist_checkbox_selected()
/*     */   {
/* 337 */     return this.watchlist_checkbox.isSelected();
/*     */   }
/*     */ 
/*     */   private JPanel get_field_subpanel()
/*     */   {
/* 350 */     this.field_user = new JTextField("anonymous");
/* 351 */     this.field_pass = new JPasswordField("");
/* 352 */     Dimension localDimension = new Dimension(2147483647, this.field_user.getPreferredSize().height);
/*     */ 
/* 354 */     this.field_user.setMaximumSize(localDimension);
/* 355 */     this.field_pass.setMaximumSize(localDimension);
/*     */ 
/* 358 */     JLabel localJLabel1 = new JLabel("Username:");
/* 359 */     localJLabel1.setFont(gui_globals.BOLD_NORMAL_FONT);
/* 360 */     JLabel localJLabel2 = new JLabel("Password:");
/* 361 */     localJLabel2.setFont(gui_globals.BOLD_NORMAL_FONT);
/*     */ 
/* 364 */     JPanel localJPanel = new JPanel();
/* 365 */     localJPanel.setLayout(new BoxLayout(localJPanel, 1));
/*     */ 
/* 367 */     localJPanel.add(localJLabel1);
/* 368 */     localJPanel.add(this.field_user);
/* 369 */     localJPanel.add(Box.createVerticalGlue());
/* 370 */     localJPanel.add(localJLabel2);
/* 371 */     localJPanel.add(this.field_pass);
/* 372 */     localJPanel.setAlignmentX(0.0F);
/* 373 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private JPanel get_checkbox_subpanel()
/*     */   {
/* 383 */     this.anon_checkbox = new JCheckBox("Edit Anonymously");
/* 384 */     this.anon_checkbox.setMnemonic(65);
/* 385 */     this.anon_checkbox.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 386 */     this.anon_checkbox.addActionListener(this);
/* 387 */     this.anon_link = gui_globals.create_link("[?]", this);
/*     */ 
/* 390 */     JPanel localJPanel = new JPanel();
/* 391 */     localJPanel.setLayout(new BoxLayout(localJPanel, 0));
/* 392 */     localJPanel.add(Box.createHorizontalGlue());
/* 393 */     localJPanel.add(this.anon_checkbox);
/* 394 */     localJPanel.add(this.anon_link);
/* 395 */     localJPanel.add(Box.createHorizontalGlue());
/* 396 */     localJPanel.setAlignmentX(0.0F);
/* 397 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private JPanel get_button_subpanel()
/*     */   {
/* 407 */     this.button_login = new JButton("Log-in");
/* 408 */     this.button_login.setMnemonic(76);
/* 409 */     this.button_login.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 410 */     this.button_login.addActionListener(this);
/* 411 */     this.button_logout = new JButton("Log-out");
/* 412 */     this.button_logout.setMnemonic(79);
/* 413 */     this.button_logout.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 414 */     this.button_logout.addActionListener(this);
/*     */ 
/* 418 */     JPanel localJPanel = new JPanel();
/* 419 */     localJPanel.add(Box.createHorizontalGlue());
/* 420 */     localJPanel.setLayout(new BoxLayout(localJPanel, 0));
/*     */ 
/* 422 */     localJPanel.add(this.button_login);
/* 423 */     localJPanel.add(Box.createHorizontalGlue());
/* 424 */     localJPanel.add(this.button_logout);
/* 425 */     localJPanel.add(Box.createHorizontalGlue());
/* 426 */     localJPanel.setAlignmentX(0.0F);
/* 427 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private JPanel get_status_subpanel()
/*     */   {
/* 438 */     this.label_status = new JLabel("");
/* 439 */     this.label_status.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 440 */     this.label_status.setHorizontalAlignment(0);
/* 441 */     this.label_status.setHorizontalTextPosition(0);
/*     */ 
/* 446 */     return gui_globals.center_comp_with_glue(this.label_status);
/*     */   }
/*     */ 
/*     */   private JPanel get_rb_subpanel()
/*     */   {
/* 454 */     this.rollback_checkbox = new JCheckBox("Use Rollback Action", true);
/* 455 */     this.rollback_checkbox.setMnemonic(85);
/* 456 */     this.rollback_checkbox.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 457 */     this.rollback_checkbox.addActionListener(this);
/* 458 */     return gui_globals.center_comp_with_glue(this.rollback_checkbox);
/*     */   }
/*     */ 
/*     */   private JPanel get_watchlist_subpanel()
/*     */   {
/* 468 */     this.watchlist_checkbox = new JCheckBox("Never Watchlist", gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.login_watch, true));
/*     */ 
/* 471 */     this.watchlist_checkbox.setMnemonic(78);
/* 472 */     this.watchlist_checkbox.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 473 */     this.watchlist_checkbox.addActionListener(this);
/* 474 */     this.watchlist_link = gui_globals.create_link("[?]", this);
/*     */ 
/* 477 */     JPanel localJPanel = new JPanel();
/* 478 */     localJPanel.setLayout(new BoxLayout(localJPanel, 0));
/* 479 */     localJPanel.add(Box.createHorizontalGlue());
/* 480 */     localJPanel.add(this.watchlist_checkbox);
/* 481 */     localJPanel.add(this.watchlist_link);
/* 482 */     localJPanel.add(Box.createHorizontalGlue());
/* 483 */     localJPanel.setAlignmentX(0.0F);
/* 484 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private void anonymous_checked()
/*     */   {
/*     */     try
/*     */     {
/* 493 */       api_post.process_logout();
/*     */     } catch (Exception localException) {
/* 495 */       JOptionPane.showMessageDialog(this, "Wikipedia logout failed,\nplease ensure network connectivity.", "Warning: Logout attempt failed", 0);
/*     */     }
/*     */ 
/* 503 */     this.is_state_stable = true;
/* 504 */     this.state_changed = true;
/* 505 */     this.session_cookie = null;
/* 506 */     this.editing_user = get_machine_ip();
/* 507 */     this.editor_has_native_rb = false;
/*     */ 
/* 510 */     this.field_user.setText("anonymous");
/* 511 */     this.field_user.setEditable(false);
/* 512 */     this.field_pass.setText("");
/* 513 */     this.field_pass.setEditable(false);
/* 514 */     this.button_login.setEnabled(false);
/* 515 */     this.button_logout.setEnabled(false);
/*     */ 
/* 518 */     set_status_to_current_editor(this.editing_user);
/*     */   }
/*     */ 
/*     */   private void anonymous_unchecked()
/*     */     throws Exception
/*     */   {
/* 529 */     this.is_state_stable = false;
/*     */ 
/* 532 */     this.field_user.setText(gui_settings.get_str_def(gui_settings.SETTINGS_STR.login_user, ""));
/*     */ 
/* 534 */     this.field_user.setEditable(true);
/* 535 */     this.field_pass.setEditable(true);
/* 536 */     this.button_login.setEnabled(true);
/* 537 */     this.button_logout.setEnabled(false);
/* 538 */     this.label_status.setText("<HTML><CENTER>Please Log-in</CENTER></HTML>");
/*     */   }
/*     */ 
/*     */   private void login_clicked()
/*     */     throws Exception
/*     */   {
/* 549 */     String str1 = this.field_user.getText();
/* 550 */     String str2 = String.valueOf(this.field_pass.getPassword());
/*     */ 
/* 553 */     Set localSet = api_retrieve.process_user_perm(str1);
/* 554 */     boolean bool = api_xml_user_perm.has_rollback(localSet);
/* 555 */     if (!login_qualified(str1, bool)) {
/* 556 */       return;
/*     */     }
/*     */ 
/* 559 */     this.session_cookie = api_post.process_login(str1, str2);
/* 560 */     if (this.session_cookie == null) {
/* 561 */       this.label_status.setText("Log-in Failed");
/*     */     }
/*     */     else {
/* 564 */       this.is_state_stable = true;
/* 565 */       this.state_changed = true;
/* 566 */       this.editing_user = str1;
/* 567 */       this.editor_has_native_rb = bool;
/*     */ 
/* 571 */       this.field_user.setEditable(false);
/* 572 */       this.field_pass.setEditable(false);
/* 573 */       this.button_login.setEnabled(false);
/* 574 */       this.button_logout.setEnabled(true);
/*     */ 
/* 577 */       set_status_to_current_editor(this.editing_user);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean login_qualified(String paramString, boolean paramBoolean)
/*     */     throws Exception
/*     */   {
/* 596 */     if ((paramBoolean) || (api_retrieve.process_user_edits(paramString, 0, stiki_utils.cur_unix_time(), 0L, 1000L, 500) >= 1000L) || (this.parent.client_interface.user_explicit(paramString)))
/*     */     {
/* 599 */       return true;
/*     */     }
/* 601 */     this.label_status.setText("Insufficient Permissions");
/* 602 */     not_qualified_dialog();
/* 603 */     return false;
/*     */   }
/*     */ 
/*     */   private void logout_clicked()
/*     */     throws Exception
/*     */   {
/* 616 */     api_post.process_logout();
/* 617 */     this.session_cookie = null;
/* 618 */     this.is_state_stable = false;
/*     */ 
/* 621 */     this.field_user.setText("");
/* 622 */     this.field_user.setEditable(true);
/* 623 */     this.editor_has_native_rb = false;
/* 624 */     this.field_pass.setText("");
/* 625 */     this.field_pass.setEditable(true);
/* 626 */     this.button_login.setEnabled(true);
/* 627 */     this.button_logout.setEnabled(false);
/* 628 */     this.label_status.setText("<HTML><CENTER>Log-out Successful</CENTER></HTML>");
/*     */   }
/*     */ 
/*     */   private String get_machine_ip()
/*     */   {
/* 641 */     return "-anonymous-";
/*     */   }
/*     */ 
/*     */   private void set_status_to_current_editor(String paramString)
/*     */   {
/* 653 */     if (paramString.length() > 20)
/* 654 */       paramString = paramString.substring(0, 17) + "...";
/* 655 */     this.label_status.setText("<HTML><CENTER>Currently editing as<BR>" + paramString + "</CENTER></HTML>");
/*     */   }
/*     */ 
/*     */   private void anon_dialog()
/*     */   {
/* 663 */     JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this), "Note that due to concerns of abuse -- anonymous editing\nhas been temporarily disabled. This functionality may\nreturn pending discussions with the Wikipedia administration.\n\nA Wikipedia log-in is now required in order to use STiki.\n\n", "Information: Anonymous editing disabled", 1);
/*     */   }
/*     */ 
/*     */   private void watchlist_dialog()
/*     */   {
/* 677 */     JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this), "If this box is checked, STiki will *never* add an article\nwhich is edited/reverted to your watchlist. If unchecked,\nSTiki will default to your account \"preferences\".\n\n", "Information: Watchlist checkbox", 1);
/*     */   }
/*     */ 
/*     */   private void not_qualified_dialog()
/*     */   {
/* 692 */     JEditorPane localJEditorPane = gui_globals.create_stiki_html_pane(null, true);
/*     */ 
/* 694 */     localJEditorPane.setBackground(UIManager.getDefaults().getColor(this.parent.getBackground()));
/*     */ 
/* 696 */     localJEditorPane.addHyperlinkListener(new HyperlinkListener() {
/*     */       public void hyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent) {
/* 698 */         if (paramHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
/* 699 */           url_browse.openURL(paramHyperlinkEvent.getURL().toString());
/*     */       }
/*     */     });
/* 705 */     String str = "The user you are attempting to login <B>does not have <BR>sufficient privileges</B> to use the STiki tool. At the <BR>current time a user meet one of the following criteria:<BR><BR>1. Have the <A HREF=\"http://en.wikipedia.org/wiki/Wikipedia:Rollback\">rollback</A> permission.<BR>2. Have > 1000 edits in Wikipedia's article namespace<BR>3. Get special approval on <A HREF=\"http://en.wikipedia.org/wiki/Wikipedia_talk:STiki\">STiki's talk page</A><BR><BR>Well-intentioned novice editors should consider option<BR>#3 above. You can be adopted by an experienced STiki<BR>user and be fighting vandalism in no time!<BR><BR>";
/*     */ 
/* 719 */     localJEditorPane.setText(str);
/*     */ 
/* 722 */     JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this), localJEditorPane, "Error: User lacks STiki permissions", 0);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_panels.gui_login_panel
 * JD-Core Version:    0.6.0
 */