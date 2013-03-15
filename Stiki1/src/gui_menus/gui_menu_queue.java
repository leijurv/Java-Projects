/*     */ package gui_menus;
/*     */ 
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.QUEUE_TYPE;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import db_client.client_interface;
/*     */ import executables.stiki_frontend_driver;
/*     */ import gui_support.gui_globals;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.PrintStream;
/*     */ import java.text.DecimalFormat;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JRadioButtonMenuItem;
/*     */ 
/*     */ public class gui_menu_queue extends JMenu
/*     */   implements ActionListener
/*     */ {
/*     */   private JRadioButtonMenuItem item_cluebotng;
/*     */   private JRadioButtonMenuItem item_stiki;
/*     */   private JRadioButtonMenuItem item_wikitrust;
/*     */   private JRadioButtonMenuItem item_spam;
/*     */   private JRadioButtonMenuItem item_meta;
/*     */   private JMenuItem item_recent_use;
/*     */   private stiki_frontend_driver parent;
/*     */   private stiki_utils.SCORE_SYS selected_queue;
/*     */   private stiki_utils.QUEUE_TYPE selected_type;
/*     */ 
/*     */   public gui_menu_queue(stiki_frontend_driver paramstiki_frontend_driver, stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */   {
/*  92 */     this.parent = paramstiki_frontend_driver;
/*     */ 
/*  95 */     setText("Rev. Queue");
/*  96 */     setFont(gui_globals.PLAIN_NORMAL_FONT);
/*  97 */     setMnemonic(81);
/*     */ 
/* 100 */     this.item_cluebotng = gui_globals.radiobutton_item("Cluebot-NG", 67, true, false);
/*     */ 
/* 102 */     this.item_stiki = gui_globals.radiobutton_item("STiki (metadata)", 83, true, false);
/*     */ 
/* 104 */     this.item_wikitrust = gui_globals.radiobutton_item("WikiTrust", 87, true, false);
/*     */ 
/* 106 */     this.item_spam = gui_globals.radiobutton_item("Link Spam", 87, true, false);
/*     */ 
/* 108 */     this.item_meta = gui_globals.radiobutton_item("Meta (combination)", 157, false, false);
/*     */ 
/* 111 */     this.item_recent_use = gui_menu_bar.create_menu_item("Recent usage stats.", 82);
/*     */ 
/* 115 */     this.item_cluebotng.addActionListener(this);
/* 116 */     this.item_stiki.addActionListener(this);
/* 117 */     this.item_wikitrust.addActionListener(this);
/* 118 */     this.item_spam.addActionListener(this);
/* 119 */     this.item_meta.addActionListener(this);
/* 120 */     this.item_recent_use.addActionListener(this);
/*     */ 
/* 123 */     ButtonGroup localButtonGroup = new ButtonGroup();
/* 124 */     localButtonGroup.add(this.item_cluebotng);
/* 125 */     localButtonGroup.add(this.item_stiki);
/* 126 */     localButtonGroup.add(this.item_wikitrust);
/* 127 */     localButtonGroup.add(this.item_spam);
/* 128 */     localButtonGroup.add(this.item_meta);
/*     */ 
/* 131 */     add(this.item_cluebotng);
/* 132 */     add(this.item_stiki);
/* 133 */     add(this.item_wikitrust);
/* 134 */     add(this.item_spam);
/* 135 */     add(this.item_meta);
/* 136 */     addSeparator();
/* 137 */     add(this.item_recent_use);
/*     */ 
/* 140 */     this.selected_queue = paramSCORE_SYS;
/* 141 */     this.selected_type = stiki_utils.queue_to_type(this.selected_queue);
/* 142 */     set_initial_state(this.selected_queue);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/* 153 */     if (paramActionEvent.getSource().equals(this.item_recent_use)) {
/*     */       try { pop_recent_use_dialog();
/*     */       } catch (Exception localException) {
/* 156 */         System.out.println("Error in recent usage stats. dialog pop:");
/* 157 */         localException.printStackTrace();
/*     */       }
/*     */     } else {
/* 160 */       if (this.item_cluebotng.isSelected())
/* 161 */         this.selected_queue = stiki_utils.SCORE_SYS.CBNG;
/* 162 */       else if (this.item_stiki.isSelected())
/* 163 */         this.selected_queue = stiki_utils.SCORE_SYS.STIKI;
/* 164 */       else if (this.item_wikitrust.isSelected())
/* 165 */         this.selected_queue = stiki_utils.SCORE_SYS.WT;
/* 166 */       else if (this.item_spam.isSelected())
/* 167 */         this.selected_queue = stiki_utils.SCORE_SYS.SPAM;
/* 168 */       this.selected_type = stiki_utils.queue_to_type(this.selected_queue);
/*     */     }
/*     */   }
/*     */ 
/*     */   public stiki_utils.SCORE_SYS selected_queue()
/*     */   {
/* 178 */     return this.selected_queue;
/*     */   }
/*     */ 
/*     */   public stiki_utils.QUEUE_TYPE selected_type()
/*     */   {
/* 186 */     return this.selected_type;
/*     */   }
/*     */ 
/*     */   private void pop_recent_use_dialog()
/*     */     throws Exception
/*     */   {
/* 200 */     DecimalFormat localDecimalFormat = new DecimalFormat("#.##");
/* 201 */     StringBuilder localStringBuilder = new StringBuilder();
/* 202 */     localStringBuilder.append("The following are STiki/queue usage statistics.\nLarge quantities of recent use are likely to\ndecrease vandalism hit-rates. These statistics\nare presented so users can best allocate their\nefforts as they see fit, either by varying\nqueues or usage times:\n\n");
/*     */ 
/* 211 */     long l = stiki_utils.cur_unix_time() - 3600L;
/* 212 */     int[] arrayOfInt = this.parent.client_interface.recent_use(l);
/* 213 */     int i = arrayOfInt[0];
/* 214 */     double d1 = i > 0 ? 100.0D * arrayOfInt[1] / i : 0.0D;
/* 215 */     int j = arrayOfInt[2];
/* 216 */     double d2 = j > 0 ? 100.0D * arrayOfInt[3] / j : 0.0D;
/* 217 */     int k = arrayOfInt[4];
/* 218 */     double d3 = k > 0 ? 100.0D * arrayOfInt[5] / k : 0.0D;
/* 219 */     int m = arrayOfInt[6];
/* 220 */     double d4 = m > 0 ? 100.0D * arrayOfInt[7] / m : 0.0D;
/*     */ 
/* 223 */     localStringBuilder.append("QUEUE - CLASSIFICATIONS (REVERT-%)\n\nIn the last one hour:\n---------------------------------------\nClueBot NG - " + j + " (" + localDecimalFormat.format(d2) + "%)\n" + "Metadata - " + i + " (" + localDecimalFormat.format(d1) + "%)\n" + "Wikitrust - " + k + " (" + localDecimalFormat.format(d3) + "%)\n" + "Link Spam - " + m + " (" + localDecimalFormat.format(d4) + "%)\n\n");
/*     */ 
/* 233 */     l = stiki_utils.cur_unix_time() - 21600L;
/* 234 */     arrayOfInt = this.parent.client_interface.recent_use(l);
/* 235 */     i = arrayOfInt[0];
/* 236 */     d1 = i > 0 ? 100.0D * arrayOfInt[1] / i : 0.0D;
/* 237 */     j = arrayOfInt[2];
/* 238 */     d2 = j > 0 ? 100.0D * arrayOfInt[3] / j : 0.0D;
/* 239 */     k = arrayOfInt[4];
/* 240 */     d3 = k > 0 ? 100.0D * arrayOfInt[5] / k : 0.0D;
/* 241 */     m = arrayOfInt[6];
/* 242 */     d4 = m > 0 ? 100.0D * arrayOfInt[7] / m : 0.0D;
/*     */ 
/* 245 */     localStringBuilder.append("In the last six hours:\n---------------------------------------\nClueBot NG - " + j + " (" + localDecimalFormat.format(d2) + "%)\n" + "Metadata - " + i + " (" + localDecimalFormat.format(d1) + "%)\n" + "Wikitrust - " + k + " (" + localDecimalFormat.format(d3) + "%)\n" + "Link Spam - " + m + " (" + localDecimalFormat.format(d4) + "%)\n\n");
/*     */ 
/* 253 */     JOptionPane.showMessageDialog(this.parent, localStringBuilder.toString(), "Recent STiki/queue usage statistics", 1);
/*     */   }
/*     */ 
/*     */   private void set_initial_state(stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */   {
/* 263 */     if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.STIKI))
/* 264 */       this.item_stiki.setSelected(true);
/* 265 */     else if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.CBNG))
/* 266 */       this.item_cluebotng.setSelected(true);
/* 267 */     else if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.WT))
/* 268 */       this.item_stiki.setSelected(true);
/* 269 */     else if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.SPAM))
/* 270 */       this.item_spam.setSelected(true);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_menus.gui_menu_queue
 * JD-Core Version:    0.6.0
 */