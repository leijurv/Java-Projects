/*    */ package mediawiki_api;
/*    */ 
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_login extends DefaultHandler
/*    */ {
/* 28 */   private String string_result = null;
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 39 */     if (!paramString3.equals("login")) {
/* 40 */       return;
/*    */     }
/* 42 */     String str1 = paramAttributes.getValue("result");
/* 43 */     if (str1.toUpperCase().equals("NEEDTOKEN")) {
/* 44 */       this.string_result = paramAttributes.getValue("token");
/*    */     }
/* 47 */     else if (str1.toUpperCase().equals("SUCCESS")) {
/* 48 */       String str2 = paramAttributes.getValue("cookieprefix");
/* 49 */       this.string_result = (str2 + "UserName=");
/* 50 */       this.string_result = (this.string_result + paramAttributes.getValue("lgusername") + "; ");
/* 51 */       this.string_result = (this.string_result + str2 + "UserID=");
/* 52 */       this.string_result = (this.string_result + paramAttributes.getValue("lguserid") + "; ");
/* 53 */       this.string_result = (this.string_result + str2 + "Token=");
/* 54 */       this.string_result = (this.string_result + paramAttributes.getValue("lgtoken") + "; ");
/* 55 */       this.string_result = (this.string_result + str2 + "_session=");
/* 56 */       this.string_result += paramAttributes.getValue("sessionid");
/*    */     }
/*    */   }
/*    */ 
/*    */   public String get_result()
/*    */   {
/* 69 */     return this.string_result;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_login
 * JD-Core Version:    0.6.0
 */