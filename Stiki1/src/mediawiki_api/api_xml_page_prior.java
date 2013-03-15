/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.stiki_utils;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_page_prior extends DefaultHandler
/*    */ {
/* 23 */   private Long rid_page_prior_result = Long.valueOf(-1L);
/*    */ 
/* 30 */   private boolean first_done = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 41 */     if (paramString3.equals("rev"))
/* 42 */       if (!this.first_done)
/* 43 */         this.first_done = true;
/* 44 */       else this.rid_page_prior_result = Long.valueOf(stiki_utils.wiki_ts_to_unix(paramAttributes.getValue("timestamp")));
/*    */   }
/*    */ 
/*    */   public Long get_result()
/*    */   {
/* 55 */     return this.rid_page_prior_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_page_prior
 * JD-Core Version:    0.6.0
 */