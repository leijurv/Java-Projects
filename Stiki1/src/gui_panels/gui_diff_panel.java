/*     */ package gui_panels;
/*     */ 
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollBar;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import javax.swing.event.HyperlinkEvent.EventType;
/*     */ import javax.swing.event.HyperlinkListener;
/*     */ 
/*     */ public class gui_diff_panel extends JPanel
/*     */   implements HyperlinkListener, KeyListener
/*     */ {
/*     */   private JEditorPane browser;
/*     */   private JScrollPane scroll_browser;
/*     */   private gui_display_pkg cur_content;
/*  48 */   private boolean activate_hyperlinks = false;
/*     */ 
/*     */   public gui_diff_panel()
/*     */   {
/*  61 */     this.browser = gui_globals.create_stiki_html_pane(null, true);
/*  62 */     this.browser.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
/*     */ 
/*  68 */     this.browser.addHyperlinkListener(this);
/*     */ 
/*  71 */     this.browser.addKeyListener(this);
/*     */ 
/*  74 */     this.scroll_browser = new JScrollPane(this.browser);
/*  75 */     setLayout(new GridLayout(0, 1));
/*  76 */     add(this.scroll_browser);
/*     */   }
/*     */ 
/*     */   public void display_content(gui_display_pkg paramgui_display_pkg)
/*     */     throws Exception
/*     */   {
/*  89 */     if (this.activate_hyperlinks)
/*  90 */       this.browser.setText(paramgui_display_pkg.content_linked);
/*  91 */     else this.browser.setText(paramgui_display_pkg.content);
/*  92 */     this.browser.setCaretPosition(0);
/*  93 */     this.cur_content = paramgui_display_pkg;
/*     */   }
/*     */ 
/*     */   public void change_browser_font(Font paramFont)
/*     */   {
/* 101 */     gui_globals.change_html_pane_font(this.browser, paramFont);
/*     */   }
/*     */ 
/*     */   public void set_hyperlink_policy(boolean paramBoolean)
/*     */   {
/* 111 */     if (this.activate_hyperlinks != paramBoolean) {
/* 112 */       this.activate_hyperlinks = paramBoolean;
/*     */       try
/*     */       {
/* 119 */         if (paramBoolean)
/* 120 */           this.browser.setText(this.cur_content.content_linked);
/* 121 */         else this.browser.setText(this.cur_content.content);
/*     */       }
/*     */       catch (NullPointerException localNullPointerException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean get_hyperlink_policy()
/*     */   {
/* 131 */     return this.activate_hyperlinks;
/*     */   }
/*     */ 
/*     */   public void hyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent)
/*     */   {
/* 138 */     if (paramHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
/*     */     {
/*     */       String str;
/*     */       try
/*     */       {
/* 144 */         str = paramHyperlinkEvent.getURL().toString();
/* 145 */         str = new URL(str).toURI().toASCIIString();
/*     */       } catch (Exception localException) {
/* 147 */         gui_globals.open_url(this, paramHyperlinkEvent.getURL().toString());
/* 148 */         return;
/*     */       }
/*     */ 
/* 152 */       gui_globals.open_url(this, str);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent paramKeyEvent)
/*     */   {
/* 164 */     if (paramKeyEvent.getKeyCode() == 40) {
/* 165 */       this.scroll_browser.getVerticalScrollBar().setValue(this.scroll_browser.getVerticalScrollBar().getValue() + 5 * this.scroll_browser.getVerticalScrollBar().getBlockIncrement());
/*     */     }
/* 169 */     else if (paramKeyEvent.getKeyCode() == 38) {
/* 170 */       this.scroll_browser.getVerticalScrollBar().setValue(this.scroll_browser.getVerticalScrollBar().getValue() - 5 * this.scroll_browser.getVerticalScrollBar().getBlockIncrement());
/*     */     }
/* 174 */     else if (paramKeyEvent.getKeyCode() == 34) {
/* 175 */       this.scroll_browser.getVerticalScrollBar().setValue(this.scroll_browser.getVerticalScrollBar().getValue() + 25 * this.scroll_browser.getVerticalScrollBar().getBlockIncrement());
/*     */     }
/* 179 */     else if (paramKeyEvent.getKeyCode() == 33)
/* 180 */       this.scroll_browser.getVerticalScrollBar().setValue(this.scroll_browser.getVerticalScrollBar().getValue() - 25 * this.scroll_browser.getVerticalScrollBar().getBlockIncrement());
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent paramKeyEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent paramKeyEvent)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_panels.gui_diff_panel
 * JD-Core Version:    0.6.0
 */