/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.stiki_utils;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_autoconfirmed extends DefaultHandler
/*    */ {
/* 21 */   private static int MIN_EDITS = 10;
/*    */ 
/* 26 */   private static int MIN_TIME_SECS = 345600;
/*    */   private String user;
/* 36 */   private boolean autoconfirmed_result = false;
/*    */ 
/*    */   public api_xml_autoconfirmed(String paramString)
/*    */   {
/* 46 */     this.user = paramString;
/*    */   }
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 61 */     if (paramString3.equals("u")) {
/* 62 */       if (!paramAttributes.getValue("name").equals(this.user)) {
/* 63 */         return;
/*    */       }
/* 65 */       if (Integer.parseInt(paramAttributes.getValue("edit_count")) < MIN_EDITS) {
/* 66 */         return;
/*    */       }
/* 68 */       if (stiki_utils.wiki_ts_to_unix(paramAttributes.getValue("registration")) + MIN_TIME_SECS > stiki_utils.cur_unix_time())
/*    */       {
/* 70 */         return;
/*    */       }
/*    */ 
/* 73 */       this.autoconfirmed_result = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean get_result()
/*    */   {
/* 83 */     return this.autoconfirmed_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_autoconfirmed
 * JD-Core Version:    0.6.0
 */