/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.pair;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_edit_token extends DefaultHandler
/*    */ {
/*    */   private pair<String, String> token_result;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 34 */     if (paramString3.equals("page")) {
/* 35 */       String str1 = paramAttributes.getValue("edittoken");
/* 36 */       String str2 = paramAttributes.getValue("starttimestamp");
/*    */ 
/* 40 */       str2 = str2.replaceAll("\\D*", "");
/* 41 */       this.token_result = new pair(str1, str2);
/*    */     }
/*    */   }
/*    */ 
/*    */   public pair<String, String> get_result()
/*    */   {
/* 51 */     return this.token_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_edit_token
 * JD-Core Version:    0.6.0
 */