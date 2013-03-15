/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_diff_text extends DefaultHandler
/*    */ {
/* 20 */   private StringBuilder diff_string_result = new StringBuilder();
/*    */ 
/* 26 */   private boolean diff_active = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 37 */     if (paramString3.equals("diff"))
/* 38 */       this.diff_active = true;
/*    */   }
/*    */ 
/*    */   public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*    */     throws SAXException
/*    */   {
/* 48 */     if (this.diff_active)
/* 49 */       this.diff_string_result.append(String.valueOf(paramArrayOfChar, paramInt1, paramInt2));
/*    */   }
/*    */ 
/*    */   public void endElement(String paramString1, String paramString2, String paramString3)
/*    */     throws SAXException
/*    */   {
/* 58 */     if (paramString3.equals("diff"))
/* 59 */       this.diff_active = false;
/*    */   }
/*    */ 
/*    */   public String get_result()
/*    */   {
/* 67 */     return this.diff_string_result.toString();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_diff_text
 * JD-Core Version:    0.6.0
 */