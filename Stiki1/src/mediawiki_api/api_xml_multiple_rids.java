/*     */ package mediawiki_api;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import db_client.client_interface;
/*     */ import db_server.db_geolocation;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class api_xml_multiple_rids extends DefaultHandler
/*     */ {
/*     */   private db_geolocation db_geo;
/*     */   private client_interface client_geo;
/*     */   private List<metadata> result_md_set;
/*     */   private boolean ignore_mode;
/*     */   private String id_rev;
/*     */   private String timestamp;
/*     */   private String title;
/*     */   private String id_page;
/*     */   private String namespace;
/*     */   private String user;
/*     */   private String comment;
/*     */   private String rb_token;
/*     */ 
/*     */   public api_xml_multiple_rids(db_geolocation paramdb_geolocation)
/*     */   {
/*  67 */     this.db_geo = paramdb_geolocation;
/*  68 */     this.client_geo = null;
/*  69 */     this.ignore_mode = false;
/*  70 */     this.result_md_set = new ArrayList();
/*     */   }
/*     */ 
/*     */   public api_xml_multiple_rids(client_interface paramclient_interface)
/*     */   {
/*  78 */     this.db_geo = null;
/*  79 */     this.client_geo = paramclient_interface;
/*  80 */     this.ignore_mode = false;
/*  81 */     this.result_md_set = new ArrayList();
/*     */   }
/*     */ 
/*     */   public api_xml_multiple_rids()
/*     */   {
/*  89 */     this.db_geo = null;
/*  90 */     this.client_geo = null;
/*  91 */     this.ignore_mode = false;
/*  92 */     this.result_md_set = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*     */     throws SAXException
/*     */   {
/* 104 */     if (paramString3.equals("badrevids"))
/* 105 */       this.ignore_mode = true;
/* 106 */     if (this.ignore_mode) {
/* 107 */       return;
/*     */     }
/* 109 */     if (paramString3.equals("page")) {
/* 110 */       this.title = paramAttributes.getValue("title");
/* 111 */       this.id_page = paramAttributes.getValue("pageid");
/* 112 */       this.namespace = paramAttributes.getValue("ns");
/* 113 */     } else if (paramString3.equals("rev")) {
/* 114 */       this.id_rev = paramAttributes.getValue("revid");
/* 115 */       this.timestamp = paramAttributes.getValue("timestamp");
/* 116 */       this.comment = paramAttributes.getValue("comment");
/* 117 */       this.rb_token = paramAttributes.getValue("rollbacktoken");
/*     */ 
/* 119 */       this.user = paramAttributes.getValue("user");
/* 120 */       if (this.user == null)
/* 121 */         this.user = paramAttributes.getValue("userhidden");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endElement(String paramString1, String paramString2, String paramString3)
/*     */     throws SAXException
/*     */   {
/* 131 */     if (paramString3.equals("badrevids"))
/* 132 */       this.ignore_mode = false;
/* 133 */     if (this.ignore_mode) {
/* 134 */       return;
/*     */     }
/* 136 */     if (paramString3.equals("page")) {
/* 137 */       reset_page_level();
/* 138 */     } else if (paramString3.equals("rev"))
/*     */     {
/*     */       try {
/* 141 */         if (this.client_geo != null) {
/* 142 */           this.result_md_set.add(new metadata(this.id_rev, this.timestamp, this.title, this.id_page, this.namespace, this.user, this.comment, this.rb_token, this.client_geo));
/*     */         }
/*     */         else
/*     */         {
/* 146 */           this.result_md_set.add(new metadata(this.id_rev, this.timestamp, this.title, this.id_page, this.namespace, this.user, this.comment, this.rb_token, this.db_geo));
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 151 */         System.out.println("Failed to populate metadata object");
/* 152 */         System.out.println("RID in question is: " + this.id_rev);
/* 153 */         localException.printStackTrace();
/*     */       }
/*     */ 
/* 156 */       reset_rev_level();
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<metadata> get_result()
/*     */   {
/* 167 */     return this.result_md_set;
/*     */   }
/*     */ 
/*     */   private void reset_rev_level()
/*     */   {
/* 180 */     this.id_rev = "";
/* 181 */     this.timestamp = "";
/* 182 */     this.user = "";
/* 183 */     this.comment = "";
/* 184 */     this.rb_token = "";
/*     */   }
/*     */ 
/*     */   private void reset_page_level()
/*     */   {
/* 192 */     this.title = "";
/* 193 */     this.id_page = "";
/* 194 */     this.namespace = "";
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_multiple_rids
 * JD-Core Version:    0.6.0
 */