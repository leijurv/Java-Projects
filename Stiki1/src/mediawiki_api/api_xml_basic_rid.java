/*     */ package mediawiki_api;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import db_server.db_geolocation;
/*     */ import java.io.PrintStream;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class api_xml_basic_rid extends DefaultHandler
/*     */ {
/*     */   private db_geolocation db_geo;
/*  28 */   private metadata result_metadata = null;
/*     */ 
/*  34 */   private boolean error = false;
/*     */   private String id_rev;
/*     */   private String timestamp;
/*     */   private String title;
/*     */   private String id_page;
/*     */   private String namespace;
/*     */   private String user;
/*     */   private String comment;
/*     */   private String rb_token;
/*     */ 
/*     */   public api_xml_basic_rid(db_geolocation paramdb_geolocation)
/*     */   {
/*  54 */     this.db_geo = paramdb_geolocation;
/*     */   }
/*     */ 
/*     */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*     */     throws SAXException
/*     */   {
/*  66 */     if (this.error) {
/*  67 */       return;
/*     */     }
/*  69 */     if (paramString3.equals("page")) {
/*  70 */       this.title = paramAttributes.getValue("title");
/*  71 */       this.id_page = paramAttributes.getValue("pageid");
/*  72 */       this.namespace = paramAttributes.getValue("ns");
/*  73 */     } else if (paramString3.equals("rev")) {
/*  74 */       this.id_rev = paramAttributes.getValue("revid");
/*  75 */       this.timestamp = paramAttributes.getValue("timestamp");
/*  76 */       this.comment = paramAttributes.getValue("comment");
/*  77 */       this.rb_token = paramAttributes.getValue("rollbacktoken");
/*     */ 
/*  79 */       this.user = paramAttributes.getValue("user");
/*  80 */       if (this.user == null)
/*  81 */         this.user = paramAttributes.getValue("userhidden");
/*     */     }
/*  83 */     else if (paramString3.equals("badrevids")) {
/*  84 */       this.error = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endElement(String paramString1, String paramString2, String paramString3)
/*     */     throws SAXException
/*     */   {
/*  94 */     if ((!this.error) && (paramString3.equals("rev")))
/*     */       try {
/*  96 */         this.result_metadata = new metadata(this.id_rev, this.timestamp, this.title, this.id_page, this.namespace, this.user, this.comment, this.rb_token, this.db_geo);
/*     */       }
/*     */       catch (Exception localException) {
/*  99 */         System.out.println("Failed to populate metadata object:");
/* 100 */         System.out.println("RID in question is: " + this.id_rev);
/* 101 */         localException.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public metadata get_result()
/*     */   {
/* 111 */     return this.result_metadata;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_basic_rid
 * JD-Core Version:    0.6.0
 */