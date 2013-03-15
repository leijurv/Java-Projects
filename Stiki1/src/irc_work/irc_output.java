/*     */ package irc_work;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import org.schwering.irc.lib.IRCConnection;
/*     */ 
/*     */ public class irc_output
/*     */ {
/*     */   public static final int IRC_TIMEOUT = 2147483647;
/*  40 */   private boolean IRC_ENABLED = true;
/*     */   private IRCConnection con_irc;
/*     */   private irc_out_listener out_listener;
/*     */ 
/*     */   public irc_output()
/*     */     throws Exception
/*     */   {
/*  61 */     if (!this.IRC_ENABLED) {
/*  62 */       return;
/*     */     }
/*     */ 
/*  68 */     String str1 = "158.130.51.53";
/*  69 */     int i = 6667;
/*  70 */     String str2 = "armstrong";
/*  71 */     String str3 = null;
/*     */ 
/*  74 */     this.con_irc = new IRCConnection(str1, i, i, str3, str2, str2, str2);
/*  75 */     this.con_irc.setDaemon(true);
/*  76 */     this.con_irc.setColors(false);
/*  77 */     this.con_irc.setPong(true);
/*  78 */     this.con_irc.setTimeout(2147483647);
/*  79 */     this.out_listener = new irc_out_listener();
/*  80 */     this.con_irc.addIRCEventListener(this.out_listener);
/*     */     try {
/*  82 */       this.con_irc.connect();
/*  83 */       init_commands();
/*  84 */       this.con_irc.removeIRCEventListener(this.out_listener);
/*     */     } catch (IOException localIOException) {
/*  86 */       System.out.println("Error establishing IRC connection:");
/*  87 */       localIOException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void msg(CHANNELS paramCHANNELS, String paramString)
/*     */     throws Exception
/*     */   {
/* 101 */     if (!this.IRC_ENABLED) {
/* 102 */       return;
/*     */     }
/* 104 */     if (!this.con_irc.isConnected()) {
/* 105 */       this.IRC_ENABLED = false;
/* 106 */       System.out.println("IRC output message failed"); } else {
/* 107 */       this.con_irc.doPrivmsg(channel(paramCHANNELS), paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized boolean isUp()
/*     */   {
/* 116 */     return this.con_irc.isConnected();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 123 */     if (this.IRC_ENABLED)
/* 124 */       this.con_irc.close();
/*     */   }
/*     */ 
/*     */   private String channel(CHANNELS paramCHANNELS)
/*     */   {
/* 136 */     if (paramCHANNELS.equals(CHANNELS.STIKI_LINKS))
/* 137 */       return "#arm-stiki-links";
/* 138 */     if (paramCHANNELS.equals(CHANNELS.STIKI_SCORES))
/* 139 */       return "#arm-stiki-scores";
/* 140 */     return "";
/*     */   }
/*     */ 
/*     */   private synchronized void init_commands()
/*     */     throws Exception
/*     */   {
/* 162 */     this.out_listener.reset_flags();
/* 163 */     this.out_listener.await_reply(376);
/* 164 */     this.con_irc.doJoin(channel(CHANNELS.STIKI_LINKS));
/* 165 */     this.out_listener.await_reply(366);
/* 166 */     this.con_irc.doJoin(channel(CHANNELS.STIKI_SCORES));
/* 167 */     this.out_listener.await_reply(366);
/*     */ 
/* 170 */     this.con_irc.doPrivmsg("userserv", "login armstrong 4a1r5m5s");
/* 171 */     this.out_listener.await_notice();
/*     */ 
/* 175 */     this.con_irc.doPrivmsg("chanserv", "op #arm-stiki-links");
/* 176 */     this.out_listener.await_notice_or_mode();
/* 177 */     this.con_irc.doPrivmsg("chanserv", "op #arm-stiki-scores");
/* 178 */     this.out_listener.await_notice_or_mode();
/*     */ 
/* 181 */     this.con_irc.doMode("#arm-stiki-links", "+m");
/* 182 */     this.con_irc.doMode("#arm-stiki-scores", "+m");
/* 183 */     this.con_irc.doTopic(channel(CHANNELS.STIKI_LINKS), "STiki feed for link additions to en.wiki");
/*     */ 
/* 185 */     this.con_irc.doTopic(channel(CHANNELS.STIKI_SCORES), "STiki feed for metadata-driven vandalism probabilities");
/*     */   }
/*     */ 
/*     */   public static enum CHANNELS
/*     */   {
/*  22 */     STIKI_LINKS, STIKI_SCORES;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     irc_work.irc_output
 * JD-Core Version:    0.6.0
 */