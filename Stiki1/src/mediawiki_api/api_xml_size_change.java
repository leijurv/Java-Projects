/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_size_change extends DefaultHandler
/*    */ {
/* 25 */   private int change = 0;
/*    */ 
/* 31 */   private boolean first_done = false;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 42 */     if (paramString3.equals("rev"))
/*    */     {
/* 47 */       if (paramAttributes.getValue("size") == null) {
/* 48 */         this.change = 0;
/*    */       }
/* 50 */       else if (this.first_done) {
/* 51 */         this.change -= Integer.parseInt(paramAttributes.getValue("size"));
/*    */       } else {
/* 53 */         this.change = Integer.parseInt(paramAttributes.getValue("size"));
/* 54 */         this.first_done = true;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public int get_result()
/*    */   {
/* 66 */     return this.change;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_size_change
 * JD-Core Version:    0.6.0
 */