/*     */ package db_client;
/*     */ 
/*     */ import core_objects.pair;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ 
/*     */ public class qmanager_client
/*     */ {
/*     */   private stiki_con_client con_client;
/*     */   private CallableStatement cstmt_queue_fetch_cbng;
/*     */   private CallableStatement cstmt_queue_fetch_stiki;
/*     */   private CallableStatement cstmt_queue_fetch_wt;
/*     */   private CallableStatement cstmt_queue_fetch_spam;
/*     */   private CallableStatement cstmt_queue_delete;
/*     */   private CallableStatement cstmt_queue_ignore;
/*     */   private CallableStatement cstmt_queue_resurrect;
/*     */   private CallableStatement cstmt_queue_wipe;
/*     */ 
/*     */   public qmanager_client(stiki_con_client paramstiki_con_client)
/*     */     throws Exception
/*     */   {
/*  76 */     this.con_client = paramstiki_con_client;
/*  77 */     prep_statements();
/*     */   }
/*     */ 
/*     */   public synchronized Queue<pair<Long, Long>> queue_fetch(stiki_utils.SCORE_SYS paramSCORE_SYS, String paramString, long paramLong)
/*     */     throws Exception
/*     */   {
/* 112 */     String str = "";
/* 113 */     if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.CBNG)) {
/* 114 */       this.cstmt_queue_fetch_cbng.setString(1, paramString);
/* 115 */       this.cstmt_queue_fetch_cbng.setLong(2, paramLong);
/* 116 */       this.cstmt_queue_fetch_cbng.execute();
/* 117 */       str = this.cstmt_queue_fetch_cbng.getString(3);
/* 118 */     } else if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.STIKI)) {
/* 119 */       this.cstmt_queue_fetch_stiki.setString(1, paramString);
/* 120 */       this.cstmt_queue_fetch_stiki.setLong(2, paramLong);
/* 121 */       this.cstmt_queue_fetch_stiki.execute();
/* 122 */       str = this.cstmt_queue_fetch_stiki.getString(3);
/* 123 */     } else if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.WT)) {
/* 124 */       this.cstmt_queue_fetch_wt.setString(1, paramString);
/* 125 */       this.cstmt_queue_fetch_wt.setLong(2, paramLong);
/* 126 */       this.cstmt_queue_fetch_wt.execute();
/* 127 */       str = this.cstmt_queue_fetch_wt.getString(3);
/* 128 */     } else if (paramSCORE_SYS.equals(stiki_utils.SCORE_SYS.SPAM)) {
/* 129 */       this.cstmt_queue_fetch_spam.setString(1, paramString);
/* 130 */       this.cstmt_queue_fetch_spam.setLong(2, paramLong);
/* 131 */       this.cstmt_queue_fetch_spam.execute();
/* 132 */       str = this.cstmt_queue_fetch_spam.getString(3);
/*     */     }
/*     */ 
/* 137 */     str = str.substring(0, str.length() - 1);
/* 138 */     String[] arrayOfString = str.split(",");
/* 139 */     LinkedList localLinkedList = new LinkedList();
/* 140 */     for (int i = 0; i < arrayOfString.length; i += 2) {
/* 141 */       localLinkedList.offer(new pair(Long.valueOf(Long.parseLong(arrayOfString[i])), Long.valueOf(Long.parseLong(arrayOfString[(i + 1)]))));
/*     */     }
/* 143 */     return localLinkedList;
/*     */   }
/*     */ 
/*     */   public synchronized void queue_delete(long paramLong)
/*     */     throws Exception
/*     */   {
/* 152 */     this.cstmt_queue_delete.setLong(1, paramLong);
/* 153 */     this.cstmt_queue_delete.execute();
/*     */   }
/*     */ 
/*     */   public synchronized void queue_resurrect(long paramLong1, long paramLong2)
/*     */     throws Exception
/*     */   {
/* 165 */     this.cstmt_queue_resurrect.setLong(1, paramLong1);
/* 166 */     this.cstmt_queue_resurrect.setLong(2, paramLong2);
/* 167 */     this.cstmt_queue_resurrect.execute();
/*     */   }
/*     */ 
/*     */   public synchronized void queue_ignore(long paramLong, String paramString)
/*     */     throws Exception
/*     */   {
/* 179 */     this.cstmt_queue_ignore.setLong(1, paramLong);
/* 180 */     this.cstmt_queue_ignore.setString(2, paramString);
/* 181 */     this.cstmt_queue_ignore.execute();
/*     */   }
/*     */ 
/*     */   public synchronized void queue_wipe(long paramLong)
/*     */     throws Exception
/*     */   {
/* 190 */     this.cstmt_queue_wipe.setLong(1, paramLong);
/* 191 */     this.cstmt_queue_wipe.execute();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */     throws Exception
/*     */   {
/* 198 */     this.cstmt_queue_delete.close();
/* 199 */     this.cstmt_queue_fetch_cbng.close();
/* 200 */     this.cstmt_queue_fetch_stiki.close();
/* 201 */     this.cstmt_queue_fetch_wt.close();
/* 202 */     this.cstmt_queue_fetch_spam.close();
/* 203 */     this.cstmt_queue_ignore.close();
/* 204 */     this.cstmt_queue_resurrect.close();
/* 205 */     this.cstmt_queue_wipe.close();
/*     */   }
/*     */ 
/*     */   private void prep_statements()
/*     */     throws Exception
/*     */   {
/* 216 */     this.cstmt_queue_fetch_cbng = this.con_client.con.prepareCall("{CALL client_queue_fetch_cbng(?,?,?)}");
/*     */ 
/* 218 */     this.cstmt_queue_fetch_cbng.registerOutParameter(3, 12);
/*     */ 
/* 220 */     this.cstmt_queue_fetch_stiki = this.con_client.con.prepareCall("{CALL client_queue_fetch_stiki(?,?,?)}");
/*     */ 
/* 222 */     this.cstmt_queue_fetch_stiki.registerOutParameter(3, 12);
/*     */ 
/* 224 */     this.cstmt_queue_fetch_wt = this.con_client.con.prepareCall("{CALL client_queue_fetch_wt(?,?,?)}");
/*     */ 
/* 226 */     this.cstmt_queue_fetch_wt.registerOutParameter(3, 12);
/*     */ 
/* 228 */     this.cstmt_queue_fetch_spam = this.con_client.con.prepareCall("{CALL client_queue_fetch_spam(?,?,?)}");
/*     */ 
/* 230 */     this.cstmt_queue_fetch_spam.registerOutParameter(3, 12);
/*     */ 
/* 232 */     this.cstmt_queue_delete = this.con_client.con.prepareCall("{CALL client_queue_delete(?)}");
/*     */ 
/* 235 */     this.cstmt_queue_ignore = this.con_client.con.prepareCall("{CALL client_queue_ignore(?,?)}");
/*     */ 
/* 238 */     this.cstmt_queue_resurrect = this.con_client.con.prepareCall("{CALL client_queue_resurrect(?,?)}");
/*     */ 
/* 241 */     this.cstmt_queue_wipe = this.con_client.con.prepareCall("{CALL client_queue_wipe(?)}");
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     db_client.qmanager_client
 * JD-Core Version:    0.6.0
 */