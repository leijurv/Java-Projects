/*     */ package gui_support;
/*     */ 
/*     */ import core_objects.stiki_utils;
/*     */ import executables.stiki_frontend_driver;
/*     */ import gui_menus.gui_menu_bar;
/*     */ import gui_menus.gui_menu_options;
/*     */ import gui_panels.gui_comment_panel;
/*     */ import gui_panels.gui_comment_panel.COMMENT_TAB;
/*     */ import gui_panels.gui_login_panel;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class gui_settings extends Properties
/*     */ {
/*  31 */   public static gui_settings props = new gui_settings();
/*     */   private static stiki_frontend_driver parent;
/*     */   private static final int SETTINGS_VERSION = 2;
/*     */ 
/*     */   public static void set_parent(stiki_frontend_driver paramstiki_frontend_driver)
/*     */   {
/*  94 */     parent = paramstiki_frontend_driver;
/*     */   }
/*     */ 
/*     */   public static void load_properties()
/*     */   {
/*     */     try
/*     */     {
/* 102 */       FileInputStream localFileInputStream = new FileInputStream(get_properties_file());
/* 103 */       props.loadFromXML(localFileInputStream);
/* 104 */       localFileInputStream.close();
/*     */     } catch (FileNotFoundException localFileNotFoundException) {
/*     */     } catch (Exception localException) {
/* 107 */       System.out.println("Error when loading persistent settings:");
/* 108 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void save_properties()
/*     */   {
/* 119 */     if (get_int_def(SETTINGS_INT.settings_version, 0) < 2)
/* 120 */       props.clear();
/* 121 */     update_properties();
/*     */     try {
/* 123 */       File localFile = get_properties_file();
/*     */ 
/* 126 */       localFile.setReadable(false, false);
/* 127 */       localFile.setWritable(false, false);
/* 128 */       localFile.setExecutable(false, false);
/* 129 */       localFile.setReadable(true, true);
/* 130 */       localFile.setWritable(true, true);
/* 131 */       localFile.setExecutable(false, true);
/*     */ 
/* 134 */       FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
/* 135 */       props.storeToXML(localFileOutputStream, "STiki Settings", "UTF-8");
/* 136 */       localFileOutputStream.close();
/*     */     } catch (Exception localException) {
/* 138 */       System.out.println("Error when saving persistent settings:");
/* 139 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int get_int_def(SETTINGS_INT paramSETTINGS_INT, int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 152 */       if (props.containsKey(paramSETTINGS_INT.toString()))
/* 153 */         return get_int(paramSETTINGS_INT);
/*     */     } catch (Exception localException) {
/* 155 */       System.out.println("Error in getting persistent setting (int):");
/* 156 */       localException.printStackTrace();
/* 157 */     }return paramInt;
/*     */   }
/*     */ 
/*     */   public static String get_str_def(SETTINGS_STR paramSETTINGS_STR, String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 167 */       if (props.containsKey(paramSETTINGS_STR.toString()))
/* 168 */         return props.getProperty(paramSETTINGS_STR.toString());
/*     */     } catch (Exception localException) {
/* 170 */       System.out.println("Error in getting persistent setting (String):");
/* 171 */       localException.printStackTrace();
/* 172 */     }return paramString;
/*     */   }
/*     */ 
/*     */   public static boolean get_bool_def(SETTINGS_BOOL paramSETTINGS_BOOL, boolean paramBoolean)
/*     */   {
/*     */     try
/*     */     {
/* 182 */       if (props.containsKey(paramSETTINGS_BOOL.toString()))
/* 183 */         return get_bool(paramSETTINGS_BOOL);
/*     */     } catch (Exception localException) {
/* 185 */       System.out.println("Error in getting persistent setting (bool):");
/* 186 */       localException.printStackTrace();
/* 187 */     }return paramBoolean;
/*     */   }
/*     */ 
/*     */   private static File get_properties_file()
/*     */     throws Exception
/*     */   {
/* 203 */     String str = System.getProperty("user.home");
/* 204 */     File localFile = null;
/* 205 */     if (str != null)
/* 206 */       localFile = new File(str);
/* 207 */     return new File(localFile, ".STiki.props.xml");
/*     */   }
/*     */ 
/*     */   private static void update_properties()
/*     */   {
/* 217 */     props.setProperty(SETTINGS_INT.win_width.toString(), String.valueOf(parent.getWidth()));
/*     */ 
/* 219 */     props.setProperty(SETTINGS_INT.win_height.toString(), String.valueOf(parent.getHeight()));
/*     */ 
/* 221 */     props.setProperty(SETTINGS_INT.win_locx.toString(), String.valueOf(parent.getX()));
/*     */ 
/* 223 */     props.setProperty(SETTINGS_INT.win_loxy.toString(), String.valueOf(parent.getY()));
/*     */ 
/* 227 */     props.setProperty(SETTINGS_STR.comment_vand.toString(), parent.comment_panel.get_comment(gui_comment_panel.COMMENT_TAB.VAND));
/*     */ 
/* 229 */     props.setProperty(SETTINGS_STR.comment_spam.toString(), parent.comment_panel.get_comment(gui_comment_panel.COMMENT_TAB.SPAM));
/*     */ 
/* 231 */     props.setProperty(SETTINGS_STR.comment_agf2.toString(), parent.comment_panel.get_comment(gui_comment_panel.COMMENT_TAB.AGF));
/*     */ 
/* 233 */     props.setProperty(SETTINGS_BOOL.warn_vand.toString(), String.valueOf(parent.comment_panel.get_warn_status(gui_comment_panel.COMMENT_TAB.VAND)));
/*     */ 
/* 235 */     props.setProperty(SETTINGS_BOOL.warn_spam.toString(), String.valueOf(parent.comment_panel.get_warn_status(gui_comment_panel.COMMENT_TAB.SPAM)));
/*     */ 
/* 237 */     props.setProperty(SETTINGS_BOOL.warn_agf.toString(), String.valueOf(parent.comment_panel.get_warn_status(gui_comment_panel.COMMENT_TAB.AGF)));
/*     */ 
/* 241 */     props.setProperty(SETTINGS_STR.login_user.toString(), parent.login_panel.get_login_user_field());
/*     */ 
/* 243 */     props.setProperty(SETTINGS_BOOL.login_watch.toString(), String.valueOf(parent.login_panel.watchlist_checkbox_selected()));
/*     */ 
/* 247 */     props.setProperty(SETTINGS_INT.options_fontsize.toString(), String.valueOf(parent.menu_bar.get_options_menu().get_browser_fontsize()));
/*     */ 
/* 250 */     props.setProperty(SETTINGS_BOOL.options_hyperlinks.toString(), String.valueOf(parent.menu_bar.get_options_menu().get_hyperlink_policy()));
/*     */ 
/* 253 */     props.setProperty(SETTINGS_BOOL.options_https.toString(), String.valueOf(parent.menu_bar.get_options_menu().get_https_policy()));
/*     */ 
/* 256 */     props.setProperty(SETTINGS_BOOL.options_dttr.toString(), String.valueOf(parent.menu_bar.get_options_menu().get_dttr_policy()));
/*     */ 
/* 261 */     props.setProperty(SETTINGS_INT.passes_used.toString(), String.valueOf(parent.get_passes_in_career()));
/*     */ 
/* 265 */     props.setProperty(SETTINGS_INT.settings_version.toString(), String.valueOf(2));
/*     */   }
/*     */ 
/*     */   private static int get_int(SETTINGS_INT paramSETTINGS_INT)
/*     */     throws Exception
/*     */   {
/* 278 */     return Integer.parseInt(props.getProperty(paramSETTINGS_INT.toString()));
/*     */   }
/*     */ 
/*     */   private static boolean get_bool(SETTINGS_BOOL paramSETTINGS_BOOL)
/*     */     throws Exception
/*     */   {
/* 287 */     return stiki_utils.str_to_bool(props.getProperty(paramSETTINGS_BOOL.toString()));
/*     */   }
/*     */ 
/*     */   public static enum SETTINGS_BOOL
/*     */   {
/*  58 */     warn_vand, 
/*  59 */     warn_spam, 
/*  60 */     warn_agf, 
/*  61 */     login_watch, 
/*  62 */     options_hyperlinks, 
/*  63 */     options_https, 
/*  64 */     options_dttr;
/*     */   }
/*     */ 
/*     */   public static enum SETTINGS_INT
/*     */   {
/*  46 */     win_width, 
/*  47 */     win_height, 
/*  48 */     win_locx, 
/*  49 */     win_loxy, 
/*  50 */     options_fontsize, 
/*  51 */     settings_version, 
/*  52 */     passes_used;
/*     */   }
/*     */ 
/*     */   public static enum SETTINGS_STR
/*     */   {
/*  37 */     comment_vand, 
/*  38 */     comment_spam, 
/*  39 */     comment_agf2, 
/*  40 */     login_user;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_settings
 * JD-Core Version:    0.6.0
 */