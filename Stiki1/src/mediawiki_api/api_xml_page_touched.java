/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.stiki_utils;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_page_touched extends DefaultHandler
/*    */ {
/* 25 */   private Map<String, Long> touch_times = new TreeMap();
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 36 */     if ((paramString3.equals("page")) && 
/* 37 */       (paramAttributes.getValue("missing") == null))
/* 38 */       this.touch_times.put(paramAttributes.getValue("title"), Long.valueOf(stiki_utils.wiki_ts_to_unix(paramAttributes.getValue("touched"))));
/*    */   }
/*    */ 
/*    */   public Map<String, Long> get_result()
/*    */   {
/* 51 */     return this.touch_times;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_page_touched
 * JD-Core Version:    0.6.0
 */