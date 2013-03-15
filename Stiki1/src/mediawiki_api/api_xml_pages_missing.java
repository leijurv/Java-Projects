/*    */ package mediawiki_api;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.TreeMap;
/*    */ import java.util.TreeSet;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_pages_missing extends DefaultHandler
/*    */ {
/* 27 */   private Set<String> result_missing_pages = new TreeSet();
/*    */ 
/* 35 */   private Map<String, String> normalize_map = new TreeMap();
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/*    */     String str1;
/* 46 */     if (paramString3.equals("n")) {
/* 47 */       str1 = paramAttributes.getValue("from");
/* 48 */       String str2 = paramAttributes.getValue("to");
/* 49 */       this.normalize_map.put(str2, str1);
/*    */     }
/* 52 */     else if ((paramString3.equals("page")) && 
/* 53 */       (paramAttributes.getValue("missing") != null)) {
/* 54 */       str1 = paramAttributes.getValue("title");
/* 55 */       if (this.normalize_map.containsKey(str1))
/* 56 */         this.result_missing_pages.add(this.normalize_map.get(str1));
/* 57 */       else this.result_missing_pages.add(str1);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Set<String> get_result()
/*    */   {
/* 68 */     return this.result_missing_pages;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_pages_missing
 * JD-Core Version:    0.6.0
 */