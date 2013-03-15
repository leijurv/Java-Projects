/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_page_missing extends DefaultHandler
/*    */ {
/* 20 */   private boolean result_page_missing = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 31 */     if (paramString3.equals("page")) {
/* 32 */       String str = paramAttributes.getValue("missing");
/* 33 */       if (str != null)
/* 34 */         this.result_page_missing = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean get_result()
/*    */   {
/* 43 */     return this.result_page_missing;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_page_missing
 * JD-Core Version:    0.6.0
 */