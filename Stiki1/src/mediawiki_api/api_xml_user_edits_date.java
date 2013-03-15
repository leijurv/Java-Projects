/*     */ package mediawiki_api;
/*     */ 
/*     */ import core_objects.stiki_utils;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class api_xml_user_edits_date extends DefaultHandler
/*     */ {
/*     */   private String user;
/*     */   private int ns;
/*     */   private long break_num;
/*     */   private int batch_size;
/*     */   private long edits;
/*     */ 
/*     */   public api_xml_user_edits_date(String paramString, int paramInt1, long paramLong1, long paramLong2, int paramInt2)
/*     */   {
/*  70 */     this.user = paramString;
/*  71 */     this.ns = paramInt1;
/*  72 */     this.edits = paramLong1;
/*  73 */     this.break_num = paramLong2;
/*  74 */     this.batch_size = paramInt2;
/*     */   }
/*     */ 
/*     */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*     */     throws SAXException
/*     */   {
/*  86 */     if ((paramString3.equals("item")) && (paramAttributes.getValue("timestamp") != null)) {
/*  87 */       this.edits += 1L;
/*  88 */     } else if ((paramString3.equals("usercontribs")) && (paramAttributes.getValue("ucstart") != null))
/*     */     {
/*  91 */       if (this.edits >= this.break_num)
/*  92 */         return;
/*  93 */       long l = stiki_utils.wiki_ts_to_unix(paramAttributes.getValue("ucstart"));
/*     */       try {
/*  95 */         this.edits = api_retrieve.process_user_edits(this.user, this.ns, l, this.edits, this.break_num, this.batch_size);
/*     */       } catch (Exception localException) {
/*  97 */         localException.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long get_result()
/*     */   {
/* 107 */     return this.edits;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_user_edits_date
 * JD-Core Version:    0.6.0
 */