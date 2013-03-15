/*     */ package gui_panels;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.QUEUE_TYPE;
/*     */ import gui_support.gui_globals;
/*     */ import gui_support.gui_settings;
/*     */ import gui_support.gui_settings.SETTINGS_BOOL;
/*     */ import gui_support.gui_settings.SETTINGS_STR;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextArea;
/*     */ 
/*     */ public class gui_comment_panel extends JTabbedPane
/*     */   implements ActionListener
/*     */ {
/*     */   private static final String DEF_TEXT_VAND = "Reverted edit(s) by [[Special:Contributions/#u#|#u#]] identified as test/vandalism using [[WP:STiki|STiki]]";
/*     */   private static final String DEF_TEXT_SPAM = "Reverted edit(s) by [[Special:Contributions/#u#|#u#]] identified as external link spam using [[WP:STiki|STiki]]";
/*     */   private static final String DEF_TEXT_AGF = "Reverted [[WP:AGF|good faith]] edit(s) by [[Special:Contributions/#u#|#u#]] using [[WP:STiki|STiki]]";
/*     */   private JPanel tab_vand;
/*     */   private JPanel tab_spam;
/*     */   private JPanel tab_agf;
/*     */   private JTextArea comment_field_vand;
/*     */   private JTextArea comment_field_spam;
/*     */   private JTextArea comment_field_agf;
/*     */   private JButton comment_button_default_vand;
/*     */   private JButton comment_button_default_spam;
/*     */   private JButton comment_button_default_agf;
/*     */   private JCheckBox warn_checkbox_vand;
/*     */   private JCheckBox warn_checkbox_spam;
/*     */   private JCheckBox warn_checkbox_agf;
/* 102 */   private stiki_utils.QUEUE_TYPE queue_type = null;
/*     */ 
/*     */   public gui_comment_panel(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE)
/*     */   {
/* 114 */     this.tab_vand = create_comment_tab(COMMENT_TAB.VAND);
/* 115 */     this.tab_spam = create_comment_tab(COMMENT_TAB.SPAM);
/* 116 */     this.tab_agf = create_comment_tab(COMMENT_TAB.AGF);
/* 117 */     addTab("Vand.", this.tab_vand);
/* 118 */     addTab("Good-faith", this.tab_agf);
/*     */ 
/* 121 */     initialize_comments_warns();
/* 122 */     change_queue_type(paramQUEUE_TYPE);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/* 133 */     if (paramActionEvent.getSource().equals(this.comment_button_default_vand))
/* 134 */       make_comment_default(COMMENT_TAB.VAND);
/* 135 */     else if (paramActionEvent.getSource().equals(this.comment_button_default_spam))
/* 136 */       make_comment_default(COMMENT_TAB.SPAM);
/* 137 */     else if (paramActionEvent.getSource().equals(this.comment_button_default_agf))
/* 138 */       make_comment_default(COMMENT_TAB.AGF);
/*     */   }
/*     */ 
/*     */   public String get_comment(metadata parammetadata, COMMENT_TAB paramCOMMENT_TAB)
/*     */   {
/* 156 */     String str = get_comment(paramCOMMENT_TAB);
/* 157 */     str = str.replaceAll("#u#", parammetadata.user);
/* 158 */     str = str.replaceAll("#a#", parammetadata.title);
/* 159 */     str = str.replaceAll("#t#", "" + (stiki_utils.cur_unix_time() - parammetadata.timestamp));
/*     */ 
/* 161 */     return str;
/*     */   }
/*     */ 
/*     */   public String get_comment(COMMENT_TAB paramCOMMENT_TAB)
/*     */   {
/* 171 */     String str = "";
/* 172 */     if (paramCOMMENT_TAB.equals(COMMENT_TAB.VAND))
/* 173 */       str = this.comment_field_vand.getText();
/* 174 */     else if (paramCOMMENT_TAB.equals(COMMENT_TAB.SPAM))
/* 175 */       str = this.comment_field_spam.getText();
/* 176 */     else if (paramCOMMENT_TAB.equals(COMMENT_TAB.AGF))
/* 177 */       str = this.comment_field_agf.getText();
/* 178 */     return str;
/*     */   }
/*     */ 
/*     */   public boolean get_warn_status(COMMENT_TAB paramCOMMENT_TAB)
/*     */   {
/* 187 */     if (paramCOMMENT_TAB.equals(COMMENT_TAB.VAND))
/* 188 */       return this.warn_checkbox_vand.isSelected();
/* 189 */     if (paramCOMMENT_TAB.equals(COMMENT_TAB.SPAM))
/* 190 */       return this.warn_checkbox_spam.isSelected();
/* 191 */     if (paramCOMMENT_TAB.equals(COMMENT_TAB.AGF))
/* 192 */       return this.warn_checkbox_agf.isSelected();
/* 193 */     return false;
/*     */   }
/*     */ 
/*     */   public void change_queue_type(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE)
/*     */   {
/* 201 */     if (this.queue_type == paramQUEUE_TYPE)
/* 202 */       return;
/* 203 */     this.queue_type = paramQUEUE_TYPE;
/* 204 */     if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM)) {
/* 205 */       setComponentAt(0, this.tab_vand);
/* 206 */       setTitleAt(0, "Vand.");
/* 207 */     } else if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.LINK_SPAM)) {
/* 208 */       setComponentAt(0, this.tab_spam);
/* 209 */       setTitleAt(0, "Spam");
/*     */     }
/*     */   }
/*     */ 
/*     */   private JPanel create_comment_tab(COMMENT_TAB paramCOMMENT_TAB)
/*     */   {
/* 225 */     JCheckBox localJCheckBox = new JCheckBox("Warn Offending Editor?", true);
/* 226 */     localJCheckBox.setMnemonic(87);
/* 227 */     localJCheckBox.setFont(gui_globals.PLAIN_NORMAL_FONT);
/*     */ 
/* 230 */     JTextArea localJTextArea = new JTextArea(5, 0);
/* 231 */     localJTextArea.setFont(gui_globals.SMALL_NORMAL_FONT);
/* 232 */     localJTextArea.setLineWrap(true);
/* 233 */     localJTextArea.setWrapStyleWord(true);
/*     */ 
/* 236 */     localJTextArea.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
/*     */ 
/* 239 */     JScrollPane localJScrollPane = new JScrollPane(localJTextArea);
/* 240 */     localJScrollPane.setVerticalScrollBarPolicy(20);
/*     */ 
/* 244 */     int i = localJScrollPane.getPreferredSize().height;
/* 245 */     localJScrollPane.setMinimumSize(new Dimension(0, i));
/* 246 */     localJScrollPane.setMaximumSize(new Dimension(2147483647, i));
/*     */ 
/* 250 */     JButton localJButton = new JButton("Default");
/* 251 */     localJButton.setMnemonic(68);
/* 252 */     localJButton.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 253 */     localJButton.addActionListener(this);
/*     */ 
/* 257 */     JPanel localJPanel = new JPanel();
/* 258 */     localJPanel.setLayout(new BoxLayout(localJPanel, 1));
/* 259 */     localJPanel.add(Box.createVerticalGlue());
/* 260 */     localJPanel.add(gui_globals.center_comp_with_glue(localJCheckBox));
/* 261 */     localJPanel.add(Box.createVerticalGlue());
/* 262 */     localJPanel.add(gui_globals.center_comp_with_glue(localJScrollPane));
/* 263 */     localJPanel.add(Box.createVerticalGlue());
/* 264 */     localJPanel.add(gui_globals.center_comp_with_glue(localJButton));
/* 265 */     localJPanel.add(Box.createVerticalGlue());
/* 266 */     localJPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
/*     */ 
/* 270 */     if (paramCOMMENT_TAB.equals(COMMENT_TAB.VAND)) {
/* 271 */       this.comment_field_vand = localJTextArea;
/* 272 */       this.comment_button_default_vand = localJButton;
/* 273 */       this.warn_checkbox_vand = localJCheckBox;
/* 274 */       localJPanel.setPreferredSize(new Dimension(localJPanel.getPreferredSize().width, (int)(localJPanel.getPreferredSize().height * 1.25D)));
/*     */     }
/* 277 */     else if (paramCOMMENT_TAB.equals(COMMENT_TAB.SPAM)) {
/* 278 */       this.comment_field_spam = localJTextArea;
/* 279 */       this.comment_button_default_spam = localJButton;
/* 280 */       this.warn_checkbox_spam = localJCheckBox;
/* 281 */       localJPanel.setPreferredSize(new Dimension(localJPanel.getPreferredSize().width, (int)(localJPanel.getPreferredSize().height * 1.25D)));
/*     */     }
/* 284 */     else if (paramCOMMENT_TAB.equals(COMMENT_TAB.AGF)) {
/* 285 */       this.comment_field_agf = localJTextArea;
/* 286 */       this.comment_button_default_agf = localJButton;
/* 287 */       this.warn_checkbox_agf = localJCheckBox;
/* 288 */       this.warn_checkbox_agf.setSelected(false);
/* 289 */       this.warn_checkbox_agf.setEnabled(false);
/*     */     }
/*     */ 
/* 292 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private void initialize_comments_warns()
/*     */   {
/* 301 */     this.comment_field_vand.setText(gui_settings.get_str_def(gui_settings.SETTINGS_STR.comment_vand, "Reverted edit(s) by [[Special:Contributions/#u#|#u#]] identified as test/vandalism using [[WP:STiki|STiki]]"));
/*     */ 
/* 304 */     this.comment_field_spam.setText(gui_settings.get_str_def(gui_settings.SETTINGS_STR.comment_spam, "Reverted edit(s) by [[Special:Contributions/#u#|#u#]] identified as external link spam using [[WP:STiki|STiki]]"));
/*     */ 
/* 307 */     this.comment_field_agf.setText(gui_settings.get_str_def(gui_settings.SETTINGS_STR.comment_agf2, "Reverted [[WP:AGF|good faith]] edit(s) by [[Special:Contributions/#u#|#u#]] using [[WP:STiki|STiki]]"));
/*     */ 
/* 311 */     this.warn_checkbox_vand.setSelected(gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.warn_vand, true));
/*     */ 
/* 314 */     this.warn_checkbox_spam.setSelected(gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.warn_spam, true));
/*     */ 
/* 317 */     this.warn_checkbox_agf.setSelected(gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.warn_agf, false));
/*     */   }
/*     */ 
/*     */   private void make_comment_default(COMMENT_TAB paramCOMMENT_TAB)
/*     */   {
/* 327 */     if (paramCOMMENT_TAB.equals(COMMENT_TAB.VAND))
/* 328 */       this.comment_field_vand.setText("Reverted edit(s) by [[Special:Contributions/#u#|#u#]] identified as test/vandalism using [[WP:STiki|STiki]]");
/* 329 */     else if (paramCOMMENT_TAB.equals(COMMENT_TAB.SPAM))
/* 330 */       this.comment_field_spam.setText("Reverted edit(s) by [[Special:Contributions/#u#|#u#]] identified as external link spam using [[WP:STiki|STiki]]");
/* 331 */     else if (paramCOMMENT_TAB.equals(COMMENT_TAB.AGF))
/* 332 */       this.comment_field_agf.setText("Reverted [[WP:AGF|good faith]] edit(s) by [[Special:Contributions/#u#|#u#]] using [[WP:STiki|STiki]]");
/*     */   }
/*     */ 
/*     */   public static enum COMMENT_TAB
/*     */   {
/*  44 */     SPAM, VAND, AGF, VOID;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_panels.gui_comment_panel
 * JD-Core Version:    0.6.0
 */