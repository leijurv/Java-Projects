/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_page_content extends DefaultHandler
/*    */ {
/* 21 */   private StringBuilder page_content = new StringBuilder();
/*    */ 
/* 28 */   private boolean at_content = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 38 */     if (paramString3.equals("rev"))
/* 39 */       this.at_content = true;
/*    */   }
/*    */ 
/*    */   public void endElement(String paramString1, String paramString2, String paramString3)
/*    */   {
/* 46 */     if (paramString3.equals("rev"))
/* 47 */       this.at_content = false;
/*    */   }
/*    */ 
/*    */   public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*    */   {
/* 58 */     if (this.at_content)
/* 59 */       this.page_content.append(String.valueOf(paramArrayOfChar, paramInt1, paramInt2));
/*    */   }
/*    */ 
/*    */   public String get_result()
/*    */   {
/* 68 */     return this.page_content.toString();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_page_content
 * JD-Core Version:    0.6.0
 */