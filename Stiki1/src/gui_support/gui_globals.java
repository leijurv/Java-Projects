/*     */ package gui_support;
/*     */ 
/*     */ import core_objects.stiki_utils;
/*     */ import gui_menus.gui_text_menu;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButtonMenuItem;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.CompoundBorder;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.text.html.HTMLDocument;
/*     */ import javax.swing.text.html.StyleSheet;
/*     */ 
/*     */ public class gui_globals
/*     */ {
/*     */   public static final int LEFT_SIDEBAR_WIDTH = 225;
/*     */   public static final int OUT_BORDER_WIDTH = 8;
/*     */   public static final int IN_BORDER_WIDTH = 3;
/*     */   public static final int TEXT_FIELD_BORDER = 3;
/*     */   public static final int BROWSER_BORDER = 8;
/*     */   public static final int INTRO_LABEL_SPACER = 10;
/*     */   public static final int MENUBAR_HORIZ_SPACING = 10;
/*  92 */   public static final Font BOLD_TITLE_FONT = new Font("SansSerif", 1, 14);
/*     */ 
/*  98 */   public static final Font BOLD_NORMAL_FONT = new Font("SansSerif", 1, 12);
/*     */ 
/* 104 */   public static final Font PLAIN_NORMAL_FONT = new Font("SansSerif", 0, 12);
/*     */ 
/* 110 */   public static final Font SMALL_NORMAL_FONT = new Font("SansSerif", 0, 10);
/*     */ 
/* 116 */   public static final Font TINY_NORMAL_FONT = new Font("SansSerif", 0, 8);
/*     */ 
/* 123 */   public static final Font PLAIN_SERIF_FONT = new Font("Serif", 0, 12);
/*     */ 
/* 129 */   public static final Font DEFAULT_BROWSER_FONT = PLAIN_NORMAL_FONT;
/*     */   public static final String ZWS = "​";
/*     */   public static final char ZWS_CHAR = '​';
/*     */   public static final String SOFT_HYPHEN = "­";
/*     */   public static final String LEFT_BRACK_REGEX = "(<|\\&lt;|\\&l;t;)";
/*     */   public static final String RIGHT_BRACK_REGEX = "(>|\\&gt;|\\&g;t;)";
/*     */   public static final String BRACKETED_REGEX = "(<|\\&lt;|\\&l;t;).*?(>|\\&gt;|\\&g;t;)";
/*     */   public static final String STRICT_BRACKETED_REGEX = "<.*?>";
/* 179 */   public static final Set<Integer> PASS_WARN_POINTS = set_pass_warn_points();
/*     */ 
/*     */   public static JEditorPane create_stiki_html_pane(URL paramURL, boolean paramBoolean)
/*     */   {
/*     */     Object localObject;
/* 203 */     if (paramURL != null) try {
/* 204 */         localObject = new JEditorPane(paramURL, paramBoolean) {
/*     */           public void copy() {
/* 206 */             if (this.val$copyable)
/* 207 */               stiki_utils.set_sys_clipboard(diff_whitespace.strip_zws_chars(getSelectedText())); 
/*     */           }
/*     */           public void cut() {
/*     */           } } ;
/*     */       } catch (Exception localException) {
/* 212 */         System.err.println("Failed to open URL: " + paramURL.toString() + " when initializing an HTML panel");
/*     */ 
/* 214 */         localException.printStackTrace();
/* 215 */         return null;
/*     */       } else
/* 217 */       localObject = new JEditorPane(paramBoolean) {
/*     */         public void copy() {
/* 219 */           if (this.val$copyable)
/* 220 */             stiki_utils.set_sys_clipboard(diff_whitespace.strip_zws_chars(getSelectedText()));
/*     */         }
/*     */ 
/*     */         public void cut() {
/*     */         }
/*     */       };
/* 227 */     ((JEditorPane)localObject).setContentType("text/html");
/* 228 */     ((JEditorPane)localObject).setVisible(true);
/* 229 */     ((JEditorPane)localObject).setEditable(false);
/* 230 */     if (!paramBoolean)
/* 231 */       ((JEditorPane)localObject).setHighlighter(null);
/* 232 */     else ((JEditorPane)localObject).addMouseListener(new gui_text_menu((JEditorPane)localObject));
/*     */ 
/* 236 */     String str = "body { font-family: " + DEFAULT_BROWSER_FONT.getFamily() + "; " + "font-size: " + DEFAULT_BROWSER_FONT.getSize() + "pt; }";
/*     */ 
/* 239 */     HTMLDocument localHTMLDocument = (HTMLDocument)((JEditorPane)localObject).getDocument();
/* 240 */     localHTMLDocument.getStyleSheet().addRule(str);
/* 241 */     return (JEditorPane)localObject;
/*     */   }
/*     */ 
/*     */   public static void change_html_pane_font(JEditorPane paramJEditorPane, Font paramFont)
/*     */   {
/* 250 */     String str = "body { font-family: " + paramFont.getFamily() + "; " + "font-size: " + paramFont.getSize() + "pt; }";
/*     */ 
/* 253 */     HTMLDocument localHTMLDocument = (HTMLDocument)paramJEditorPane.getDocument();
/* 254 */     localHTMLDocument.getStyleSheet().addRule(str);
/* 255 */     paramJEditorPane.repaint();
/*     */   }
/*     */ 
/*     */   public static void open_url(Component paramComponent, String paramString)
/*     */   {
/* 267 */     paramString = paramString.replace(" ", "_");
/*     */     try {
/* 269 */       url_browse.openURL(paramString);
/*     */     } catch (Exception localException) {
/* 271 */       JOptionPane.showMessageDialog(paramComponent, "Resource cannot not be opened.\nThis is likely the result of:\n\n(a) an ill-formed URL. This does not\nnecessarily indicate a broken link\nor erroneous Wiki-formatting\n\n(b) You are using Java 1.5 or earlier\non an OS that does not support awt.Desktop\nand we were unsuccessful in more hack-ish\nattempts to open a browser application.\nPlease report this error.", "Warning: Error opening URL", 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CompoundBorder produce_titled_border(String paramString)
/*     */   {
/* 295 */     paramString = paramString.toUpperCase();
/* 296 */     TitledBorder localTitledBorder = BorderFactory.createTitledBorder(paramString);
/* 297 */     localTitledBorder.setTitleJustification(2);
/* 298 */     localTitledBorder.setTitleFont(BOLD_TITLE_FONT);
/* 299 */     Border localBorder1 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
/*     */ 
/* 301 */     Border localBorder2 = BorderFactory.createEmptyBorder(3, 3, 3, 3);
/*     */ 
/* 305 */     CompoundBorder localCompoundBorder = BorderFactory.createCompoundBorder(localBorder1, localTitledBorder);
/* 306 */     return BorderFactory.createCompoundBorder(localCompoundBorder, localBorder2);
/*     */   }
/*     */ 
/*     */   public static JLabel plain_centered_multiline_label(String paramString)
/*     */   {
/* 316 */     JLabel localJLabel = new JLabel("<HTML><CENTER>" + paramString + "</CENTER></HTML>");
/* 317 */     localJLabel.setFont(PLAIN_NORMAL_FONT);
/* 318 */     localJLabel.setHorizontalAlignment(0);
/* 319 */     localJLabel.setHorizontalTextPosition(0);
/* 320 */     return localJLabel;
/*     */   }
/*     */ 
/*     */   public static JPanel center_comp_with_glue(Component paramComponent)
/*     */   {
/* 333 */     JPanel localJPanel = new JPanel();
/* 334 */     localJPanel.setLayout(new BoxLayout(localJPanel, 0));
/* 335 */     localJPanel.add(Box.createHorizontalGlue());
/* 336 */     localJPanel.add(paramComponent);
/* 337 */     localJPanel.add(Box.createHorizontalGlue());
/* 338 */     localJPanel.setAlignmentX(0.0F);
/* 339 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   public static JPanel create_horiz_separator()
/*     */   {
/* 347 */     JPanel localJPanel = new JPanel();
/* 348 */     JSeparator localJSeparator = new JSeparator(0);
/* 349 */     localJPanel.add(localJSeparator);
/* 350 */     localJPanel.setLayout(new GridLayout(0, 1));
/* 351 */     localJPanel.setMaximumSize(new Dimension(2147483647, 1));
/* 352 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   public static JPanel create_vert_separator()
/*     */   {
/* 360 */     JPanel localJPanel = new JPanel();
/* 361 */     JSeparator localJSeparator = new JSeparator(1);
/* 362 */     localJPanel.add(localJSeparator);
/* 363 */     localJPanel.setLayout(new GridLayout(1, 0));
/* 364 */     localJPanel.setMaximumSize(new Dimension(1, 2147483647));
/* 365 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   public static JLabel create_intro_label(String paramString)
/*     */   {
/* 375 */     JLabel localJLabel = new JLabel(paramString);
/* 376 */     localJLabel.setFont(BOLD_NORMAL_FONT);
/* 377 */     localJLabel.setAlignmentX(1.0F);
/* 378 */     localJLabel.setHorizontalAlignment(4);
/* 379 */     localJLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
/*     */ 
/* 381 */     return localJLabel;
/*     */   }
/*     */ 
/*     */   public static JLabel create_data_label(String paramString)
/*     */   {
/* 390 */     JLabel localJLabel = new JLabel(paramString);
/* 391 */     localJLabel.setFont(PLAIN_NORMAL_FONT);
/* 392 */     return localJLabel;
/*     */   }
/*     */ 
/*     */   public static JTextField create_data_field(String paramString)
/*     */   {
/* 401 */     JTextField localJTextField = new JTextField(paramString);
/* 402 */     localJTextField.setBorder(null);
/* 403 */     localJTextField.setOpaque(false);
/* 404 */     localJTextField.setEditable(false);
/* 405 */     localJTextField.setFont(PLAIN_NORMAL_FONT);
/* 406 */     return localJTextField;
/*     */   }
/*     */ 
/*     */   public static JButton create_link(String paramString, ActionListener paramActionListener)
/*     */   {
/* 415 */     JButton localJButton = new JButton(paramString);
/* 416 */     localJButton.setFont(get_link_font(false));
/* 417 */     localJButton.setHorizontalAlignment(2);
/* 418 */     localJButton.setAlignmentX(0.0F);
/* 419 */     localJButton.setBorderPainted(false);
/* 420 */     localJButton.setOpaque(false);
/* 421 */     localJButton.setBackground(Color.LIGHT_GRAY);
/* 422 */     localJButton.addActionListener(paramActionListener);
/* 423 */     localJButton.setMargin(new Insets(0, 0, 0, 0));
/* 424 */     return localJButton;
/*     */   }
/*     */ 
/*     */   public static JButton create_small_link(String paramString, ActionListener paramActionListener)
/*     */   {
/* 433 */     JButton localJButton = new JButton(paramString);
/* 434 */     localJButton.setFont(get_link_font(true));
/* 435 */     localJButton.setHorizontalAlignment(2);
/* 436 */     localJButton.setAlignmentX(0.0F);
/* 437 */     localJButton.setBorderPainted(false);
/* 438 */     localJButton.setOpaque(false);
/* 439 */     localJButton.setBackground(Color.LIGHT_GRAY);
/* 440 */     localJButton.addActionListener(paramActionListener);
/* 441 */     localJButton.setMargin(new Insets(0, 0, 0, 0));
/* 442 */     return localJButton;
/*     */   }
/*     */ 
/*     */   public static JCheckBoxMenuItem checkbox_item(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 455 */     JCheckBoxMenuItem localJCheckBoxMenuItem = new JCheckBoxMenuItem(paramString);
/* 456 */     localJCheckBoxMenuItem.setFont(PLAIN_NORMAL_FONT);
/* 457 */     localJCheckBoxMenuItem.setMnemonic(paramInt);
/* 458 */     localJCheckBoxMenuItem.setEnabled(paramBoolean1);
/* 459 */     localJCheckBoxMenuItem.setSelected(paramBoolean2);
/* 460 */     return localJCheckBoxMenuItem;
/*     */   }
/*     */ 
/*     */   public static JRadioButtonMenuItem radiobutton_item(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 473 */     JRadioButtonMenuItem localJRadioButtonMenuItem = new JRadioButtonMenuItem(paramString);
/* 474 */     localJRadioButtonMenuItem.setFont(PLAIN_NORMAL_FONT);
/* 475 */     localJRadioButtonMenuItem.setMnemonic(paramInt);
/* 476 */     localJRadioButtonMenuItem.setEnabled(paramBoolean1);
/* 477 */     localJRadioButtonMenuItem.setSelected(paramBoolean2);
/* 478 */     return localJRadioButtonMenuItem;
/*     */   }
/*     */ 
/*     */   public static Font get_link_font(boolean paramBoolean)
/*     */   {
/* 489 */     if (!paramBoolean)
/* 490 */       localFont = PLAIN_NORMAL_FONT;
/* 491 */     else localFont = SMALL_NORMAL_FONT;
/* 492 */     Hashtable localHashtable = new Hashtable();
/* 493 */     localHashtable.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
/* 494 */     localHashtable.put(TextAttribute.FOREGROUND, new Color(0, 0, 153));
/* 495 */     Font localFont = localFont.deriveFont(localHashtable);
/* 496 */     return localFont;
/*     */   }
/*     */ 
/*     */   public static Set<Integer> set_pass_warn_points()
/*     */   {
/* 507 */     TreeSet localTreeSet = new TreeSet();
/* 508 */     localTreeSet.add(Integer.valueOf(10));
/* 509 */     localTreeSet.add(Integer.valueOf(50));
/* 510 */     localTreeSet.add(Integer.valueOf(100));
/* 511 */     localTreeSet.add(Integer.valueOf(250));
/* 512 */     localTreeSet.add(Integer.valueOf(500));
/* 513 */     localTreeSet.add(Integer.valueOf(1000));
/* 514 */     return localTreeSet;
/*     */   }
/*     */ 
/*     */   public static void pop_overused_pass_warning(JComponent paramJComponent, int paramInt)
/*     */   {
/* 524 */     JOptionPane.showMessageDialog(paramJComponent, "STiki monitors your use of the \"pass\" button and asks\nyou to be careful with its use. If you are uncertain of\nthe classification, please use \"pass\" only when the\ndiffering knowledge of another STiki user may result in\na \"vandalism\" or \"good faith revert\" decision. If it\nis likely that other STiki users will also be uncertain,\nplease default to using the \"innocent\" button.\n\nThis approach is intended to prevent the same edit being\nshown to multiple STiki users, none of whom can make a\ndecision; that would be wasteful of STiki users' time.\nRemember that STiki is not the final say on an edit.\nArticle watchlisters and others with more knowledge of\nthe subject will also see the change and may revert it.\n\nThis message will become less frequently shown as your\nnumber of classifications grows. Thanks!\n\n", "Info: Potential overuse of PASS button", 1);
/*     */   }
/*     */ 
/*     */   public static void pop_dttr_warning(JComponent paramJComponent)
/*     */   {
/* 551 */     JOptionPane.showMessageDialog(paramJComponent, "The user you are about to revert has at least 50\narticle edits. Many editors of Wikipedia suggest\n\"not templating the regulars\" (see [[WP:DTTR]]).\n\nYou will now be returned to the STiki window to\nre-inspect the edit. Whichever option you choose this\ntime will be applied as normal.\n\nIf the edit is truly unconstructive, revert it.\nHowever, wiki-etiquette dictates that a polite and\npersonalized user talk page message is preferred over\nstandardized warning templates for such users.\n\nAlternatively, if the situation is ambiguous you\nshould consider leaving an article talk page message.\n\n", "Warning: Potential to template a regular", 2);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_globals
 * JD-Core Version:    0.6.0
 */