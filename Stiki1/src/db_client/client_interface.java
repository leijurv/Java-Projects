/*     */ package db_client;
/*     */ 
/*     */ import core_objects.escape_string;
/*     */ import core_objects.metadata;
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import edit_processing.rollback_handler.RB_TYPE;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ 
/*     */ public class client_interface
/*     */ {
/*     */   public final stiki_con_client con_client;
/*     */   public final qmanager_client queues;
/*     */   private CallableStatement cstmt_feedback_insert;
/*     */   private CallableStatement cstmt_geo_country;
/*     */   private CallableStatement cstmt_oe_insert;
/*     */   private CallableStatement cstmt_default_queue;
/*     */   private CallableStatement cstmt_users_explicit;
/*     */   private CallableStatement cstmt_recent_use;
/*     */   private CallableStatement cstmt_ping;
/*     */ 
/*     */   public client_interface()
/*     */     throws Exception
/*     */   {
/*  85 */     this.con_client = new stiki_con_client();
/*  86 */     if (this.con_client.con != null) {
/*  87 */       this.queues = new qmanager_client(this.con_client);
/*  88 */       prep_statements();
/*     */     } else {
/*  90 */       this.queues = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void feedback_insert(long paramLong, int paramInt, String paramString)
/*     */     throws Exception
/*     */   {
/* 105 */     this.cstmt_feedback_insert.setLong(1, paramLong);
/* 106 */     this.cstmt_feedback_insert.setInt(2, paramInt);
/* 107 */     this.cstmt_feedback_insert.setString(3, paramString);
/* 108 */     this.cstmt_feedback_insert.execute();
/*     */   }
/*     */ 
/*     */   public synchronized String geo_country(long paramLong)
/*     */     throws Exception
/*     */   {
/* 118 */     this.cstmt_geo_country.setLong(1, paramLong);
/* 119 */     this.cstmt_geo_country.execute();
/* 120 */     return this.cstmt_geo_country.getString(2);
/*     */   }
/*     */ 
/*     */   public synchronized void oe_insert(metadata parammetadata, long paramLong, rollback_handler.RB_TYPE paramRB_TYPE)
/*     */     throws Exception
/*     */   {
/* 134 */     this.cstmt_oe_insert.setLong(1, parammetadata.rid);
/* 135 */     this.cstmt_oe_insert.setLong(2, parammetadata.pid);
/* 136 */     this.cstmt_oe_insert.setLong(3, parammetadata.timestamp);
/* 137 */     this.cstmt_oe_insert.setInt(4, parammetadata.namespace);
/* 138 */     this.cstmt_oe_insert.setString(5, escape_string.escape(parammetadata.user));
/* 139 */     this.cstmt_oe_insert.setLong(6, paramLong);
/* 140 */     this.cstmt_oe_insert.setString(7, parammetadata.country);
/* 141 */     if (paramRB_TYPE.equals(rollback_handler.RB_TYPE.HUMAN))
/* 142 */       this.cstmt_oe_insert.setInt(8, 1);
/*     */     else
/* 144 */       this.cstmt_oe_insert.setInt(8, 2);
/* 145 */     this.cstmt_oe_insert.execute();
/*     */   }
/*     */ 
/*     */   public synchronized stiki_utils.SCORE_SYS default_queue()
/*     */     throws Exception
/*     */   {
/* 153 */     this.cstmt_default_queue.execute();
/* 154 */     return stiki_utils.constant_to_queue(this.cstmt_default_queue.getInt(1));
/*     */   }
/*     */ 
/*     */   public synchronized boolean user_explicit(String paramString)
/*     */     throws Exception
/*     */   {
/* 164 */     this.cstmt_users_explicit.setString(1, paramString);
/* 165 */     this.cstmt_users_explicit.execute();
/* 166 */     return this.cstmt_users_explicit.getInt(2) > 0;
/*     */   }
/*     */ 
/*     */   public synchronized void ping()
/*     */     throws Exception
/*     */   {
/* 173 */     this.cstmt_ping.execute();
/*     */   }
/*     */ 
/*     */   public synchronized int[] recent_use(long paramLong)
/*     */     throws Exception
/*     */   {
/* 185 */     int[] arrayOfInt = new int[8];
/* 186 */     this.cstmt_recent_use.setLong(1, paramLong);
/* 187 */     this.cstmt_recent_use.execute();
/* 188 */     arrayOfInt[0] = this.cstmt_recent_use.getInt(2);
/* 189 */     arrayOfInt[1] = this.cstmt_recent_use.getInt(3);
/* 190 */     arrayOfInt[2] = this.cstmt_recent_use.getInt(4);
/* 191 */     arrayOfInt[3] = this.cstmt_recent_use.getInt(5);
/* 192 */     arrayOfInt[4] = this.cstmt_recent_use.getInt(6);
/* 193 */     arrayOfInt[5] = this.cstmt_recent_use.getInt(7);
/* 194 */     arrayOfInt[6] = this.cstmt_recent_use.getInt(8);
/* 195 */     arrayOfInt[7] = this.cstmt_recent_use.getInt(9);
/* 196 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public synchronized void shutdown()
/*     */     throws Exception
/*     */   {
/* 203 */     this.cstmt_feedback_insert.close();
/* 204 */     this.cstmt_geo_country.close();
/* 205 */     this.cstmt_oe_insert.close();
/* 206 */     this.cstmt_default_queue.close();
/* 207 */     this.cstmt_recent_use.close();
/* 208 */     this.cstmt_ping.close();
/* 209 */     this.con_client.con.close();
/*     */   }
/*     */ 
/*     */   private void prep_statements()
/*     */     throws Exception
/*     */   {
/* 220 */     this.cstmt_feedback_insert = this.con_client.con.prepareCall("{CALL client_feedback_insert(?,?,?)}");
/*     */ 
/* 223 */     this.cstmt_geo_country = this.con_client.con.prepareCall("{CALL client_geo_country(?,?)}");
/*     */ 
/* 225 */     this.cstmt_geo_country.registerOutParameter(2, 1);
/*     */ 
/* 227 */     this.cstmt_oe_insert = this.con_client.con.prepareCall("{CALL client_oe_insert(?,?,?,?,?,?,?,?)}");
/*     */ 
/* 230 */     this.cstmt_default_queue = this.con_client.con.prepareCall("{CALL client_default_queue(?)}");
/*     */ 
/* 232 */     this.cstmt_default_queue.registerOutParameter(1, 4);
/*     */ 
/* 234 */     this.cstmt_users_explicit = this.con_client.con.prepareCall("{CALL client_users_explicit(?,?)}");
/*     */ 
/* 236 */     this.cstmt_users_explicit.registerOutParameter(2, 4);
/*     */ 
/* 238 */     this.cstmt_recent_use = this.con_client.con.prepareCall("{CALL client_recent_use(?,?,?,?,?,?,?,?,?)}");
/*     */ 
/* 240 */     this.cstmt_recent_use.registerOutParameter(2, 4);
/* 241 */     this.cstmt_recent_use.registerOutParameter(3, 4);
/* 242 */     this.cstmt_recent_use.registerOutParameter(4, 4);
/* 243 */     this.cstmt_recent_use.registerOutParameter(5, 4);
/* 244 */     this.cstmt_recent_use.registerOutParameter(6, 4);
/* 245 */     this.cstmt_recent_use.registerOutParameter(7, 4);
/* 246 */     this.cstmt_recent_use.registerOutParameter(8, 4);
/* 247 */     this.cstmt_recent_use.registerOutParameter(9, 4);
/*     */ 
/* 249 */     this.cstmt_ping = this.con_client.con.prepareCall("{CALL client_ping()}");
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     db_client.client_interface
 * JD-Core Version:    0.6.0
 */