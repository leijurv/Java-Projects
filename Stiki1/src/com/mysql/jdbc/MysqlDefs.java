/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public final class MysqlDefs
/*     */ {
/*     */   static final int COM_BINLOG_DUMP = 18;
/*     */   static final int COM_CHANGE_USER = 17;
/*     */   static final int COM_CLOSE_STATEMENT = 25;
/*     */   static final int COM_CONNECT_OUT = 20;
/*     */   static final int COM_END = 29;
/*     */   static final int COM_EXECUTE = 23;
/*     */   static final int COM_FETCH = 28;
/*     */   static final int COM_LONG_DATA = 24;
/*     */   static final int COM_PREPARE = 22;
/*     */   static final int COM_REGISTER_SLAVE = 21;
/*     */   static final int COM_RESET_STMT = 26;
/*     */   static final int COM_SET_OPTION = 27;
/*     */   static final int COM_TABLE_DUMP = 19;
/*     */   static final int CONNECT = 11;
/*     */   static final int CREATE_DB = 5;
/*     */   static final int DEBUG = 13;
/*     */   static final int DELAYED_INSERT = 16;
/*     */   static final int DROP_DB = 6;
/*     */   static final int FIELD_LIST = 4;
/*     */   static final int FIELD_TYPE_BIT = 16;
/*     */   public static final int FIELD_TYPE_BLOB = 252;
/*     */   static final int FIELD_TYPE_DATE = 10;
/*     */   static final int FIELD_TYPE_DATETIME = 12;
/*     */   static final int FIELD_TYPE_DECIMAL = 0;
/*     */   static final int FIELD_TYPE_DOUBLE = 5;
/*     */   static final int FIELD_TYPE_ENUM = 247;
/*     */   static final int FIELD_TYPE_FLOAT = 4;
/*     */   static final int FIELD_TYPE_GEOMETRY = 255;
/*     */   static final int FIELD_TYPE_INT24 = 9;
/*     */   static final int FIELD_TYPE_LONG = 3;
/*     */   static final int FIELD_TYPE_LONG_BLOB = 251;
/*     */   static final int FIELD_TYPE_LONGLONG = 8;
/*     */   static final int FIELD_TYPE_MEDIUM_BLOB = 250;
/*     */   static final int FIELD_TYPE_NEW_DECIMAL = 246;
/*     */   static final int FIELD_TYPE_NEWDATE = 14;
/*     */   static final int FIELD_TYPE_NULL = 6;
/*     */   static final int FIELD_TYPE_SET = 248;
/*     */   static final int FIELD_TYPE_SHORT = 2;
/*     */   static final int FIELD_TYPE_STRING = 254;
/*     */   static final int FIELD_TYPE_TIME = 11;
/*     */   static final int FIELD_TYPE_TIMESTAMP = 7;
/*     */   static final int FIELD_TYPE_TINY = 1;
/*     */   static final int FIELD_TYPE_TINY_BLOB = 249;
/*     */   static final int FIELD_TYPE_VAR_STRING = 253;
/*     */   static final int FIELD_TYPE_VARCHAR = 15;
/*     */   static final int FIELD_TYPE_YEAR = 13;
/*     */   static final int INIT_DB = 2;
/*     */   static final long LENGTH_BLOB = 65535L;
/*     */   static final long LENGTH_LONGBLOB = 4294967295L;
/*     */   static final long LENGTH_MEDIUMBLOB = 16777215L;
/*     */   static final long LENGTH_TINYBLOB = 255L;
/*     */   static final int MAX_ROWS = 50000000;
/*     */   public static final int NO_CHARSET_INFO = -1;
/*     */   static final byte OPEN_CURSOR_FLAG = 1;
/*     */   static final int PING = 14;
/*     */   static final int PROCESS_INFO = 10;
/*     */   static final int PROCESS_KILL = 12;
/*     */   static final int QUERY = 3;
/*     */   static final int QUIT = 1;
/*     */   static final int RELOAD = 7;
/*     */   static final int SHUTDOWN = 8;
/*     */   static final int SLEEP = 0;
/*     */   static final int STATISTICS = 9;
/*     */   static final int TIME = 15;
/* 490 */   private static Map mysqlToJdbcTypesMap = new HashMap();
/*     */ 
/*     */   static int mysqlToJavaType(int mysqlType)
/*     */   {
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/* 196 */     switch (mysqlType) {
/*     */     case 0:
/*     */     case 246:
/* 199 */       jdbcType = 3;
/*     */ 
/* 201 */       break;
/*     */     case 1:
/* 204 */       jdbcType = -6;
/*     */ 
/* 206 */       break;
/*     */     case 2:
/* 209 */       jdbcType = 5;
/*     */ 
/* 211 */       break;
/*     */     case 3:
/* 214 */       jdbcType = 4;
/*     */ 
/* 216 */       break;
/*     */     case 4:
/* 219 */       jdbcType = 7;
/*     */ 
/* 221 */       break;
/*     */     case 5:
/* 224 */       jdbcType = 8;
/*     */ 
/* 226 */       break;
/*     */     case 6:
/* 229 */       jdbcType = 0;
/*     */ 
/* 231 */       break;
/*     */     case 7:
/* 234 */       jdbcType = 93;
/*     */ 
/* 236 */       break;
/*     */     case 8:
/* 239 */       jdbcType = -5;
/*     */ 
/* 241 */       break;
/*     */     case 9:
/* 244 */       jdbcType = 4;
/*     */ 
/* 246 */       break;
/*     */     case 10:
/* 249 */       jdbcType = 91;
/*     */ 
/* 251 */       break;
/*     */     case 11:
/* 254 */       jdbcType = 92;
/*     */ 
/* 256 */       break;
/*     */     case 12:
/* 259 */       jdbcType = 93;
/*     */ 
/* 261 */       break;
/*     */     case 13:
/* 264 */       jdbcType = 91;
/*     */ 
/* 266 */       break;
/*     */     case 14:
/* 269 */       jdbcType = 91;
/*     */ 
/* 271 */       break;
/*     */     case 247:
/* 274 */       jdbcType = 1;
/*     */ 
/* 276 */       break;
/*     */     case 248:
/* 279 */       jdbcType = 1;
/*     */ 
/* 281 */       break;
/*     */     case 249:
/* 284 */       jdbcType = -3;
/*     */ 
/* 286 */       break;
/*     */     case 250:
/* 289 */       jdbcType = -4;
/*     */ 
/* 291 */       break;
/*     */     case 251:
/* 294 */       jdbcType = -4;
/*     */ 
/* 296 */       break;
/*     */     case 252:
/* 299 */       jdbcType = -4;
/*     */ 
/* 301 */       break;
/*     */     case 15:
/*     */     case 253:
/* 305 */       jdbcType = 12;
/*     */ 
/* 307 */       break;
/*     */     case 254:
/* 310 */       jdbcType = 1;
/*     */ 
/* 312 */       break;
/*     */     case 255:
/* 314 */       jdbcType = -2;
/*     */ 
/* 316 */       break;
/*     */     case 16:
/* 318 */       jdbcType = -7;
/*     */ 
/* 320 */       break;
/*     */     default:
/* 322 */       jdbcType = 12;
/*     */     }
/*     */ 
/* 325 */     return jdbcType;
/*     */   }
/*     */ 
/*     */   static int mysqlToJavaType(String mysqlType)
/*     */   {
/* 332 */     if (mysqlType.equalsIgnoreCase("BIT"))
/* 333 */       return mysqlToJavaType(16);
/* 334 */     if (mysqlType.equalsIgnoreCase("TINYINT"))
/* 335 */       return mysqlToJavaType(1);
/* 336 */     if (mysqlType.equalsIgnoreCase("SMALLINT"))
/* 337 */       return mysqlToJavaType(2);
/* 338 */     if (mysqlType.equalsIgnoreCase("MEDIUMINT"))
/* 339 */       return mysqlToJavaType(9);
/* 340 */     if ((mysqlType.equalsIgnoreCase("INT")) || (mysqlType.equalsIgnoreCase("INTEGER")))
/* 341 */       return mysqlToJavaType(3);
/* 342 */     if (mysqlType.equalsIgnoreCase("BIGINT"))
/* 343 */       return mysqlToJavaType(8);
/* 344 */     if (mysqlType.equalsIgnoreCase("INT24"))
/* 345 */       return mysqlToJavaType(9);
/* 346 */     if (mysqlType.equalsIgnoreCase("REAL"))
/* 347 */       return mysqlToJavaType(5);
/* 348 */     if (mysqlType.equalsIgnoreCase("FLOAT"))
/* 349 */       return mysqlToJavaType(4);
/* 350 */     if (mysqlType.equalsIgnoreCase("DECIMAL"))
/* 351 */       return mysqlToJavaType(0);
/* 352 */     if (mysqlType.equalsIgnoreCase("NUMERIC"))
/* 353 */       return mysqlToJavaType(0);
/* 354 */     if (mysqlType.equalsIgnoreCase("DOUBLE"))
/* 355 */       return mysqlToJavaType(5);
/* 356 */     if (mysqlType.equalsIgnoreCase("CHAR"))
/* 357 */       return mysqlToJavaType(254);
/* 358 */     if (mysqlType.equalsIgnoreCase("VARCHAR"))
/* 359 */       return mysqlToJavaType(253);
/* 360 */     if (mysqlType.equalsIgnoreCase("DATE"))
/* 361 */       return mysqlToJavaType(10);
/* 362 */     if (mysqlType.equalsIgnoreCase("TIME"))
/* 363 */       return mysqlToJavaType(11);
/* 364 */     if (mysqlType.equalsIgnoreCase("YEAR"))
/* 365 */       return mysqlToJavaType(13);
/* 366 */     if (mysqlType.equalsIgnoreCase("TIMESTAMP"))
/* 367 */       return mysqlToJavaType(7);
/* 368 */     if (mysqlType.equalsIgnoreCase("DATETIME"))
/* 369 */       return mysqlToJavaType(12);
/* 370 */     if (mysqlType.equalsIgnoreCase("TINYBLOB"))
/* 371 */       return -2;
/* 372 */     if (mysqlType.equalsIgnoreCase("BLOB"))
/* 373 */       return -4;
/* 374 */     if (mysqlType.equalsIgnoreCase("MEDIUMBLOB"))
/* 375 */       return -4;
/* 376 */     if (mysqlType.equalsIgnoreCase("LONGBLOB"))
/* 377 */       return -4;
/* 378 */     if (mysqlType.equalsIgnoreCase("TINYTEXT"))
/* 379 */       return 12;
/* 380 */     if (mysqlType.equalsIgnoreCase("TEXT"))
/* 381 */       return -1;
/* 382 */     if (mysqlType.equalsIgnoreCase("MEDIUMTEXT"))
/* 383 */       return -1;
/* 384 */     if (mysqlType.equalsIgnoreCase("LONGTEXT"))
/* 385 */       return -1;
/* 386 */     if (mysqlType.equalsIgnoreCase("ENUM"))
/* 387 */       return mysqlToJavaType(247);
/* 388 */     if (mysqlType.equalsIgnoreCase("SET"))
/* 389 */       return mysqlToJavaType(248);
/* 390 */     if (mysqlType.equalsIgnoreCase("GEOMETRY"))
/* 391 */       return mysqlToJavaType(255);
/* 392 */     if (mysqlType.equalsIgnoreCase("BINARY"))
/* 393 */       return -2;
/* 394 */     if (mysqlType.equalsIgnoreCase("VARBINARY"))
/* 395 */       return -3;
/* 396 */     if (mysqlType.equalsIgnoreCase("BIT")) {
/* 397 */       return mysqlToJavaType(16);
/*     */     }
/*     */ 
/* 401 */     return 1111;
/*     */   }
/*     */ 
/*     */   public static String typeToName(int mysqlType)
/*     */   {
/* 409 */     switch (mysqlType) {
/*     */     case 0:
/* 411 */       return "FIELD_TYPE_DECIMAL";
/*     */     case 1:
/* 414 */       return "FIELD_TYPE_TINY";
/*     */     case 2:
/* 417 */       return "FIELD_TYPE_SHORT";
/*     */     case 3:
/* 420 */       return "FIELD_TYPE_LONG";
/*     */     case 4:
/* 423 */       return "FIELD_TYPE_FLOAT";
/*     */     case 5:
/* 426 */       return "FIELD_TYPE_DOUBLE";
/*     */     case 6:
/* 429 */       return "FIELD_TYPE_NULL";
/*     */     case 7:
/* 432 */       return "FIELD_TYPE_TIMESTAMP";
/*     */     case 8:
/* 435 */       return "FIELD_TYPE_LONGLONG";
/*     */     case 9:
/* 438 */       return "FIELD_TYPE_INT24";
/*     */     case 10:
/* 441 */       return "FIELD_TYPE_DATE";
/*     */     case 11:
/* 444 */       return "FIELD_TYPE_TIME";
/*     */     case 12:
/* 447 */       return "FIELD_TYPE_DATETIME";
/*     */     case 13:
/* 450 */       return "FIELD_TYPE_YEAR";
/*     */     case 14:
/* 453 */       return "FIELD_TYPE_NEWDATE";
/*     */     case 247:
/* 456 */       return "FIELD_TYPE_ENUM";
/*     */     case 248:
/* 459 */       return "FIELD_TYPE_SET";
/*     */     case 249:
/* 462 */       return "FIELD_TYPE_TINY_BLOB";
/*     */     case 250:
/* 465 */       return "FIELD_TYPE_MEDIUM_BLOB";
/*     */     case 251:
/* 468 */       return "FIELD_TYPE_LONG_BLOB";
/*     */     case 252:
/* 471 */       return "FIELD_TYPE_BLOB";
/*     */     case 253:
/* 474 */       return "FIELD_TYPE_VAR_STRING";
/*     */     case 254:
/* 477 */       return "FIELD_TYPE_STRING";
/*     */     case 15:
/* 480 */       return "FIELD_TYPE_VARCHAR";
/*     */     case 255:
/* 483 */       return "FIELD_TYPE_GEOMETRY";
/*     */     }
/*     */ 
/* 486 */     return " Unknown MySQL Type # " + mysqlType;
/*     */   }
/*     */ 
/*     */   static final void appendJdbcTypeMappingQuery(StringBuffer buf, String mysqlTypeColumnName)
/*     */   {
/* 559 */     buf.append("CASE ");
/* 560 */     Map typesMap = new HashMap();
/* 561 */     typesMap.putAll(mysqlToJdbcTypesMap);
/* 562 */     typesMap.put("BINARY", Constants.integerValueOf(-2));
/* 563 */     typesMap.put("VARBINARY", Constants.integerValueOf(-3));
/*     */ 
/* 565 */     Iterator mysqlTypes = typesMap.keySet().iterator();
/*     */ 
/* 567 */     while (mysqlTypes.hasNext()) {
/* 568 */       String mysqlTypeName = (String)mysqlTypes.next();
/* 569 */       buf.append(" WHEN ");
/* 570 */       buf.append(mysqlTypeColumnName);
/* 571 */       buf.append("='");
/* 572 */       buf.append(mysqlTypeName);
/* 573 */       buf.append("' THEN ");
/* 574 */       buf.append(typesMap.get(mysqlTypeName));
/*     */ 
/* 576 */       if ((mysqlTypeName.equalsIgnoreCase("DOUBLE")) || (mysqlTypeName.equalsIgnoreCase("FLOAT")) || (mysqlTypeName.equalsIgnoreCase("DECIMAL")) || (mysqlTypeName.equalsIgnoreCase("NUMERIC")))
/*     */       {
/* 580 */         buf.append(" WHEN ");
/* 581 */         buf.append(mysqlTypeColumnName);
/* 582 */         buf.append("='");
/* 583 */         buf.append(mysqlTypeName);
/* 584 */         buf.append(" unsigned' THEN ");
/* 585 */         buf.append(typesMap.get(mysqlTypeName));
/*     */       }
/*     */     }
/*     */ 
/* 589 */     buf.append(" ELSE ");
/* 590 */     buf.append(1111);
/* 591 */     buf.append(" END ");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 493 */     mysqlToJdbcTypesMap.put("BIT", Constants.integerValueOf(mysqlToJavaType(16)));
/*     */ 
/* 496 */     mysqlToJdbcTypesMap.put("TINYINT", Constants.integerValueOf(mysqlToJavaType(1)));
/*     */ 
/* 498 */     mysqlToJdbcTypesMap.put("SMALLINT", Constants.integerValueOf(mysqlToJavaType(2)));
/*     */ 
/* 500 */     mysqlToJdbcTypesMap.put("MEDIUMINT", Constants.integerValueOf(mysqlToJavaType(9)));
/*     */ 
/* 502 */     mysqlToJdbcTypesMap.put("INT", Constants.integerValueOf(mysqlToJavaType(3)));
/*     */ 
/* 504 */     mysqlToJdbcTypesMap.put("INTEGER", Constants.integerValueOf(mysqlToJavaType(3)));
/*     */ 
/* 506 */     mysqlToJdbcTypesMap.put("BIGINT", Constants.integerValueOf(mysqlToJavaType(8)));
/*     */ 
/* 508 */     mysqlToJdbcTypesMap.put("INT24", Constants.integerValueOf(mysqlToJavaType(9)));
/*     */ 
/* 510 */     mysqlToJdbcTypesMap.put("REAL", Constants.integerValueOf(mysqlToJavaType(5)));
/*     */ 
/* 512 */     mysqlToJdbcTypesMap.put("FLOAT", Constants.integerValueOf(mysqlToJavaType(4)));
/*     */ 
/* 514 */     mysqlToJdbcTypesMap.put("DECIMAL", Constants.integerValueOf(mysqlToJavaType(0)));
/*     */ 
/* 516 */     mysqlToJdbcTypesMap.put("NUMERIC", Constants.integerValueOf(mysqlToJavaType(0)));
/*     */ 
/* 518 */     mysqlToJdbcTypesMap.put("DOUBLE", Constants.integerValueOf(mysqlToJavaType(5)));
/*     */ 
/* 520 */     mysqlToJdbcTypesMap.put("CHAR", Constants.integerValueOf(mysqlToJavaType(254)));
/*     */ 
/* 522 */     mysqlToJdbcTypesMap.put("VARCHAR", Constants.integerValueOf(mysqlToJavaType(253)));
/*     */ 
/* 524 */     mysqlToJdbcTypesMap.put("DATE", Constants.integerValueOf(mysqlToJavaType(10)));
/*     */ 
/* 526 */     mysqlToJdbcTypesMap.put("TIME", Constants.integerValueOf(mysqlToJavaType(11)));
/*     */ 
/* 528 */     mysqlToJdbcTypesMap.put("YEAR", Constants.integerValueOf(mysqlToJavaType(13)));
/*     */ 
/* 530 */     mysqlToJdbcTypesMap.put("TIMESTAMP", Constants.integerValueOf(mysqlToJavaType(7)));
/*     */ 
/* 532 */     mysqlToJdbcTypesMap.put("DATETIME", Constants.integerValueOf(mysqlToJavaType(12)));
/*     */ 
/* 534 */     mysqlToJdbcTypesMap.put("TINYBLOB", Constants.integerValueOf(-2));
/* 535 */     mysqlToJdbcTypesMap.put("BLOB", Constants.integerValueOf(-4));
/*     */ 
/* 537 */     mysqlToJdbcTypesMap.put("MEDIUMBLOB", Constants.integerValueOf(-4));
/*     */ 
/* 539 */     mysqlToJdbcTypesMap.put("LONGBLOB", Constants.integerValueOf(-4));
/*     */ 
/* 541 */     mysqlToJdbcTypesMap.put("TINYTEXT", Constants.integerValueOf(12));
/*     */ 
/* 543 */     mysqlToJdbcTypesMap.put("TEXT", Constants.integerValueOf(-1));
/*     */ 
/* 545 */     mysqlToJdbcTypesMap.put("MEDIUMTEXT", Constants.integerValueOf(-1));
/*     */ 
/* 547 */     mysqlToJdbcTypesMap.put("LONGTEXT", Constants.integerValueOf(-1));
/*     */ 
/* 549 */     mysqlToJdbcTypesMap.put("ENUM", Constants.integerValueOf(mysqlToJavaType(247)));
/*     */ 
/* 551 */     mysqlToJdbcTypesMap.put("SET", Constants.integerValueOf(mysqlToJavaType(248)));
/*     */ 
/* 553 */     mysqlToJdbcTypesMap.put("GEOMETRY", Constants.integerValueOf(mysqlToJavaType(255)));
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.MysqlDefs
 * JD-Core Version:    0.6.0
 */