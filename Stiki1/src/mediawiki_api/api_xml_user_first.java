/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.stiki_utils;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_user_first extends DefaultHandler
/*    */ {
/* 22 */   private Long user_first_edit_ts_result = Long.valueOf(-1L);
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 33 */     if (paramString3.equals("item"))
/* 34 */       this.user_first_edit_ts_result = Long.valueOf(stiki_utils.wiki_ts_to_unix(paramAttributes.getValue("timestamp")));
/*    */   }
/*    */ 
/*    */   public Long get_result()
/*    */   {
/* 45 */     return this.user_first_edit_ts_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_user_first
 * JD-Core Version:    0.6.0
 */