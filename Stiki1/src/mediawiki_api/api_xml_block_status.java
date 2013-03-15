/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_block_status extends DefaultHandler
/*    */ {
/* 21 */   private boolean is_blocked = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 35 */     if (paramString3.equals("block"))
/* 36 */       this.is_blocked = true;
/*    */   }
/*    */ 
/*    */   public boolean get_result()
/*    */   {
/* 45 */     return this.is_blocked;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_block_status
 * JD-Core Version:    0.6.0
 */