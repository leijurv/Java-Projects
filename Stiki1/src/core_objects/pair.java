/*    */ package core_objects;
/*    */ 
/*    */ public class pair<F, S>
/*    */ {
/*    */   public F fst;
/*    */   public S snd;
/*    */ 
/*    */   public pair(F paramF, S paramS)
/*    */   {
/* 32 */     this.fst = paramF;
/* 33 */     this.snd = paramS;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 41 */     pair localpair = (pair)paramObject;
/*    */ 
/* 43 */     return (this.fst.equals(localpair.fst)) && (this.snd == localpair.snd);
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 51 */     return this.fst.hashCode() * this.snd.hashCode();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     core_objects.pair
 * JD-Core Version:    0.6.0
 */