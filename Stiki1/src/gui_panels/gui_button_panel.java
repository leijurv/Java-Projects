/*     */ package gui_panels;
/*     */ 
/*     */ import core_objects.stiki_utils.QUEUE_TYPE;
/*     */ import executables.stiki_frontend_driver;
/*     */ import executables.stiki_frontend_driver.FB_TYPE;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class gui_button_panel extends JPanel
/*     */   implements ActionListener, KeyListener
/*     */ {
/*     */   private JButton button_guilty;
/*     */   private JButton button_agf;
/*     */   private JButton button_pass;
/*     */   private JButton button_innocent;
/*     */   private JButton button_back;
/*     */   private stiki_utils.QUEUE_TYPE cur_type;
/*     */   private stiki_frontend_driver parent;
/*     */ 
/*     */   public gui_button_panel(stiki_frontend_driver paramstiki_frontend_driver, stiki_utils.QUEUE_TYPE paramQUEUE_TYPE)
/*     */   {
/*  80 */     this.parent = paramstiki_frontend_driver;
/*     */ 
/*  83 */     this.button_guilty = new JButton("Vandalism (Undo)");
/*  84 */     this.button_agf = new JButton("Good Faith Revert");
/*  85 */     this.button_pass = new JButton("Pass");
/*  86 */     this.button_innocent = new JButton("Innocent");
/*  87 */     this.button_back = new JButton("<HTML><CENTER>&lt;<BR><BR>B<BR>A<BR>C<BR>K<BR><BR>&lt;</CENTER></HTML>");
/*     */ 
/*  91 */     this.button_guilty.setMnemonic(86);
/*  92 */     this.button_agf.setMnemonic(71);
/*  93 */     this.button_pass.setMnemonic(80);
/*  94 */     this.button_innocent.setMnemonic(73);
/*  95 */     this.button_back.setMnemonic(66);
/*     */ 
/*  98 */     this.button_guilty.setFont(gui_globals.PLAIN_NORMAL_FONT);
/*  99 */     this.button_agf.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 100 */     this.button_pass.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 101 */     this.button_innocent.setFont(gui_globals.PLAIN_NORMAL_FONT);
/* 102 */     this.button_back.setFont(gui_globals.TINY_NORMAL_FONT);
/*     */ 
/* 105 */     this.button_guilty.addActionListener(this);
/* 106 */     this.button_agf.addActionListener(this);
/* 107 */     this.button_pass.addActionListener(this);
/* 108 */     this.button_innocent.addActionListener(this);
/* 109 */     this.button_back.addActionListener(this);
/* 110 */     this.button_guilty.addKeyListener(this);
/* 111 */     this.button_agf.addKeyListener(this);
/* 112 */     this.button_pass.addKeyListener(this);
/* 113 */     this.button_innocent.addKeyListener(this);
/* 114 */     this.button_back.addKeyListener(this);
/*     */ 
/* 119 */     Dimension localDimension = this.button_agf.getPreferredSize();
/* 120 */     this.button_guilty.setMaximumSize(localDimension);
/* 121 */     this.button_agf.setMaximumSize(localDimension);
/* 122 */     this.button_pass.setMaximumSize(localDimension);
/* 123 */     this.button_innocent.setMaximumSize(localDimension);
/*     */ 
/* 126 */     this.button_back.setMargin(new Insets(0, 0, 0, 0));
/* 127 */     this.button_back.setMaximumSize(this.button_back.getPreferredSize());
/* 128 */     this.button_back.setEnabled(false);
/*     */ 
/* 131 */     JPanel localJPanel = new JPanel();
/* 132 */     localJPanel.setLayout(new BoxLayout(localJPanel, 1));
/*     */ 
/* 134 */     localJPanel.add(Box.createVerticalGlue());
/* 135 */     localJPanel.add(this.button_guilty);
/* 136 */     localJPanel.add(Box.createVerticalGlue());
/* 137 */     localJPanel.add(this.button_agf);
/* 138 */     localJPanel.add(Box.createVerticalGlue());
/* 139 */     localJPanel.add(this.button_pass);
/* 140 */     localJPanel.add(Box.createVerticalGlue());
/* 141 */     localJPanel.add(this.button_innocent);
/* 142 */     localJPanel.add(Box.createVerticalGlue());
/*     */ 
/* 145 */     setLayout(new BoxLayout(this, 0));
/* 146 */     add(Box.createHorizontalGlue());
/* 147 */     add(this.button_back);
/* 148 */     add(Box.createHorizontalGlue());
/* 149 */     add(Box.createHorizontalGlue());
/* 150 */     add(localJPanel);
/* 151 */     add(Box.createHorizontalGlue());
/* 152 */     setAlignmentX(0.0F);
/*     */ 
/* 155 */     change_type_setup(paramQUEUE_TYPE);
/*     */   }
/*     */ 
/*     */   public void change_type_setup(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE)
/*     */   {
/* 167 */     if (this.cur_type != paramQUEUE_TYPE) {
/* 168 */       if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM)) {
/* 169 */         this.button_guilty.setText("Vandalism (Undo)");
/* 170 */         this.button_guilty.setMnemonic(86);
/* 171 */       } else if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.LINK_SPAM)) {
/* 172 */         this.button_guilty.setText("Link Spam (Undo)");
/* 173 */         this.button_guilty.setMnemonic(83);
/*     */       }
/* 175 */       this.cur_type = paramQUEUE_TYPE;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void back_button_enabled(boolean paramBoolean)
/*     */   {
/* 184 */     this.button_back.setEnabled(paramBoolean);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/*     */     try
/*     */     {
/* 195 */       if (paramActionEvent.getSource().equals(this.button_innocent))
/* 196 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.INNOCENT);
/* 197 */       else if (paramActionEvent.getSource().equals(this.button_pass))
/* 198 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.PASS);
/* 199 */       else if (paramActionEvent.getSource().equals(this.button_agf))
/* 200 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.AGF);
/* 201 */       else if (paramActionEvent.getSource().equals(this.button_guilty))
/* 202 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.GUILTY);
/* 203 */       else if (paramActionEvent.getSource().equals(this.button_back))
/* 204 */         this.parent.back_button_pressed();
/*     */     } catch (Exception localException) {
/* 206 */       System.out.println("Error internal to button-press handler: ");
/* 207 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent paramKeyEvent)
/*     */   {
/*     */     try
/*     */     {
/* 220 */       if (((paramKeyEvent.getKeyChar() == 'v') || (paramKeyEvent.getKeyChar() == 'V')) && (this.cur_type == stiki_utils.QUEUE_TYPE.VANDALISM))
/*     */       {
/* 222 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.GUILTY);
/* 223 */         this.button_guilty.requestFocusInWindow();
/* 224 */       } else if (((paramKeyEvent.getKeyChar() == 's') || (paramKeyEvent.getKeyChar() == 'S')) && (this.cur_type == stiki_utils.QUEUE_TYPE.LINK_SPAM))
/*     */       {
/* 226 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.GUILTY);
/* 227 */         this.button_guilty.requestFocusInWindow();
/* 228 */       } else if ((paramKeyEvent.getKeyChar() == 'g') || (paramKeyEvent.getKeyChar() == 'G')) {
/* 229 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.AGF);
/* 230 */         this.button_agf.requestFocusInWindow();
/* 231 */       } else if ((paramKeyEvent.getKeyChar() == 'p') || (paramKeyEvent.getKeyChar() == 'P')) {
/* 232 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.PASS);
/* 233 */         this.button_pass.requestFocusInWindow();
/* 234 */       } else if ((paramKeyEvent.getKeyChar() == 'i') || (paramKeyEvent.getKeyChar() == 'I')) {
/* 235 */         this.parent.class_action(stiki_frontend_driver.FB_TYPE.INNOCENT);
/* 236 */         this.button_innocent.requestFocusInWindow();
/* 237 */       } else if ((paramKeyEvent.getKeyChar() == 'b') || (paramKeyEvent.getKeyChar() == 'B')) {
/* 238 */         this.parent.back_button_pressed();
/* 239 */         this.button_back.requestFocusInWindow();
/*     */       }
/*     */ 
/* 246 */       if ((paramKeyEvent.getKeyCode() == 40) || (paramKeyEvent.getKeyCode() == 38) || (paramKeyEvent.getKeyCode() == 34) || (paramKeyEvent.getKeyCode() == 33))
/*     */       {
/* 250 */         this.parent.diff_browser.keyPressed(paramKeyEvent);
/*     */       }
/*     */     } catch (Exception localException) {
/* 253 */       System.out.println("Error internal to classification-handler: ");
/* 254 */       localException.printStackTrace();
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
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_panels.gui_button_panel
 * JD-Core Version:    0.6.0
 */