/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class LRUCache extends LinkedHashMap
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected int maxElements;
/*    */ 
/*    */   public LRUCache(int maxSize)
/*    */   {
/* 42 */     super(maxSize);
/* 43 */     this.maxElements = maxSize;
/*    */   }
/*    */ 
/*    */   protected boolean removeEldestEntry(Map.Entry eldest)
/*    */   {
/* 52 */     return size() > this.maxElements;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.LRUCache
 * JD-Core Version:    0.6.0
 */