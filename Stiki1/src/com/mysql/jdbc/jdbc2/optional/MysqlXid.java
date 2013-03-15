/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class MysqlXid
/*     */   implements Xid
/*     */ {
/*  38 */   int hash = 0;
/*     */   byte[] myBqual;
/*     */   int myFormatId;
/*     */   byte[] myGtrid;
/*     */ 
/*     */   public MysqlXid(byte[] gtrid, byte[] bqual, int formatId)
/*     */   {
/*  47 */     this.myGtrid = gtrid;
/*  48 */     this.myBqual = bqual;
/*  49 */     this.myFormatId = formatId;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object another)
/*     */   {
/*  54 */     if ((another instanceof Xid)) {
/*  55 */       Xid anotherAsXid = (Xid)another;
/*     */ 
/*  57 */       if (this.myFormatId != anotherAsXid.getFormatId()) {
/*  58 */         return false;
/*     */       }
/*     */ 
/*  61 */       byte[] otherBqual = anotherAsXid.getBranchQualifier();
/*  62 */       byte[] otherGtrid = anotherAsXid.getGlobalTransactionId();
/*     */ 
/*  64 */       if ((otherGtrid != null) && (otherGtrid.length == this.myGtrid.length)) {
/*  65 */         int length = otherGtrid.length;
/*     */ 
/*  67 */         for (int i = 0; i < length; i++) {
/*  68 */           if (otherGtrid[i] != this.myGtrid[i]) {
/*  69 */             return false;
/*     */           }
/*     */         }
/*     */ 
/*  73 */         if ((otherBqual != null) && (otherBqual.length == this.myBqual.length)) {
/*  74 */           length = otherBqual.length;
/*     */ 
/*  76 */           for (int i = 0; i < length; i++)
/*  77 */             if (otherBqual[i] != this.myBqual[i])
/*  78 */               return false;
/*     */         }
/*     */         else
/*     */         {
/*  82 */           return false;
/*     */         }
/*     */ 
/*  85 */         return true;
/*     */       }
/*  87 */       return false;
/*     */     }
/*     */ 
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   public byte[] getBranchQualifier()
/*     */   {
/*  95 */     return this.myBqual;
/*     */   }
/*     */ 
/*     */   public int getFormatId() {
/*  99 */     return this.myFormatId;
/*     */   }
/*     */ 
/*     */   public byte[] getGlobalTransactionId() {
/* 103 */     return this.myGtrid;
/*     */   }
/*     */ 
/*     */   public synchronized int hashCode() {
/* 107 */     if (this.hash == 0) {
/* 108 */       for (int i = 0; i < this.myGtrid.length; i++) {
/* 109 */         this.hash = (33 * this.hash + this.myGtrid[i]);
/*     */       }
/*     */     }
/*     */ 
/* 113 */     return this.hash;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlXid
 * JD-Core Version:    0.6.0
 */