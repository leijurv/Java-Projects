/*     */ package gui_panels;
/*     */ 
/*     */ import gui_support.gui_globals;
/*     */ import gui_support.gui_revert_and_warn.RV_STYLE;
/*     */ import gui_support.gui_revert_and_warn.WARNING;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class gui_revert_panel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private String guilty_user;
/*     */   private String last_page;
/*     */   private JPanel user_panel;
/*     */   private JLabel label_user;
/*     */   private JLabel data_warning;
/*     */   private JButton link_contribs;
/*     */   private JButton link_talk;
/*     */   private JButton link_page;
/*     */ 
/*     */   public gui_revert_panel()
/*     */   {
/*  60 */     this.link_contribs = gui_globals.create_small_link("(edits)", this);
/*  61 */     this.link_talk = gui_globals.create_small_link("(talk)", this);
/*  62 */     this.link_page = gui_globals.create_small_link("(article)", this);
/*  63 */     JPanel localJPanel1 = new JPanel();
/*  64 */     localJPanel1.setLayout(new BoxLayout(localJPanel1, 0));
/*  65 */     localJPanel1.add(this.link_contribs);
/*  66 */     localJPanel1.add(this.link_talk);
/*  67 */     localJPanel1.add(this.link_page);
/*     */ 
/*  70 */     this.label_user = gui_globals.create_data_label("");
/*  71 */     this.user_panel = new JPanel();
/*  72 */     this.user_panel.setLayout(new BoxLayout(this.user_panel, 1));
/*  73 */     this.user_panel.add(gui_globals.center_comp_with_glue(this.label_user));
/*  74 */     this.user_panel.add(gui_globals.center_comp_with_glue(localJPanel1));
/*  75 */     this.user_panel.setVisible(false);
/*     */ 
/*  78 */     this.data_warning = gui_globals.plain_centered_multiline_label("No prior revert:<BR>No warning data");
/*     */ 
/*  82 */     JPanel localJPanel2 = new JPanel();
/*  83 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 1));
/*  84 */     localJPanel2.add(Box.createVerticalGlue());
/*  85 */     localJPanel2.add(gui_globals.center_comp_with_glue(this.user_panel));
/*  86 */     localJPanel2.add(Box.createVerticalGlue());
/*  87 */     localJPanel2.add(gui_globals.center_comp_with_glue(this.data_warning));
/*  88 */     localJPanel2.add(Box.createVerticalGlue());
/*     */ 
/*  92 */     localJPanel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
/*  93 */     setLayout(new GridLayout(0, 1));
/*  94 */     add(localJPanel2);
/*     */ 
/*  97 */     this.link_contribs.setVisible(false);
/*  98 */     this.link_talk.setVisible(false);
/*     */   }
/*     */ 
/*     */   public void update_display(String paramString1, String paramString2, gui_revert_and_warn.RV_STYLE paramRV_STYLE, gui_revert_and_warn.WARNING paramWARNING)
/*     */   {
/* 117 */     this.guilty_user = paramString1;
/* 118 */     this.last_page = paramString2;
/* 119 */     this.label_user.setText(trim_editor(paramString1));
/* 120 */     this.user_panel.setVisible(true);
/* 121 */     this.link_contribs.setVisible(true);
/* 122 */     this.link_talk.setVisible(true);
/* 123 */     this.link_page.setVisible(true);
/*     */ 
/* 128 */     String str = get_warn_message(paramRV_STYLE, paramWARNING);
/* 129 */     this.data_warning.setText("<HTML><CENTER>" + str + "</CENTER></HTML>");
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/* 138 */     if (paramActionEvent.getSource().equals(this.link_contribs)) {
/* 139 */       gui_globals.open_url(this, "http://en.wikipedia.org/wiki/Special:Contributions/" + this.guilty_user);
/*     */     }
/* 141 */     else if (paramActionEvent.getSource().equals(this.link_talk)) {
/* 142 */       gui_globals.open_url(this, "http://en.wikipedia.org/wiki/User_talk:" + this.guilty_user);
/*     */     }
/* 144 */     else if (paramActionEvent.getSource().equals(this.link_page))
/* 145 */       gui_globals.open_url(this, "http://en.wikipedia.org/wiki/" + this.last_page);
/*     */   }
/*     */ 
/*     */   private static String get_warn_message(gui_revert_and_warn.RV_STYLE paramRV_STYLE, gui_revert_and_warn.WARNING paramWARNING)
/*     */   {
/* 163 */     String str = "";
/* 164 */     if ((paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.SIMPLE_UNDO)) || (paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.RB_SW_ONE)) || (paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.RB_SW_MULTIPLE)) || (paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.NOGO_SW)))
/*     */     {
/* 168 */       str = str + "Undid ";
/*     */     } else str = str + "RB'ed ";
/*     */ 
/* 171 */     if ((paramWARNING.equals(gui_revert_and_warn.WARNING.NO_BEATEN)) || (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_ERROR)))
/*     */     {
/* 173 */       str = str + "0 edits<BR>";
/* 174 */     } else if ((paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.SIMPLE_UNDO)) || (paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.RB_NATIVE_ONE)) || (paramRV_STYLE.equals(gui_revert_and_warn.RV_STYLE.RB_SW_ONE)))
/*     */     {
/* 177 */       str = str + "1 edit<BR>";
/*     */     } else str = str + "2+ edits<BR>";
/*     */ 
/* 181 */     if (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_BEATEN)) {
/* 182 */       str = str + "<FONT COLOR=\"red\"><B>beaten to revert?<BR>check page hist</B></FONT>";
/*     */     }
/* 184 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_ERROR)) {
/* 185 */       str = str + "<FONT COLOR=\"red\"><B>unknown error<BR>check page hist</B></FONT>";
/*     */     }
/* 187 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_EDIT_TOO_OLD))
/* 188 */       str = str + "no warning given<BR>(edit(s) too old)";
/* 189 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_USER_BLOCK))
/* 190 */       str = str + "no warning given<BR>(user blocked)";
/* 191 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_OPT_OUT))
/* 192 */       str = str + "no warning given<BR>(STiki opt-out)";
/* 193 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.NO_AIV_TIMING))
/* 194 */       str = str + "no AIV report<BR>warned since edit";
/* 195 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.YES_UW1))
/* 196 */       str = str + "issued warning<BR>at warn-level 1";
/* 197 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.YES_UW2))
/* 198 */       str = str + "issued warning<BR>at warn-level 2";
/* 199 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.YES_UW3))
/* 200 */       str = str + "issued warning<BR>at warn-level 3";
/* 201 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.YES_UW4))
/* 202 */       str = str + "issued warning<BR>at warn-level 4";
/* 203 */     else if (paramWARNING.equals(gui_revert_and_warn.WARNING.YES_AIV))
/* 204 */       str = str + "reported to AIV<BR>(will be blocked)";
/*     */     else {
/* 206 */       str = str + "reported to AIV<BR>(special 4im case)";
/*     */     }
/* 208 */     return str;
/*     */   }
/*     */ 
/*     */   private static String trim_editor(String paramString)
/*     */   {
/* 217 */     if (paramString.length() > 15)
/* 218 */       paramString = paramString.substring(0, 12) + "...";
/* 219 */     return paramString;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_panels.gui_revert_panel
 * JD-Core Version:    0.6.0
 */