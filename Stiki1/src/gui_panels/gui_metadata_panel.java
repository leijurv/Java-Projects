/*     */ package gui_panels;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.stiki_utils;
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.ComponentListener;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class gui_metadata_panel extends JPanel
/*     */   implements ActionListener, ComponentListener
/*     */ {
/*     */   private gui_display_pkg cur_pkg;
/*     */   private JTextField data_rid;
/*     */   private JTextField data_title;
/*     */   private JTextField data_user;
/*     */   private JTextField data_timestamp;
/*     */   private JTextField data_comment;
/*     */   private JButton link_rid;
/*     */   private JButton link_title;
/*     */   private JButton link_title_hist;
/*     */   private JButton link_user_cont;
/*     */   private JButton link_user_talk;
/*     */   private JPanel intro_panel;
/*     */   private JPanel data_panel;
/*     */   private JPanel link_panel;
/*     */   private JPanel all_panel;
/*     */ 
/*     */   public gui_metadata_panel()
/*     */   {
/*  78 */     this.intro_panel = new JPanel(new GridLayout(5, 0, 0, 0));
/*  79 */     this.intro_panel.add(gui_globals.create_intro_label("REVISION-ID:"));
/*  80 */     this.intro_panel.add(gui_globals.create_intro_label("ARTICLE:"));
/*  81 */     this.intro_panel.add(gui_globals.create_intro_label("EDITING-USER:"));
/*  82 */     this.intro_panel.add(gui_globals.create_intro_label("TIME-STAMP:"));
/*  83 */     this.intro_panel.add(gui_globals.create_intro_label("COMMENT:"));
/*     */ 
/*  86 */     this.data_panel = new JPanel();
/*  87 */     this.data_panel.setLayout(new GridLayout(5, 0, 0, 0));
/*  88 */     this.data_panel.add(this.data_rid = gui_globals.create_data_field(""));
/*  89 */     this.data_panel.add(this.data_title = gui_globals.create_data_field(""));
/*  90 */     this.data_panel.add(this.data_user = gui_globals.create_data_field(""));
/*  91 */     this.data_panel.add(this.data_timestamp = gui_globals.create_data_field(""));
/*  92 */     this.data_panel.add(this.data_comment = gui_globals.create_data_field(""));
/*     */ 
/*  95 */     this.link_rid = gui_globals.create_link("(Wiki-DIFF)", this);
/*  96 */     this.link_title = gui_globals.create_link("(Current-Page)", this);
/*  97 */     this.link_title_hist = gui_globals.create_link("(Page-Hist)", this);
/*  98 */     this.link_user_cont = gui_globals.create_link("(User-Contribs)", this);
/*  99 */     this.link_user_talk = gui_globals.create_link("(User-Talk)", this);
/*     */ 
/* 102 */     JPanel localJPanel1 = new JPanel();
/* 103 */     localJPanel1.setLayout(new BoxLayout(localJPanel1, 0));
/* 104 */     localJPanel1.add(Box.createHorizontalStrut(10));
/* 105 */     localJPanel1.add(this.link_rid);
/* 106 */     localJPanel1.add(Box.createHorizontalGlue());
/* 107 */     JPanel localJPanel2 = new JPanel();
/* 108 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 0));
/* 109 */     localJPanel2.add(Box.createHorizontalStrut(10));
/* 110 */     localJPanel2.add(this.link_title);
/* 111 */     localJPanel2.add(this.link_title_hist);
/* 112 */     localJPanel2.add(Box.createHorizontalGlue());
/* 113 */     JPanel localJPanel3 = new JPanel();
/* 114 */     localJPanel3.setLayout(new BoxLayout(localJPanel3, 0));
/* 115 */     localJPanel3.add(Box.createHorizontalStrut(10));
/* 116 */     localJPanel3.add(this.link_user_cont);
/* 117 */     localJPanel3.add(this.link_user_talk);
/* 118 */     localJPanel3.add(Box.createHorizontalGlue());
/*     */ 
/* 121 */     this.link_panel = new JPanel();
/* 122 */     this.link_panel.setLayout(new GridLayout(5, 0, 0, 0));
/* 123 */     this.link_panel.add(localJPanel1);
/* 124 */     this.link_panel.add(localJPanel2);
/* 125 */     this.link_panel.add(localJPanel3);
/*     */ 
/* 128 */     this.all_panel = new JPanel();
/* 129 */     this.all_panel.setLayout(new BoxLayout(this.all_panel, 0));
/* 130 */     this.all_panel.add(this.intro_panel);
/* 131 */     this.all_panel.add(this.data_panel);
/* 132 */     this.all_panel.add(this.link_panel);
/*     */ 
/* 137 */     this.intro_panel.setMaximumSize(new Dimension(this.intro_panel.getPreferredSize().width, 2147483647));
/*     */ 
/* 139 */     this.link_panel.setMaximumSize(new Dimension(this.link_panel.getPreferredSize().width, 2147483647));
/*     */ 
/* 144 */     setLayout(new GridLayout(0, 1));
/* 145 */     add(this.all_panel);
/* 146 */     addComponentListener(this);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*     */     try
/*     */     {
/* 164 */       if (paramActionEvent.getSource().equals(this.link_rid)) {
/* 165 */         gui_globals.open_url(this, "http://en.wikipedia.org/w/index.php?oldid=" + ((metadata)this.cur_pkg.page_hist.get(this.cur_pkg.rb_depth)).rid + "&diff=cur");
/*     */       }
/*     */ 
/* 168 */       if (paramActionEvent.getSource().equals(this.link_user_cont)) {
/* 169 */         gui_globals.open_url(this, "http://en.wikipedia.org/wiki/Special:Contributions/" + this.cur_pkg.metadata.user);
/*     */       }
/* 171 */       if (paramActionEvent.getSource().equals(this.link_user_talk)) {
/* 172 */         gui_globals.open_url(this, "http://en.wikipedia.org/wiki/User_talk:" + this.cur_pkg.metadata.user);
/*     */       }
/* 174 */       if (paramActionEvent.getSource().equals(this.link_title)) {
/* 175 */         gui_globals.open_url(this, "http://en.wikipedia.org/w/index.php?title=" + URLEncoder.encode(this.cur_pkg.metadata.title, "UTF-8"));
/*     */       }
/*     */ 
/* 178 */       if (paramActionEvent.getSource().equals(this.link_title_hist)) {
/* 179 */         gui_globals.open_url(this, "http://en.wikipedia.org/w/index.php?title=" + URLEncoder.encode(this.cur_pkg.metadata.title, "UTF-8") + "&action=history");
/*     */       }
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 184 */       JOptionPane.showMessageDialog(this, "Error in the metadata interface,\nlikely caused by network error \n" + localException.getMessage(), "Error: Problem in log-in pane", 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void set_displayed_rid(gui_display_pkg paramgui_display_pkg)
/*     */   {
/* 198 */     this.cur_pkg = paramgui_display_pkg;
/* 199 */     this.data_rid.setText(this.cur_pkg.metadata.rid + "");
/* 200 */     this.data_title.setText(this.cur_pkg.metadata.title);
/* 201 */     this.data_user.setText(this.cur_pkg.metadata.user);
/* 202 */     this.data_timestamp.setText(time_ago_str(this.cur_pkg.metadata.timestamp));
/* 203 */     this.data_comment.setText(this.cur_pkg.metadata.comment);
/* 204 */     resize();
/*     */   }
/*     */ 
/*     */   public void resize()
/*     */   {
/* 218 */     int i = this.all_panel.getSize().width - (this.intro_panel.getSize().width + this.link_panel.getSize().width);
/*     */ 
/* 225 */     int j = Math.max(0, this.data_rid.getPreferredSize().width);
/* 226 */     j = Math.max(j, this.data_title.getPreferredSize().width);
/* 227 */     j = Math.max(j, this.data_user.getPreferredSize().width);
/* 228 */     j = Math.max(j, this.data_timestamp.getPreferredSize().width);
/* 229 */     j = Math.max(j, this.data_comment.getPreferredSize().width);
/*     */ 
/* 231 */     if (j >= i) {
/* 232 */       j = i;
/*     */     }
/*     */ 
/* 235 */     this.data_panel.setMinimumSize(new Dimension(j, 0));
/* 236 */     this.data_panel.setPreferredSize(new Dimension(j, this.data_panel.getPreferredSize().height));
/*     */ 
/* 238 */     this.data_panel.setMaximumSize(new Dimension(j, 2147483647));
/* 239 */     revalidate();
/*     */   }
/*     */ 
/*     */   public void componentResized(ComponentEvent paramComponentEvent)
/*     */   {
/* 244 */     resize();
/*     */   }
/*     */ 
/*     */   public void componentHidden(ComponentEvent paramComponentEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void componentMoved(ComponentEvent paramComponentEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void componentShown(ComponentEvent paramComponentEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   private String time_ago_str(long paramLong)
/*     */   {
/* 261 */     int i = (int)(stiki_utils.cur_unix_time() - paramLong);
/*     */ 
/* 264 */     int j = i / 86400;
/* 265 */     i %= 86400;
/* 266 */     int k = i / 3600;
/* 267 */     i %= 3600;
/* 268 */     int m = i / 60;
/* 269 */     int n = i % 60;
/*     */     String str;
/* 272 */     if (j > 0) {
/* 273 */       str = j + " days and " + k + " hours ago";
/*     */     }
/* 275 */     else if (k > 0)
/* 276 */       str = k + " hours and " + m + " minutes ago";
/*     */     else {
/* 278 */       str = m + " minutes and " + n + " seconds ago";
/*     */     }
/*     */ 
/* 282 */     if (j == 1) str = str.replace("days", "day");
/* 283 */     if (k == 1) str = str.replace("hours", "hour");
/* 284 */     if (m == 1) str = str.replace("minutes", "minute");
/* 285 */     if (n == 1) str = str.replace("seconds", "second");
/* 286 */     return str;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_panels.gui_metadata_panel
 * JD-Core Version:    0.6.0
 */