/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_find_off extends DefaultHandler
/*    */ {
/*    */   private Long offending_rid_result;
/*    */   private String uc_off;
/* 34 */   private boolean finished = false;
/*    */ 
/*    */   public api_xml_find_off(String paramString)
/*    */   {
/* 44 */     this.offending_rid_result = Long.valueOf(-1L);
/* 45 */     this.uc_off = paramString;
/*    */   }
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 54 */     if (this.finished) {
/* 55 */       return;
/*    */     }
/* 57 */     if (paramString3.equals("rev"))
/*    */     {
/* 59 */       String str = paramAttributes.getValue("user");
/* 60 */       if (str == null)
/* 61 */         return;
/* 62 */       if (str.toUpperCase().equals(this.uc_off)) {
/* 63 */         this.offending_rid_result = Long.valueOf(Long.parseLong(paramAttributes.getValue("revid")));
/*    */ 
/* 65 */         this.finished = true;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void endElement(String paramString1, String paramString2, String paramString3)
/*    */     throws SAXException
/*    */   {
/* 76 */     if (paramString3.equals("revisions"))
/* 77 */       this.finished = true;
/*    */   }
/*    */ 
/*    */   public Long get_result()
/*    */   {
/* 85 */     return this.offending_rid_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_find_off
 * JD-Core Version:    0.6.0
 */