/*    */ package mediawiki_api;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class api_xml_user_perm extends DefaultHandler
/*    */ {
/*    */   private Set<String> user_perms;
/* 29 */   private boolean g_active = false;
/*    */ 
/*    */   public static boolean has_rollback(Set<String> paramSet)
/*    */   {
/* 41 */     return (paramSet.contains("rollbacker")) || (paramSet.contains("sysop"));
/*    */   }
/*    */ 
/*    */   public api_xml_user_perm(String paramString)
/*    */   {
/* 53 */     this.user_perms = new HashSet();
/*    */   }
/*    */ 
/*    */   public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*    */     throws SAXException
/*    */   {
/* 66 */     String str = String.valueOf(paramArrayOfChar, paramInt1, paramInt2).trim();
/* 67 */     if (this.g_active)
/* 68 */       this.user_perms.add(str);
/*    */   }
/*    */ 
/*    */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*    */     throws SAXException
/*    */   {
/* 76 */     if (paramString3.equals("g"))
/* 77 */       this.g_active = true;
/*    */   }
/*    */ 
/*    */   public void endElement(String paramString1, String paramString2, String paramString3)
/*    */     throws SAXException
/*    */   {
/* 85 */     if (paramString3.equals("g"))
/* 86 */       this.g_active = false;
/*    */   }
/*    */ 
/*    */   public Set<String> get_result()
/*    */   {
/* 94 */     return this.user_perms;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_user_perm
 * JD-Core Version:    0.6.0
 */