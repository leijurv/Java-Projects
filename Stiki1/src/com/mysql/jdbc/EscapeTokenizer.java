/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ public class EscapeTokenizer
/*     */ {
/*  39 */   private int bracesLevel = 0;
/*     */ 
/*  41 */   private boolean emittingEscapeCode = false;
/*     */ 
/*  43 */   private boolean inComment = false;
/*     */ 
/*  45 */   private boolean inQuotes = false;
/*     */ 
/*  47 */   private char lastChar = '\000';
/*     */ 
/*  49 */   private char lastLastChar = '\000';
/*     */ 
/*  51 */   private int pos = 0;
/*     */ 
/*  53 */   private char quoteChar = '\000';
/*     */ 
/*  55 */   private boolean sawVariableUse = false;
/*     */ 
/*  57 */   private String source = null;
/*     */ 
/*  59 */   private int sourceLength = 0;
/*     */ 
/*     */   public EscapeTokenizer(String s)
/*     */   {
/*  71 */     this.source = s;
/*  72 */     this.sourceLength = s.length();
/*  73 */     this.pos = 0;
/*     */   }
/*     */ 
/*     */   public synchronized boolean hasMoreTokens()
/*     */   {
/*  85 */     return this.pos < this.sourceLength;
/*     */   }
/*     */ 
/*     */   public synchronized String nextToken()
/*     */   {
/*  94 */     StringBuffer tokenBuf = new StringBuffer();
/*     */ 
/*  96 */     if (this.emittingEscapeCode) {
/*  97 */       tokenBuf.append("{");
/*  98 */       this.emittingEscapeCode = false;
/*     */     }
/*     */ 
/* 101 */     for (; this.pos < this.sourceLength; this.pos += 1) {
/* 102 */       char c = this.source.charAt(this.pos);
/*     */ 
/* 106 */       if ((!this.inQuotes) && (c == '@')) {
/* 107 */         this.sawVariableUse = true;
/*     */       }
/*     */ 
/* 110 */       if (((c == '\'') || (c == '"')) && (!this.inComment)) {
/* 111 */         if ((this.inQuotes) && (c == this.quoteChar) && 
/* 112 */           (this.pos + 1 < this.sourceLength) && 
/* 113 */           (this.source.charAt(this.pos + 1) == this.quoteChar))
/*     */         {
/* 115 */           if (this.lastChar != '\\') {
/* 116 */             tokenBuf.append(this.quoteChar);
/* 117 */             tokenBuf.append(this.quoteChar);
/* 118 */             this.pos += 1;
/* 119 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 124 */         if (this.lastChar != '\\') {
/* 125 */           if (this.inQuotes) {
/* 126 */             if (this.quoteChar == c)
/* 127 */               this.inQuotes = false;
/*     */           }
/*     */           else {
/* 130 */             this.inQuotes = true;
/* 131 */             this.quoteChar = c;
/*     */           }
/* 133 */         } else if (this.lastLastChar == '\\') {
/* 134 */           if (this.inQuotes) {
/* 135 */             if (this.quoteChar == c)
/* 136 */               this.inQuotes = false;
/*     */           }
/*     */           else {
/* 139 */             this.inQuotes = true;
/* 140 */             this.quoteChar = c;
/*     */           }
/*     */         }
/*     */ 
/* 144 */         tokenBuf.append(c);
/* 145 */       } else if (c == '-') {
/* 146 */         if ((this.lastChar == '-') && (this.lastLastChar != '\\') && (!this.inQuotes))
/*     */         {
/* 148 */           this.inComment = true;
/*     */         }
/*     */ 
/* 151 */         tokenBuf.append(c);
/* 152 */       } else if ((c == '\n') || (c == '\r')) {
/* 153 */         this.inComment = false;
/*     */ 
/* 155 */         tokenBuf.append(c);
/* 156 */       } else if (c == '{') {
/* 157 */         if ((this.inQuotes) || (this.inComment)) {
/* 158 */           tokenBuf.append(c);
/*     */         } else {
/* 160 */           this.bracesLevel += 1;
/*     */ 
/* 162 */           if (this.bracesLevel == 1) {
/* 163 */             this.pos += 1;
/* 164 */             this.emittingEscapeCode = true;
/*     */ 
/* 166 */             return tokenBuf.toString();
/*     */           }
/*     */ 
/* 169 */           tokenBuf.append(c);
/*     */         }
/* 171 */       } else if (c == '}') {
/* 172 */         tokenBuf.append(c);
/*     */ 
/* 174 */         if ((!this.inQuotes) && (!this.inComment)) {
/* 175 */           this.lastChar = c;
/*     */ 
/* 177 */           this.bracesLevel -= 1;
/*     */ 
/* 179 */           if (this.bracesLevel == 0) {
/* 180 */             this.pos += 1;
/*     */ 
/* 182 */             return tokenBuf.toString();
/*     */           }
/*     */         }
/*     */       } else {
/* 186 */         tokenBuf.append(c);
/*     */       }
/*     */ 
/* 189 */       this.lastLastChar = this.lastChar;
/* 190 */       this.lastChar = c;
/*     */     }
/*     */ 
/* 193 */     return tokenBuf.toString();
/*     */   }
/*     */ 
/*     */   boolean sawVariableUse() {
/* 197 */     return this.sawVariableUse;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.EscapeTokenizer
 * JD-Core Version:    0.6.0
 */