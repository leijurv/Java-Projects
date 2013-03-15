/*     */ package irc_work;
/*     */ 
/*     */ import org.schwering.irc.lib.IRCEventAdapter;
/*     */ import org.schwering.irc.lib.IRCModeParser;
/*     */ import org.schwering.irc.lib.IRCUser;
/*     */ 
/*     */ public class irc_out_listener extends IRCEventAdapter
/*     */ {
/*  25 */   public final int SPIN_INTERVAL_MS = 10;
/*     */ 
/*  33 */   private boolean have_mode = false;
/*     */ 
/*  38 */   private boolean have_notice = false;
/*     */ 
/*  43 */   private int last_reply_code = -1;
/*     */ 
/*     */   public void await_reply(int paramInt)
/*     */   {
/*     */     try
/*     */     {
/*  53 */       while (this.last_reply_code != paramInt)
/*  54 */         Thread.sleep(10L);
/*  55 */       reset_flags();
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void await_notice() {
/*     */     try {
/*  63 */       while (!this.have_notice)
/*  64 */         Thread.sleep(10L);
/*  65 */       reset_flags();
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void await_notice_or_mode() {
/*     */     try {
/*  73 */       while ((!this.have_notice) && (!this.have_mode))
/*  74 */         Thread.sleep(10L);
/*  75 */       reset_flags();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset_flags() {
/*  83 */     this.have_mode = false;
/*  84 */     this.have_notice = false;
/*  85 */     this.last_reply_code = -1;
/*     */   }
/*     */ 
/*     */   public void onMode(String paramString, IRCUser paramIRCUser, IRCModeParser paramIRCModeParser)
/*     */   {
/*  94 */     this.have_mode = true;
/*     */   }
/*     */ 
/*     */   public void onNotice(String paramString1, IRCUser paramIRCUser, String paramString2) {
/*  98 */     this.have_notice = true;
/*     */   }
/*     */ 
/*     */   public void onReply(int paramInt, String paramString1, String paramString2) {
/* 102 */     this.last_reply_code = paramInt;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     irc_work.irc_out_listener
 * JD-Core Version:    0.6.0
 */