/*    */ package mediawiki_api;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_rb_response extends DefaultHandler
/*    */ {
/*    */   private long earliest_rid_rbed;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 33 */     if (paramString3.equals("rollback"))
/*    */     {
/* 35 */       if ((paramAttributes.getValue("revid") == null) || (paramAttributes.getValue("old_revid") == null) || (paramAttributes.getValue("last_revid") == null))
/*    */       {
/* 38 */         this.earliest_rid_rbed = -1L;
/* 39 */       } else if (paramAttributes.getValue("revid") == paramAttributes.getValue("old_revid"))
/*    */       {
/* 41 */         this.earliest_rid_rbed = 0L;
/*    */       }
/* 43 */       else this.earliest_rid_rbed = Long.parseLong(paramAttributes.getValue("last_revid"));
/*    */ 
/*    */     }
/*    */ 
/* 48 */     if (paramString3.equals("error"))
/*    */       try {
/* 50 */         if (!paramAttributes.getValue("code").equals("alreadyrolled")) {
/* 51 */           System.err.println("A rollback failed to commit. The server returned code \"" + paramAttributes.getValue("code") + "\" and details \"" + paramAttributes.getValue("info") + "\"");
/*    */ 
/* 55 */           if (paramAttributes.getValue("code").equals("badtoken"))
/* 56 */             this.earliest_rid_rbed = -2L;
/*    */         }
/*    */       }
/*    */       catch (Exception localException)
/*    */       {
/*    */       }
/*    */   }
/*    */ 
/*    */   public long get_result()
/*    */   {
/* 73 */     return this.earliest_rid_rbed;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_rb_response
 * JD-Core Version:    0.6.0
 */