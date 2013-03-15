/*     */ package mediawiki_api;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class api_xml_xlink extends DefaultHandler
/*     */ {
/*  21 */   private Set<String> result_set = new TreeSet();
/*     */ 
/*  26 */   private StringBuffer cur_link = new StringBuffer();
/*     */ 
/*  32 */   private boolean el_active = false;
/*     */   private long pid;
/*     */ 
/*     */   public api_xml_xlink(long paramLong)
/*     */   {
/*  48 */     this.pid = paramLong;
/*     */   }
/*     */ 
/*     */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*     */     throws SAXException
/*     */   {
/*  60 */     if (paramString3.equals("el"))
/*  61 */       this.el_active = true;
/*  62 */     else if (paramString3.equals("extlinks")) try {
/*  63 */         if (paramAttributes.getValue("eloffset") != null) {
/*  64 */           this.result_set.addAll(api_retrieve.process_xlinks(this.pid, Integer.parseInt(paramAttributes.getValue("eloffset"))));
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*  69 */         System.err.println("Error in recursive xlink search:");
/*  70 */         localException.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*     */     throws SAXException
/*     */   {
/*  82 */     if (this.el_active)
/*  83 */       this.cur_link.append(String.valueOf(paramArrayOfChar, paramInt1, paramInt2));
/*     */   }
/*     */ 
/*     */   public void endElement(String paramString1, String paramString2, String paramString3)
/*     */     throws SAXException
/*     */   {
/*  92 */     if (paramString3.equals("el")) {
/*  93 */       this.el_active = false;
/*  94 */       this.result_set.add(this.cur_link.toString().replace("&amp;", "&"));
/*  95 */       this.cur_link = new StringBuffer();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<String> get_result()
/*     */   {
/* 105 */     return this.result_set;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_xml_xlink
 * JD-Core Version:    0.6.0
 */