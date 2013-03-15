/*    */ package gui_support;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.awt.Toolkit;
/*    */ import java.net.URL;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class gui_filesys_images
/*    */ {
/*    */   public static final String IMG_BASE = "icons/";
/* 27 */   public static final Image ICON_16 = get_img("icons/icon_16.png");
/* 28 */   public static final Image ICON_20 = get_img("icons/icon_20.png");
/* 29 */   public static final Image ICON_32 = get_img("icons/icon_32.png");
/* 30 */   public static final Image ICON_64 = get_img("icons/icon_64.png");
/* 31 */   public static final Image ICON_128 = get_img("icons/icon_128.png");
/* 32 */   public static final Image ICON_200 = get_img("icons/icon_200.png");
/*    */ 
/*    */   public static List<Image> get_icon_set()
/*    */     throws Exception
/*    */   {
/* 42 */     ArrayList localArrayList = new ArrayList();
/* 43 */     localArrayList.add(ICON_16);
/* 44 */     localArrayList.add(ICON_20);
/* 45 */     localArrayList.add(ICON_32);
/* 46 */     localArrayList.add(ICON_64);
/* 47 */     localArrayList.add(ICON_128);
/* 48 */     localArrayList.add(ICON_200);
/* 49 */     return localArrayList;
/*    */   }
/*    */ 
/*    */   private static Image get_img(String paramString)
/*    */   {
/* 61 */     URL localURL = gui_filesys_images.class.getResource(paramString);
/* 62 */     Image localImage = Toolkit.getDefaultToolkit().getImage(localURL);
/* 63 */     return localImage;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_filesys_images
 * JD-Core Version:    0.6.0
 */