/*    */ package gui_support;
/*    */ 
/*    */ import core_objects.metadata;
/*    */ import gui_edit_queue.gui_display_pkg;
/*    */ import java.io.InputStream;
/*    */ import java.util.List;
/*    */ import mediawiki_api.api_post;
/*    */ import mediawiki_api.api_post.EDIT_OUTCOME;
/*    */ import mediawiki_api.api_retrieve;
/*    */ 
/*    */ public class gui_soft_rollback
/*    */ {
/*    */   public static int software_rollback(gui_display_pkg paramgui_display_pkg, String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
/*    */     throws Exception
/*    */   {
/* 45 */     metadata localmetadata = (metadata)paramgui_display_pkg.page_hist.get(0);
/* 46 */     if ((paramgui_display_pkg.rb_depth == 1) || (paramgui_display_pkg.rb_depth == paramgui_display_pkg.page_hist.size()))
/*    */     {
/* 49 */       localInputStream = api_post.edit_revert(localmetadata.rid, localmetadata.title, paramString1, paramBoolean1, paramgui_display_pkg.get_token(), paramString2, paramBoolean2);
/*    */ 
/* 52 */       localEDIT_OUTCOME = api_post.edit_was_made(localInputStream);
/* 53 */       localInputStream.close();
/*    */ 
/* 55 */       if (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.ERROR)) return -1;
/* 56 */       if (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.BEATEN)) return 0;
/* 57 */       return 1;
/*    */     }
/*    */ 
/* 61 */     InputStream localInputStream = api_post.edit_full_text(localmetadata.title, paramString1, api_retrieve.process_page_content(((metadata)paramgui_display_pkg.page_hist.get(paramgui_display_pkg.rb_depth)).rid), paramBoolean1, paramgui_display_pkg.get_token(), paramString2, false, paramBoolean2);
/*    */ 
/* 65 */     api_post.EDIT_OUTCOME localEDIT_OUTCOME = api_post.edit_was_made(localInputStream);
/* 66 */     localInputStream.close();
/*    */ 
/* 68 */     if (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.ERROR)) return -1;
/* 69 */     if (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.BEATEN)) return 0;
/*    */ 
/* 71 */     return paramgui_display_pkg.rb_depth;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_soft_rollback
 * JD-Core Version:    0.6.0
 */