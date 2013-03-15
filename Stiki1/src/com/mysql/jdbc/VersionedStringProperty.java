/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.SQLException;
/*      */ import java.util.List;
/*      */ 
/*      */ class VersionedStringProperty
/*      */ {
/*      */   int majorVersion;
/*      */   int minorVersion;
/*      */   int subminorVersion;
/* 1086 */   boolean preferredValue = false;
/*      */   String propertyInfo;
/*      */ 
/*      */   VersionedStringProperty(String property)
/*      */   {
/* 1091 */     property = property.trim();
/*      */ 
/* 1093 */     if (property.startsWith("*")) {
/* 1094 */       property = property.substring(1);
/* 1095 */       this.preferredValue = true;
/*      */     }
/*      */ 
/* 1098 */     if (property.startsWith(">")) {
/* 1099 */       property = property.substring(1);
/*      */ 
/* 1101 */       int charPos = 0;
/*      */ 
/* 1103 */       for (charPos = 0; charPos < property.length(); charPos++) {
/* 1104 */         char c = property.charAt(charPos);
/*      */ 
/* 1106 */         if ((!Character.isWhitespace(c)) && (!Character.isDigit(c)) && (c != '.'))
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */ 
/* 1112 */       String versionInfo = property.substring(0, charPos);
/* 1113 */       List versionParts = StringUtils.split(versionInfo, ".", true);
/*      */ 
/* 1115 */       this.majorVersion = Integer.parseInt(versionParts.get(0).toString());
/*      */ 
/* 1117 */       if (versionParts.size() > 1)
/* 1118 */         this.minorVersion = Integer.parseInt(versionParts.get(1).toString());
/*      */       else {
/* 1120 */         this.minorVersion = 0;
/*      */       }
/*      */ 
/* 1123 */       if (versionParts.size() > 2) {
/* 1124 */         this.subminorVersion = Integer.parseInt(versionParts.get(2).toString());
/*      */       }
/*      */       else {
/* 1127 */         this.subminorVersion = 0;
/*      */       }
/*      */ 
/* 1130 */       this.propertyInfo = property.substring(charPos);
/*      */     } else {
/* 1132 */       this.majorVersion = (this.minorVersion = this.subminorVersion = 0);
/* 1133 */       this.propertyInfo = property;
/*      */     }
/*      */   }
/*      */ 
/*      */   VersionedStringProperty(String property, int major, int minor, int subminor) {
/* 1138 */     this.propertyInfo = property;
/* 1139 */     this.majorVersion = major;
/* 1140 */     this.minorVersion = minor;
/* 1141 */     this.subminorVersion = subminor;
/*      */   }
/*      */ 
/*      */   boolean isOkayForVersion(Connection conn) throws SQLException {
/* 1145 */     return conn.versionMeetsMinimum(this.majorVersion, this.minorVersion, this.subminorVersion);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1151 */     return this.propertyInfo;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.VersionedStringProperty
 * JD-Core Version:    0.6.0
 */