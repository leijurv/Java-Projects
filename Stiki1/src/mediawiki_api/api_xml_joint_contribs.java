/*    */ package mediawiki_api;
/*    */ 
/*    */ import core_objects.pair;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_joint_contribs extends DefaultHandler
/*    */ {
/*    */   private List<String> USERS;
/*    */   private List<pair<Long, Long>> CONTRIBS;
/*    */ 
/*    */   public api_xml_joint_contribs(List<String> paramList)
/*    */   {
/* 42 */     this.USERS = paramList;
/* 43 */     this.CONTRIBS = new ArrayList();
/*    */   }
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 56 */       if (paramString3.equals("item")) {
/* 57 */         this.CONTRIBS.add(new pair(Long.valueOf(Long.parseLong(paramAttributes.getValue("revid"))), Long.valueOf(Long.parseLong(paramAttributes.getValue("pageid")))));
/*    */       }
/* 60 */       else if (paramString3.equals("usercontribs"))
/*    */       {
/*    */         String str;
/* 61 */         if (paramAttributes.getValue("ucstart") != null) {
/* 62 */           str = paramAttributes.getValue("ucstart");
/* 63 */           this.CONTRIBS.addAll(api_retrieve.process_joint_contribs(this.USERS, str));
/*    */         }
/* 65 */         else if (paramAttributes.getValue("uccontinue") != null) {
/* 66 */           str = paramAttributes.getValue("ucstart").split("|")[1];
/* 67 */           this.CONTRIBS.addAll(api_retrieve.process_joint_contribs(this.USERS, str));
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception localException) {
/* 72 */       localException.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public List<pair<Long, Long>> get_result()
/*    */   {
/* 83 */     return this.CONTRIBS;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_joint_contribs
 * JD-Core Version:    0.6.0
 */