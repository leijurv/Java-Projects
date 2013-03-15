/*    */ package mediawiki_api;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_latest_page extends DefaultHandler
/*    */ {
/*    */   private Map<Long, Long> pid_rid_result;
/*    */ 
/*    */   public api_xml_latest_page()
/*    */   {
/* 32 */     this.pid_rid_result = new HashMap();
/*    */   }
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 44 */     if (paramString3.equals("page")) {
/* 45 */       long l2 = Long.parseLong(paramAttributes.getValue("pageid"));
/*    */       long l1;
/* 46 */       if (paramAttributes.getValue("lastrevid") != null)
/* 47 */         l1 = Long.parseLong(paramAttributes.getValue("lastrevid"));
/* 48 */       else l1 = -1L;
/* 49 */       this.pid_rid_result.put(Long.valueOf(l2), Long.valueOf(l1));
/*    */     }
/*    */   }
/*    */ 
/*    */   public Map<Long, Long> get_result()
/*    */   {
/* 58 */     return this.pid_rid_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_latest_page
 * JD-Core Version:    0.6.0
 */