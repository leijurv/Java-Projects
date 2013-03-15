/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_page_protected extends DefaultHandler
/*    */ {
/* 21 */   private boolean is_protected = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 32 */     if ((paramString3.equals("flagged")) && 
/* 33 */       (paramAttributes.getValue("type").equals("edit")))
/* 34 */       this.is_protected = true;
/*    */   }
/*    */ 
/*    */   public boolean get_result()
/*    */   {
/* 45 */     return this.is_protected;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_page_protected
 * JD-Core Version:    0.6.0
 */