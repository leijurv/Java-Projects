/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_user_edits_ever extends DefaultHandler
/*    */ {
/* 22 */   private long edits = -1L;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 33 */     if ((paramString3.equals("user")) && 
/* 34 */       (paramAttributes.getValue("editcount") != null))
/* 35 */       this.edits = Long.parseLong(paramAttributes.getValue("editcount"));
/*    */   }
/*    */ 
/*    */   public long get_result()
/*    */   {
/* 45 */     return this.edits;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_user_edits_ever
 * JD-Core Version:    0.6.0
 */