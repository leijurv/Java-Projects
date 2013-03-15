/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RowDataStatic
/*     */   implements RowData
/*     */ {
/*     */   private Field[] metadata;
/*     */   private int index;
/*     */   ResultSetImpl owner;
/*     */   private List rows;
/*     */ 
/*     */   public RowDataStatic(List rows)
/*     */   {
/*  55 */     this.index = -1;
/*  56 */     this.rows = rows;
/*     */   }
/*     */ 
/*     */   public void addRow(ResultSetRow row)
/*     */   {
/*  66 */     this.rows.add(row);
/*     */   }
/*     */ 
/*     */   public void afterLast()
/*     */   {
/*  73 */     this.index = this.rows.size();
/*     */   }
/*     */ 
/*     */   public void beforeFirst()
/*     */   {
/*  80 */     this.index = -1;
/*     */   }
/*     */ 
/*     */   public void beforeLast()
/*     */   {
/*  87 */     this.index = (this.rows.size() - 2);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ResultSetRow getAt(int atIndex)
/*     */     throws SQLException
/*     */   {
/* 105 */     if ((atIndex < 0) || (atIndex >= this.rows.size())) {
/* 106 */       return null;
/*     */     }
/*     */ 
/* 109 */     return ((ResultSetRow)this.rows.get(atIndex)).setMetadata(this.metadata);
/*     */   }
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */   {
/* 118 */     return this.index;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 125 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */   {
/* 134 */     boolean hasMore = this.index + 1 < this.rows.size();
/*     */ 
/* 136 */     return hasMore;
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */   {
/* 145 */     return this.index >= this.rows.size();
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */   {
/* 154 */     return (this.index == -1) && (this.rows.size() != 0);
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 163 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 172 */     return this.rows.size() == 0;
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */   {
/* 181 */     return this.index == 0;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */   {
/* 194 */     if (this.rows.size() == 0) {
/* 195 */       return false;
/*     */     }
/*     */ 
/* 198 */     return this.index == this.rows.size() - 1;
/*     */   }
/*     */ 
/*     */   public void moveRowRelative(int rowsToMove)
/*     */   {
/* 208 */     this.index += rowsToMove;
/*     */   }
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 217 */     this.index += 1;
/*     */ 
/* 219 */     if (this.index < this.rows.size()) {
/* 220 */       ResultSetRow row = (ResultSetRow)this.rows.get(this.index);
/*     */ 
/* 222 */       return row.setMetadata(this.metadata);
/*     */     }
/*     */ 
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   public void removeRow(int atIndex)
/*     */   {
/* 235 */     this.rows.remove(atIndex);
/*     */   }
/*     */ 
/*     */   public void setCurrentRow(int newIndex)
/*     */   {
/* 245 */     this.index = newIndex;
/*     */   }
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 252 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 261 */     return this.rows.size();
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty() {
/* 265 */     return (this.rows != null) && (this.rows.size() == 0);
/*     */   }
/*     */ 
/*     */   public void setMetadata(Field[] metadata) {
/* 269 */     this.metadata = metadata;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.RowDataStatic
 * JD-Core Version:    0.6.0
 */