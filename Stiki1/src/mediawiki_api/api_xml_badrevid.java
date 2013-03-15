/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_badrevid extends DefaultHandler
/*    */ {
/* 20 */   private boolean result_badrevid = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 31 */     if (paramString3.equals("badrevids"))
/* 32 */       this.result_badrevid = true;
/*    */   }
/*    */ 
/*    */   public boolean get_result()
/*    */   {
/* 41 */     return this.result_badrevid;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_badrevid
 * JD-Core Version:    0.6.0
 */