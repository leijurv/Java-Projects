/*     */ package core_objects;
/*     */ 
/*     */ import db_client.client_interface;
/*     */ import db_server.db_geolocation;
/*     */ import mediawiki_api.api_retrieve;
/*     */ 
/*     */ public class metadata
/*     */ {
/*     */   public final long rid;
/*     */   public final long pid;
/*     */   public final long timestamp;
/*     */   public final int namespace;
/*     */   public final String title;
/*     */   public final String user;
/*     */   public final boolean user_is_ip;
/*     */   public final String comment;
/*     */   public final String country;
/*     */   public String rb_token;
/*  82 */   private boolean is_rb = false;
/*     */ 
/*     */   public metadata(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, db_geolocation paramdb_geolocation)
/*     */     throws Exception
/*     */   {
/* 105 */     this.namespace = Integer.parseInt(paramString5);
/* 106 */     this.rid = Long.parseLong(paramString1);
/* 107 */     this.pid = Long.parseLong(paramString4);
/* 108 */     this.title = paramString3;
/* 109 */     this.rb_token = paramString8;
/*     */ 
/* 113 */     if (paramString7 == null) this.comment = ""; else {
/* 114 */       this.comment = paramString7;
/*     */     }
/*     */ 
/* 117 */     this.user = paramString6;
/* 118 */     if (this.user.matches("(\\d)+\\.(\\d)+\\.(\\d)+\\.(\\d)+"))
/* 119 */       this.user_is_ip = true;
/* 120 */     else this.user_is_ip = false;
/*     */ 
/* 123 */     if ((!this.user_is_ip) || (paramdb_geolocation == null))
/* 124 */       this.country = "";
/*     */     else {
/* 126 */       this.country = paramdb_geolocation.get_country_code(stiki_utils.ip_to_long(this.user));
/*     */     }
/*     */ 
/* 130 */     this.timestamp = stiki_utils.wiki_ts_to_unix(paramString2);
/*     */   }
/*     */ 
/*     */   public metadata(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, client_interface paramclient_interface)
/*     */     throws Exception
/*     */   {
/* 148 */     this.namespace = Integer.parseInt(paramString5);
/* 149 */     this.rid = Long.parseLong(paramString1);
/* 150 */     this.pid = Long.parseLong(paramString4);
/* 151 */     this.title = paramString3;
/* 152 */     this.rb_token = paramString8;
/*     */ 
/* 156 */     if (paramString7 == null) this.comment = ""; else {
/* 157 */       this.comment = paramString7;
/*     */     }
/*     */ 
/* 160 */     this.user = paramString6;
/* 161 */     if (this.user.matches("(\\d)+\\.(\\d)+\\.(\\d)+\\.(\\d)+"))
/* 162 */       this.user_is_ip = true;
/* 163 */     else this.user_is_ip = false;
/*     */ 
/* 166 */     if ((!this.user_is_ip) || (paramclient_interface == null))
/* 167 */       this.country = "";
/*     */     else {
/* 169 */       this.country = paramclient_interface.geo_country(stiki_utils.ip_to_long(this.user));
/*     */     }
/*     */ 
/* 173 */     this.timestamp = stiki_utils.wiki_ts_to_unix(paramString2);
/*     */   }
/*     */ 
/*     */   public metadata()
/*     */   {
/* 182 */     this.rid = 0L;
/* 183 */     this.pid = 0L;
/* 184 */     this.timestamp = 0L;
/* 185 */     this.namespace = 0;
/* 186 */     this.title = "";
/* 187 */     this.user = "";
/* 188 */     this.user_is_ip = false;
/* 189 */     this.comment = "";
/* 190 */     this.rb_token = "";
/* 191 */     this.country = "";
/* 192 */     this.is_rb = false;
/*     */   }
/*     */ 
/*     */   public void refresh_rb_token(String paramString)
/*     */     throws Exception
/*     */   {
/* 204 */     this.rb_token = api_retrieve.process_basic_rid(this.rid, paramString).rb_token;
/*     */   }
/*     */ 
/*     */   public void set_is_rb(boolean paramBoolean)
/*     */   {
/* 213 */     this.is_rb = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean get_is_rb()
/*     */   {
/* 221 */     return this.is_rb;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     core_objects.metadata
 * JD-Core Version:    0.6.0
 */