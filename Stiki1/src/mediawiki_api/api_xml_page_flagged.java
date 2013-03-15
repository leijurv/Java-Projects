/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_page_flagged extends DefaultHandler
/*    */ {
/* 21 */   private boolean is_flagged = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 34 */     if (paramString3.equals("flagged"))
/* 35 */       this.is_flagged = true;
/*    */   }
/*    */ 
/*    */   public boolean get_result()
/*    */   {
/* 44 */     return this.is_flagged;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_page_flagged
 * JD-Core Version:    0.6.0
 */