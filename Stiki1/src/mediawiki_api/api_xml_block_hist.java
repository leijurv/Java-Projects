/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.pair;
/*    */ import core_objects.stiki_utils;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_block_hist extends DefaultHandler
/*    */ {
/* 27 */   private List<pair<Long, String>> blk_hist = new ArrayList();
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 39 */     if (paramString3.equals("item")) {
/* 40 */       Long localLong = Long.valueOf(stiki_utils.wiki_ts_to_unix(paramAttributes.getValue("timestamp")));
/*    */ 
/* 42 */       String str = paramAttributes.getValue("action");
/* 43 */       this.blk_hist.add(new pair(localLong, str));
/*    */     }
/*    */   }
/*    */ 
/*    */   public List<pair<Long, String>> get_result()
/*    */   {
/* 55 */     return this.blk_hist;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_block_hist
 * JD-Core Version:    0.6.0
 */