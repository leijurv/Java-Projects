/*     */ package executables;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.stiki_utils;
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import gui_panels.gui_diff_panel;
/*     */ import gui_panels.gui_metadata_panel;
/*     */ import gui_support.gui_filesys_images;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import offline_review_tool.ort_edit_queue;
/*     */ 
/*     */ public class offline_review_driver extends JFrame
/*     */   implements ActionListener, KeyListener
/*     */ {
/*     */   public gui_diff_panel diff_browser;
/*     */   public gui_metadata_panel metadata_panel;
/*     */   private static final int NUM_NON_GUI_THREADS = 8;
/*     */   private static ExecutorService WORKER_THREADS;
/*     */   private static ort_edit_queue edit_queue;
/*     */   private static BufferedWriter out_file;
/*     */   private JPanel button_panel;
/*     */   private JButton button_marked;
/*     */   private JButton button_unmarked;
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */     throws Exception
/*     */   {
/* 121 */     new offline_review_driver(paramArrayOfString[0]);
/*     */   }
/*     */ 
/*     */   public offline_review_driver()
/*     */     throws Exception
/*     */   {
/* 133 */     visual_setup();
/* 134 */     JFileChooser localJFileChooser = new JFileChooser();
/* 135 */     if (localJFileChooser.showOpenDialog(this) == 0)
/* 136 */       enqueue_rid_file(localJFileChooser.getSelectedFile().getAbsolutePath());
/* 137 */     else return;
/*     */   }
/*     */ 
/*     */   public offline_review_driver(String paramString)
/*     */     throws Exception
/*     */   {
/* 145 */     visual_setup();
/* 146 */     enqueue_rid_file(paramString);
/*     */   }
/*     */ 
/*     */   public void visual_setup()
/*     */     throws Exception
/*     */   {
/* 158 */     initialize_button_panel();
/* 159 */     this.metadata_panel = new gui_metadata_panel();
/* 160 */     this.diff_browser = new gui_diff_panel();
/*     */ 
/* 163 */     JPanel localJPanel = new JPanel(new BorderLayout(0, 0));
/* 164 */     this.diff_browser.setBorder(gui_globals.produce_titled_border("DIFF-Browser"));
/*     */ 
/* 166 */     localJPanel.add(this.diff_browser, "Center");
/* 167 */     localJPanel.add(initialize_bottom_panel(), "South");
/* 168 */     getContentPane().add(localJPanel, "Center");
/*     */ 
/* 171 */     Dimension localDimension = getToolkit().getScreenSize();
/* 172 */     int i = localDimension.width * 8 / 10;
/* 173 */     int j = localDimension.height * 8 / 10;
/* 174 */     int k = (localDimension.width - i) / 2;
/* 175 */     int m = (localDimension.height - j) / 2;
/* 176 */     setBounds(k, m, i, j);
/*     */ 
/* 179 */     setTitle("STiki: Offline Review Tool (ORT)");
/* 180 */     setIconImage(gui_filesys_images.ICON_64);
/* 181 */     setVisible(true);
/* 182 */     setDefaultCloseOperation(0);
/* 183 */     addWindowListener(get_exit_handler());
/*     */   }
/*     */ 
/*     */   public void enqueue_rid_file(String paramString)
/*     */     throws Exception
/*     */   {
/* 194 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 196 */       localBufferedReader = stiki_utils.create_reader(paramString);
/* 197 */       out_file = stiki_utils.create_writer(determine_out_file(paramString), false);
/*     */     }
/*     */     catch (Exception localException) {
/* 200 */       localException.printStackTrace();
/* 201 */       JOptionPane.showMessageDialog(this, "The RID file passed to the Offline Review Tool (ORT)\ncould not be opened. ORT will now shut-down. Check the\nSTiki help-file for further documentation for the\nrequired format.", "Error: Passed in RID file failed", 0);
/*     */ 
/* 207 */       return;
/*     */     }
/*     */ 
/* 211 */     WORKER_THREADS = Executors.newFixedThreadPool(8);
/* 212 */     edit_queue = new ort_edit_queue(WORKER_THREADS, localBufferedReader);
/*     */ 
/* 214 */     advance_revision();
/* 215 */     this.button_marked.requestFocusInWindow();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*     */     try
/*     */     {
/* 225 */       if (paramActionEvent.getSource().equals(this.button_marked))
/* 226 */         class_action(CLASS_TYPE.MARKED);
/* 227 */       else if (paramActionEvent.getSource().equals(this.button_unmarked))
/* 228 */         class_action(CLASS_TYPE.UNMARKED);
/*     */     } catch (Exception localException) {
/* 230 */       System.out.println("Error internal to button-press handler: ");
/* 231 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent paramKeyEvent)
/*     */   {
/*     */     try
/*     */     {
/* 242 */       if ((paramKeyEvent.getKeyChar() == 'm') || (paramKeyEvent.getKeyChar() == 'M')) {
/* 243 */         class_action(CLASS_TYPE.MARKED);
/* 244 */         this.button_marked.requestFocusInWindow();
/* 245 */       } else if ((paramKeyEvent.getKeyChar() == 'u') || (paramKeyEvent.getKeyChar() == 'U')) {
/* 246 */         class_action(CLASS_TYPE.UNMARKED);
/* 247 */         this.button_unmarked.requestFocusInWindow();
/*     */       }
/*     */     } catch (Exception localException) {
/* 250 */       System.out.println("Error internal to classification-handler: ");
/* 251 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent paramKeyEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent paramKeyEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   private String determine_out_file(String paramString)
/*     */   {
/*     */     String str;
/* 275 */     if (paramString.contains("."))
/* 276 */       str = paramString.split("\\.")[0] + ".marked";
/*     */     else {
/* 278 */       str = paramString + ".marked";
/*     */     }
/* 280 */     return str;
/*     */   }
/*     */ 
/*     */   private void advance_revision()
/*     */     throws Exception
/*     */   {
/* 287 */     edit_queue.next_rid();
/* 288 */     this.diff_browser.display_content(edit_queue.get_cur_edit());
/* 289 */     this.metadata_panel.set_displayed_rid(edit_queue.get_cur_edit());
/*     */   }
/*     */ 
/*     */   private void class_action(CLASS_TYPE paramCLASS_TYPE)
/*     */     throws Exception
/*     */   {
/* 299 */     if (edit_queue.get_cur_edit().metadata.rid == 0L) {
/* 300 */       return;
/*     */     }
/* 302 */     if (paramCLASS_TYPE.equals(CLASS_TYPE.MARKED)) {
/* 303 */       out_file.write(edit_queue.get_cur_edit().metadata.rid + ",1\n");
/* 304 */       out_file.flush();
/*     */     } else {
/* 306 */       out_file.write(edit_queue.get_cur_edit().metadata.rid + ",0\n");
/* 307 */       out_file.flush();
/*     */     }
/* 309 */     advance_revision();
/*     */   }
/*     */ 
/*     */   private void initialize_button_panel()
/*     */   {
/* 321 */     this.button_marked = new JButton("Mark");
/* 322 */     this.button_unmarked = new JButton("Unmark");
/* 323 */     this.button_marked.setMnemonic(77);
/* 324 */     this.button_unmarked.setMnemonic(85);
/* 325 */     this.button_marked.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 326 */     this.button_unmarked.setFont(gui_globals.PLAIN_NORMAL_FONT);
/*     */ 
/* 329 */     this.button_marked.addActionListener(this);
/* 330 */     this.button_unmarked.addActionListener(this);
/* 331 */     this.button_marked.addKeyListener(this);
/* 332 */     this.button_unmarked.addKeyListener(this);
/*     */ 
/* 335 */     JLabel localJLabel = gui_globals.plain_centered_multiline_label("Classify using buttons,<BR>or 'm' and 'u' hotkeys");
/*     */ 
/* 337 */     localJLabel.setFont(gui_globals.SMALL_NORMAL_FONT);
/*     */ 
/* 342 */     Dimension localDimension = this.button_unmarked.getPreferredSize();
/* 343 */     this.button_marked.setMaximumSize(localDimension);
/* 344 */     this.button_unmarked.setMaximumSize(localDimension);
/*     */ 
/* 347 */     JPanel localJPanel = new JPanel();
/* 348 */     localJPanel.setLayout(new BoxLayout(localJPanel, 1));
/* 349 */     localJPanel.add(this.button_marked);
/* 350 */     localJPanel.add(Box.createVerticalGlue());
/* 351 */     localJPanel.add(this.button_unmarked);
/* 352 */     localJPanel.setAlignmentX(0.0F);
/*     */ 
/* 355 */     this.button_panel = new JPanel();
/* 356 */     this.button_panel.setLayout(new BoxLayout(this.button_panel, 1));
/* 357 */     this.button_panel.add(Box.createVerticalGlue());
/* 358 */     this.button_panel.add(gui_globals.center_comp_with_glue(localJPanel));
/* 359 */     this.button_panel.add(Box.createVerticalGlue());
/* 360 */     this.button_panel.add(localJLabel);
/*     */   }
/*     */ 
/*     */   private JPanel initialize_bottom_panel()
/*     */   {
/* 370 */     this.button_panel.setBorder(gui_globals.produce_titled_border("Classify"));
/*     */ 
/* 372 */     this.metadata_panel.setBorder(gui_globals.produce_titled_border("Edit Properties"));
/*     */ 
/* 376 */     JPanel localJPanel = new JPanel(new BorderLayout(0, 0));
/* 377 */     localJPanel.add(this.button_panel, "West");
/* 378 */     localJPanel.add(this.metadata_panel, "Center");
/* 379 */     return localJPanel;
/*     */   }
/*     */ 
/*     */   private WindowAdapter get_exit_handler()
/*     */   {
/* 387 */     1 local1 = new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent paramWindowEvent) {
/*     */         try { offline_review_driver.out_file.flush();
/* 390 */           offline_review_driver.out_file.close();
/* 391 */           offline_review_driver.edit_queue.shutdown();
/* 392 */           offline_review_driver.WORKER_THREADS.shutdownNow();
/* 393 */           System.exit(0);
/*     */         } catch (Exception localException) {
/* 395 */           System.out.println("Error during STiki-ORT shutdown:");
/* 396 */           localException.printStackTrace();
/* 397 */           System.exit(0);
/*     */         }
/*     */       }
/*     */     };
/* 401 */     return local1;
/*     */   }
/*     */ 
/*     */   public static enum CLASS_TYPE
/*     */   {
/*  57 */     MARKED, UNMARKED;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     executables.offline_review_driver
 * JD-Core Version:    0.6.0
 */