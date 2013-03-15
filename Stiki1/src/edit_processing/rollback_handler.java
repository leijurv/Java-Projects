/*     */ package edit_processing;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.pair;
/*     */ import db_server.db_country;
/*     */ import db_server.db_edits;
/*     */ import db_server.db_features;
/*     */ import db_server.db_geolocation;
/*     */ import db_server.db_hyperlinks;
/*     */ import db_server.db_off_edits;
/*     */ import java.io.PrintStream;
/*     */ import mediawiki_api.api_retrieve;
/*     */ import mediawiki_api.api_xml_user_perm;
/*     */ 
/*     */ public class rollback_handler
/*     */ {
/*  37 */   public static final String[] RB_REGEX = { "REVERTED EDIT.* BY .* TO LAST VERSION BY .*", "\\[\\[(WIKIPEDIA|WP):RBK\\|REVERTED\\]\\] EDIT.* BY .* TO LAST VERSION BY .*", "\\[\\[HELP:REVERTING\\|REVERTED\\]\\] EDIT.* BY .* TO LAST VERSION BY .*", "REVERTED EDIT.* BY .* TO LAST REVISION BY .*", "REVERTED .* EDIT.* BY .* IDENTIFIED AS \\[\\[(WIKIPEDIA|WP):VAND\\|VANDALISM\\]\\].*", "REVERTING POSSIBLE VANDALISM BY .* TO VERSION BY .* THANKS, \\[\\[USER:CLUEBOT\\|CLUEBOT\\]\\].*", "REVERTING POSSIBLE VANDALISM BY .* TO VERSION BY .* THANKS, \\[\\[USER:CLUEBOT NG\\|CLUEBOT NG\\]\\].*" };
/*     */ 
/*  51 */   public static final RB_TYPE[] REGEX_TYPE = { RB_TYPE.HUMAN, RB_TYPE.HUMAN, RB_TYPE.HUMAN, RB_TYPE.HUMAN, RB_TYPE.HUMAN, RB_TYPE.BOT, RB_TYPE.BOT };
/*     */   public static final int SEARCH_DEPTH = 10;
/*     */ 
/*     */   public static void new_edit(metadata parammetadata, db_off_edits paramdb_off_edits, db_geolocation paramdb_geolocation, db_edits paramdb_edits, db_features paramdb_features, db_country paramdb_country, db_hyperlinks paramdb_hyperlinks)
/*     */     throws Exception
/*     */   {
/*  85 */     pair localpair = find_oe(parammetadata);
/*  86 */     if (((Long)localpair.fst).longValue() == -1L) {
/*  87 */       parammetadata.set_is_rb(false);
/*  88 */       return;
/*     */     }
/*     */ 
/*  92 */     parammetadata.set_is_rb(true);
/*     */ 
/*  94 */     metadata localmetadata = api_retrieve.process_basic_rid(((Long)localpair.fst).longValue(), paramdb_geolocation);
/*  95 */     if ((localmetadata != null) && (parammetadata.rid != localmetadata.rid))
/*  96 */       paramdb_off_edits.new_oe(localmetadata, parammetadata.rid, (RB_TYPE)localpair.snd, paramdb_edits, paramdb_features, paramdb_country, paramdb_hyperlinks);
/*     */   }
/*     */ 
/*     */   private static pair<Long, RB_TYPE> find_oe(metadata parammetadata)
/*     */     throws Exception
/*     */   {
/* 116 */     RB_TYPE localRB_TYPE = RB_TYPE.NONE;
/* 117 */     int i = 0;
/* 118 */     String str1 = parammetadata.comment.toUpperCase();
/* 119 */     for (int j = 0; j < RB_REGEX.length; j++) {
/* 120 */       if (str1.matches(RB_REGEX[j])) {
/* 121 */         localRB_TYPE = REGEX_TYPE[j];
/* 122 */         i = 1;
/* 123 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 127 */     if (i == 0) {
/* 128 */       return new pair(Long.valueOf(-1L), RB_TYPE.NONE);
/*     */     }
/* 130 */     if (!localRB_TYPE.equals(RB_TYPE.BOT)) {
/* 131 */       boolean bool = api_xml_user_perm.has_rollback(api_retrieve.process_user_perm(parammetadata.user));
/*     */ 
/* 133 */       if (!bool) {
/* 134 */         return new pair(Long.valueOf(-1L), RB_TYPE.NONE);
/*     */       }
/*     */     }
/*     */ 
/* 138 */     String str2 = parse_offender(str1);
/* 139 */     if ((str2.equals("")) || (str2.equals(parammetadata.user))) {
/* 140 */       return new pair(Long.valueOf(-1L), RB_TYPE.NONE);
/*     */     }
/*     */ 
/* 143 */     long l = api_retrieve.process_offender_search(str2, parammetadata.pid, parammetadata.rid, 10);
/*     */ 
/* 145 */     if (l == -1L) {
/* 146 */       return new pair(Long.valueOf(-1L), RB_TYPE.NONE);
/*     */     }
/*     */ 
/* 149 */     return new pair(Long.valueOf(l), localRB_TYPE);
/*     */   }
/*     */ 
/*     */   private static String parse_offender(String paramString)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 165 */       String[] arrayOfString = paramString.split(" BY | TO LAST | TO VERSION | IDENTIFIED ");
/*     */ 
/* 167 */       String str1 = arrayOfString[1].trim();
/*     */ 
/* 171 */       if (str1.matches(".*\\(.*\\)")) {
/* 172 */         if (str1.matches("\\[\\[.*\\]\\].*\\(.*\\)")) {
/* 173 */           str1 = str1.split("\\]\\]")[0];
/* 174 */           str1 = str1 + "]]";
/*     */         } else {
/* 176 */           str1 = str1.substring(0, str1.indexOf('(')).trim();
/*     */         }
/*     */       }
/*     */ 
/* 180 */       String str2 = "\\[\\[.*\\|.*\\]\\]";
/* 181 */       String str3 = "\\[\\[USER:.*\\]\\]";
/* 182 */       String str4 = "\\[\\[.*\\]\\]";
/* 183 */       String str5 = ".* (TALK)";
/*     */ 
/* 185 */       if (str1.matches(str2))
/* 186 */         return str1.split("\\||\\]\\]")[1].trim();
/* 187 */       if (str1.matches(str3))
/* 188 */         return str1.substring(7, str1.length() - 2);
/* 189 */       if (str1.matches(str4))
/* 190 */         return str1.substring(2, str1.length() - 2);
/* 191 */       if (str1.matches(str5)) {
/* 192 */         return str1.substring(0, str1.length() - 7);
/*     */       }
/* 194 */       return str1;
/*     */     }
/*     */     catch (Exception localException) {
/* 197 */       System.out.println("Offender-parse failed: " + paramString);
/* 198 */     }return "";
/*     */   }
/*     */ 
/*     */   public static enum RB_TYPE
/*     */   {
/*  29 */     HUMAN, BOT, NONE;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     edit_processing.rollback_handler
 * JD-Core Version:    0.6.0
 */