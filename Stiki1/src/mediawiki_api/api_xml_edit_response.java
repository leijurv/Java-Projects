/*    */ package mediawiki_api;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_edit_response extends DefaultHandler
/*    */ {
/* 23 */   private api_post.EDIT_OUTCOME edit_result = api_post.EDIT_OUTCOME.ERROR;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 34 */     if (paramString3.equals("edit")) {
/* 35 */       if (paramAttributes.getValue("result").toUpperCase().equals("SUCCESS"))
/* 36 */         this.edit_result = api_post.EDIT_OUTCOME.SUCCESS;
/* 37 */       if (paramAttributes.getValue("nochange") != null) {
/* 38 */         this.edit_result = api_post.EDIT_OUTCOME.BEATEN;
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 43 */     if (paramString3.equals("error")) try {
/* 44 */         System.err.println("An edit (not rollback) failed to commit. The server returned code \"" + paramAttributes.getValue("code") + "\" and details \"" + paramAttributes.getValue("info") + "\"");
/*    */       }
/*    */       catch (Exception localException)
/*    */       {
/*    */       }
/*    */   }
/*    */ 
/*    */   public api_post.EDIT_OUTCOME get_result()
/*    */   {
/* 61 */     return this.edit_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_edit_response
 * JD-Core Version:    0.6.0
 */