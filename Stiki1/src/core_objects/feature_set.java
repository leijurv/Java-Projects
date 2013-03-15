/*     */ package core_objects;
/*     */ 
/*     */ public class feature_set
/*     */ {
/*     */   public final boolean LABEL;
/*     */   public final long R_ID;
/*     */   public final boolean IS_IP;
/*     */   public final double REP_USER;
/*     */   public final double REP_ARTICLE;
/*     */   public final float TOD;
/*     */   public final int DOW;
/*     */   public final long TS_R;
/*     */   public final long TS_LP;
/*     */   public final long TS_RBU;
/*     */   public final int COMM_LENGTH;
/*     */   public final int BYTE_CHANGE;
/*     */   public final double REP_COUNTRY;
/*     */   public final int NLP_DIRTY;
/*     */   public final int NLP_CHAR_REP;
/*     */   public final double NLP_UCASE;
/*     */   public final double NLP_ALPHA;
/*     */ 
/*     */   public feature_set(boolean paramBoolean1, long paramLong1, boolean paramBoolean2, double paramDouble1, double paramDouble2, float paramFloat, int paramInt1, long paramLong2, long paramLong3, long paramLong4, int paramInt2, int paramInt3, double paramDouble3, int paramInt4, int paramInt5, double paramDouble4, double paramDouble5)
/*     */   {
/* 125 */     this.LABEL = paramBoolean1;
/* 126 */     this.R_ID = paramLong1;
/* 127 */     this.IS_IP = paramBoolean2;
/* 128 */     this.REP_USER = paramDouble1;
/* 129 */     this.REP_ARTICLE = paramDouble2;
/* 130 */     this.TOD = paramFloat;
/* 131 */     this.DOW = paramInt1;
/* 132 */     this.TS_R = paramLong2;
/* 133 */     this.TS_LP = paramLong3;
/* 134 */     this.TS_RBU = paramLong4;
/* 135 */     this.COMM_LENGTH = paramInt2;
/* 136 */     this.BYTE_CHANGE = paramInt3;
/* 137 */     this.REP_COUNTRY = paramDouble3;
/* 138 */     this.NLP_DIRTY = paramInt4;
/* 139 */     this.NLP_CHAR_REP = paramInt5;
/* 140 */     this.NLP_UCASE = paramDouble4;
/* 141 */     this.NLP_ALPHA = paramDouble5;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     core_objects.feature_set
 * JD-Core Version:    0.6.0
 */