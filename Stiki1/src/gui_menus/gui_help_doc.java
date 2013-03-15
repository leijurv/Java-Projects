/*     */ package gui_menus;
/*     */ 
/*     */ import gui_support.gui_filesys_images;
/*     */ import gui_support.gui_globals;
/*     */ import gui_support.url_browse;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.net.URL;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import javax.swing.event.HyperlinkEvent.EventType;
/*     */ import javax.swing.event.HyperlinkListener;
/*     */ 
/*     */ public class gui_help_doc
/*     */ {
/*     */   public static final int HELP_WIN_WIDTH = 595;
/*     */   public static final int HELP_WIN_HEIGHT = 700;
/*     */   public static final String HELP_FILEPATH = "stiki_help.html";
/*     */   public static final String ANCHOR_FULL = "sec_top";
/*     */   public static final String ANCHOR_QUEUE = "sec_queue";
/*     */   public static final String ANCHOR_STIKI_S = "sec_stiki_scoring";
/*     */   public static final String ANCHOR_FILTERS = "sec_filter";
/*     */   public static final String ANCHOR_BROWSER = "sec_browser";
/*     */   public static final String ANCHOR_METADATA = "sec_metadata";
/*     */   public static final String ANCHOR_CLASS = "sec_class";
/*     */   public static final String ANCHOR_LOGIN = "sec_login";
/*     */   public static final String ANCHOR_COMMENT = "sec_comment";
/*     */   public static final String ANCHOR_LASTRV = "sec_lastrevert";
/*     */   public static final String ANCHOR_PFORM = "sec_performance";
/*     */   public static final String ANCHOR_ORT = "sec_ort";
/*     */ 
/*     */   public static void show_help(Component paramComponent, String paramString)
/*     */     throws Exception
/*     */   {
/*  73 */     JFrame localJFrame = new JFrame();
/*  74 */     Dimension localDimension = localJFrame.getToolkit().getScreenSize();
/*  75 */     int i = (localDimension.width - 595) / 2;
/*  76 */     int j = (localDimension.height - 700) / 2;
/*  77 */     localJFrame.setBounds(i, j, 595, 700);
/*  78 */     localJFrame.setTitle("STiki Help Pane");
/*  79 */     localJFrame.setIconImage(gui_filesys_images.ICON_64);
/*     */ 
/*  82 */     JEditorPane localJEditorPane = get_help_doc(paramString);
/*  83 */     JScrollPane localJScrollPane = new JScrollPane(localJEditorPane);
/*  84 */     localJFrame.add(localJScrollPane);
/*     */ 
/*  87 */     Border localBorder1 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
/*     */ 
/*  90 */     Border localBorder2 = BorderFactory.createLineBorder(Color.BLACK, 1);
/*  91 */     localJEditorPane.setBorder(localBorder1);
/*  92 */     localJScrollPane.setBorder(BorderFactory.createCompoundBorder(localBorder1, localBorder2));
/*     */ 
/*  97 */     localJEditorPane.addHyperlinkListener(new HyperlinkListener() {
/*     */       public void hyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent) {
/*  99 */         if (paramHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
/* 100 */           JEditorPane localJEditorPane = (JEditorPane)paramHyperlinkEvent.getSource();
/* 101 */           if (paramHyperlinkEvent.getDescription().startsWith("#"))
/* 102 */             localJEditorPane.scrollToReference(paramHyperlinkEvent.getDescription().substring(1));
/* 103 */           else url_browse.openURL(paramHyperlinkEvent.getURL().toString());
/*     */         }
/*     */       }
/*     */     });
/* 107 */     localJFrame.setVisible(true);
/*     */   }
/*     */ 
/*     */   private static JEditorPane get_help_doc(String paramString)
/*     */     throws Exception
/*     */   {
/* 127 */     URL localURL = new URL(null, gui_help_doc.class.getResource("stiki_help.html").toExternalForm() + "#" + paramString);
/*     */ 
/* 129 */     return gui_globals.create_stiki_html_pane(localURL, true);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_help_doc
 * JD-Core Version:    0.6.0
 */