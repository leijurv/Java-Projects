/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   protected static Method systemNanoTimeMethod;
/*     */   private static Method CAST_METHOD;
/*     */   private static final TimeZone DEFAULT_TIMEZONE;
/*     */   private static Util enclosingInstance;
/*     */   private static boolean isJdbc4;
/*     */   private static boolean isColdFusion;
/*     */ 
/*     */   public static boolean nanoTimeAvailable()
/*     */   {
/*  66 */     return systemNanoTimeMethod != null;
/*     */   }
/*     */ 
/*     */   static final TimeZone getDefaultTimeZone()
/*     */   {
/*  77 */     return (TimeZone)DEFAULT_TIMEZONE.clone();
/*     */   }
/*     */ 
/*     */   public static boolean isJdbc4()
/*     */   {
/* 131 */     return isJdbc4;
/*     */   }
/*     */ 
/*     */   public static boolean isColdFusion() {
/* 135 */     return isColdFusion;
/*     */   }
/*     */ 
/*     */   static String newCrypt(String password, String seed)
/*     */   {
/* 143 */     if ((password == null) || (password.length() == 0)) {
/* 144 */       return password;
/*     */     }
/*     */ 
/* 147 */     long[] pw = newHash(seed);
/* 148 */     long[] msg = newHash(password);
/* 149 */     long max = 1073741823L;
/* 150 */     long seed1 = (pw[0] ^ msg[0]) % max;
/* 151 */     long seed2 = (pw[1] ^ msg[1]) % max;
/* 152 */     char[] chars = new char[seed.length()];
/*     */ 
/* 154 */     for (int i = 0; i < seed.length(); i++) {
/* 155 */       seed1 = (seed1 * 3L + seed2) % max;
/* 156 */       seed2 = (seed1 + seed2 + 33L) % max;
/* 157 */       double d = seed1 / max;
/* 158 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 159 */       chars[i] = (char)b;
/*     */     }
/*     */ 
/* 162 */     seed1 = (seed1 * 3L + seed2) % max;
/* 163 */     seed2 = (seed1 + seed2 + 33L) % max;
/* 164 */     double d = seed1 / max;
/* 165 */     byte b = (byte)(int)Math.floor(d * 31.0D);
/*     */ 
/* 167 */     for (int i = 0; i < seed.length(); tmp205_203++)
/*     */     {
/*     */       int tmp205_203 = i;
/*     */       char[] tmp205_201 = chars; tmp205_201[tmp205_203] = (char)(tmp205_201[tmp205_203] ^ (char)b);
/*     */     }
/*     */ 
/* 171 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   static long[] newHash(String password) {
/* 175 */     long nr = 1345345333L;
/* 176 */     long add = 7L;
/* 177 */     long nr2 = 305419889L;
/*     */ 
/* 180 */     for (int i = 0; i < password.length(); i++) {
/* 181 */       if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 185 */       long tmp = 0xFF & password.charAt(i);
/* 186 */       nr ^= ((nr & 0x3F) + add) * tmp + (nr << 8);
/* 187 */       nr2 += (nr2 << 8 ^ nr);
/* 188 */       add += tmp;
/*     */     }
/*     */ 
/* 191 */     long[] result = new long[2];
/* 192 */     result[0] = (nr & 0x7FFFFFFF);
/* 193 */     result[1] = (nr2 & 0x7FFFFFFF);
/*     */ 
/* 195 */     return result;
/*     */   }
/*     */ 
/*     */   static String oldCrypt(String password, String seed)
/*     */   {
/* 203 */     long max = 33554431L;
/*     */ 
/* 207 */     if ((password == null) || (password.length() == 0)) {
/* 208 */       return password;
/*     */     }
/*     */ 
/* 211 */     long hp = oldHash(seed);
/* 212 */     long hm = oldHash(password);
/*     */ 
/* 214 */     long nr = hp ^ hm;
/* 215 */     nr %= max;
/* 216 */     long s1 = nr;
/* 217 */     long s2 = nr / 2L;
/*     */ 
/* 219 */     char[] chars = new char[seed.length()];
/*     */ 
/* 221 */     for (int i = 0; i < seed.length(); i++) {
/* 222 */       s1 = (s1 * 3L + s2) % max;
/* 223 */       s2 = (s1 + s2 + 33L) % max;
/* 224 */       double d = s1 / max;
/* 225 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 226 */       chars[i] = (char)b;
/*     */     }
/*     */ 
/* 229 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   static long oldHash(String password) {
/* 233 */     long nr = 1345345333L;
/* 234 */     long nr2 = 7L;
/*     */ 
/* 237 */     for (int i = 0; i < password.length(); i++) {
/* 238 */       if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 242 */       long tmp = password.charAt(i);
/* 243 */       nr ^= ((nr & 0x3F) + nr2) * tmp + (nr << 8);
/* 244 */       nr2 += tmp;
/*     */     }
/*     */ 
/* 247 */     return nr & 0x7FFFFFFF;
/*     */   }
/*     */ 
/*     */   private static RandStructcture randomInit(long seed1, long seed2)
/*     */   {
/*     */     Util tmp7_4 = enclosingInstance; tmp7_4.getClass(); RandStructcture randStruct = new RandStructcture();
/*     */ 
/* 253 */     randStruct.maxValue = 1073741823L;
/* 254 */     randStruct.maxValueDbl = randStruct.maxValue;
/* 255 */     randStruct.seed1 = (seed1 % randStruct.maxValue);
/* 256 */     randStruct.seed2 = (seed2 % randStruct.maxValue);
/*     */ 
/* 258 */     return randStruct;
/*     */   }
/*     */ 
/*     */   public static Object readObject(ResultSet resultSet, int index)
/*     */     throws Exception
/*     */   {
/* 276 */     ObjectInputStream objIn = new ObjectInputStream(resultSet.getBinaryStream(index));
/*     */ 
/* 278 */     Object obj = objIn.readObject();
/* 279 */     objIn.close();
/*     */ 
/* 281 */     return obj;
/*     */   }
/*     */ 
/*     */   private static double rnd(RandStructcture randStruct) {
/* 285 */     randStruct.seed1 = ((randStruct.seed1 * 3L + randStruct.seed2) % randStruct.maxValue);
/*     */ 
/* 287 */     randStruct.seed2 = ((randStruct.seed1 + randStruct.seed2 + 33L) % randStruct.maxValue);
/*     */ 
/* 290 */     return randStruct.seed1 / randStruct.maxValueDbl;
/*     */   }
/*     */ 
/*     */   public static String scramble(String message, String password)
/*     */   {
/* 306 */     byte[] to = new byte[8];
/* 307 */     String val = "";
/*     */ 
/* 309 */     message = message.substring(0, 8);
/*     */ 
/* 311 */     if ((password != null) && (password.length() > 0)) {
/* 312 */       long[] hashPass = newHash(password);
/* 313 */       long[] hashMessage = newHash(message);
/*     */ 
/* 315 */       RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);
/*     */ 
/* 318 */       int msgPos = 0;
/* 319 */       int msgLength = message.length();
/* 320 */       int toPos = 0;
/*     */ 
/* 322 */       while (msgPos++ < msgLength) {
/* 323 */         to[(toPos++)] = (byte)(int)(Math.floor(rnd(randStruct) * 31.0D) + 64.0D);
/*     */       }
/*     */ 
/* 327 */       byte extra = (byte)(int)Math.floor(rnd(randStruct) * 31.0D);
/*     */ 
/* 329 */       for (int i = 0; i < to.length; i++)
/*     */       {
/*     */         int tmp140_138 = i;
/*     */         byte[] tmp140_136 = to; tmp140_136[tmp140_138] = (byte)(tmp140_136[tmp140_138] ^ extra);
/*     */       }
/*     */ 
/* 333 */       val = new String(to);
/*     */     }
/*     */ 
/* 336 */     return val;
/*     */   }
/*     */ 
/*     */   public static String stackTraceToString(Throwable ex)
/*     */   {
/* 352 */     StringBuffer traceBuf = new StringBuffer();
/* 353 */     traceBuf.append(Messages.getString("Util.1"));
/*     */ 
/* 355 */     if (ex != null) {
/* 356 */       traceBuf.append(ex.getClass().getName());
/*     */ 
/* 358 */       String message = ex.getMessage();
/*     */ 
/* 360 */       if (message != null) {
/* 361 */         traceBuf.append(Messages.getString("Util.2"));
/* 362 */         traceBuf.append(message);
/*     */       }
/*     */ 
/* 365 */       StringWriter out = new StringWriter();
/*     */ 
/* 367 */       PrintWriter printOut = new PrintWriter(out);
/*     */ 
/* 369 */       ex.printStackTrace(printOut);
/*     */ 
/* 371 */       traceBuf.append(Messages.getString("Util.3"));
/* 372 */       traceBuf.append(out.toString());
/*     */     }
/*     */ 
/* 375 */     traceBuf.append(Messages.getString("Util.4"));
/*     */ 
/* 377 */     return traceBuf.toString();
/*     */   }
/*     */ 
/*     */   public static Object getInstance(String className, Class[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 384 */       return handleNewInstance(Class.forName(className).getConstructor(argTypes), args, exceptionInterceptor);
/*     */     }
/*     */     catch (SecurityException e) {
/* 387 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 391 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/*     */     }
/* 395 */     throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public static final Object handleNewInstance(Constructor ctor, Object[] args, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*     */     Throwable target;
/*     */     try
/*     */     {
/* 409 */       return ctor.newInstance(args);
/*     */     } catch (IllegalArgumentException e) {
/* 411 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (InstantiationException e)
/*     */     {
/* 415 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/* 419 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 423 */       target = e.getTargetException();
/*     */ 
/* 425 */       if ((target instanceof SQLException)) {
/* 426 */         throw ((SQLException)target);
/*     */       }
/*     */ 
/* 429 */       if ((target instanceof ExceptionInInitializerError)) {
/* 430 */         target = ((ExceptionInInitializerError)target).getException();
/*     */       }
/*     */     }
/* 433 */     throw SQLError.createSQLException(target.toString(), "S1000", exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public static boolean interfaceExists(String hostname)
/*     */   {
/*     */     try
/*     */     {
/* 448 */       Class networkInterfaceClass = Class.forName("java.net.NetworkInterface");
/*     */ 
/* 450 */       return networkInterfaceClass.getMethod("getByName", null).invoke(networkInterfaceClass, new Object[] { hostname }) != null;
/*     */     } catch (Throwable t) {
/*     */     }
/* 453 */     return false;
/*     */   }
/*     */ 
/*     */   public static Object cast(Object invokeOn, Object toCast)
/*     */   {
/* 466 */     if (CAST_METHOD != null) {
/*     */       try {
/* 468 */         return CAST_METHOD.invoke(invokeOn, new Object[] { toCast });
/*     */       } catch (Throwable t) {
/* 470 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 474 */     return null;
/*     */   }
/*     */ 
/*     */   public static long getCurrentTimeNanosOrMillis() {
/* 478 */     if (systemNanoTimeMethod != null)
/*     */       try {
/* 480 */         return ((Long)systemNanoTimeMethod.invoke(null, null)).longValue();
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/*     */       }
/* 491 */     return System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs) throws SQLException
/*     */   {
/* 496 */     while (rs.next())
/* 497 */       mappedValues.put(rs.getObject(1), rs.getObject(2));
/*     */   }
/*     */ 
/*     */   public static Map calculateDifferences(Map map1, Map map2)
/*     */   {
/* 502 */     Map diffMap = new HashMap();
/*     */ 
/* 504 */     Iterator map1Entries = map1.entrySet().iterator();
/*     */ 
/* 506 */     while (map1Entries.hasNext()) {
/* 507 */       Map.Entry entry = (Map.Entry)map1Entries.next();
/* 508 */       Object key = entry.getKey();
/*     */ 
/* 510 */       Number value1 = null;
/* 511 */       Number value2 = null;
/*     */ 
/* 513 */       if ((entry.getValue() instanceof Number))
/*     */       {
/* 515 */         value1 = (Number)entry.getValue();
/* 516 */         value2 = (Number)map2.get(key);
/*     */       } else {
/*     */         try {
/* 519 */           value1 = new Double(entry.getValue().toString());
/* 520 */           value2 = new Double(map2.get(key).toString()); } catch (NumberFormatException nfe) {
/*     */         }
/* 522 */         continue;
/*     */       }
/*     */ 
/* 526 */       if (value1.equals(value2))
/*     */       {
/*     */         continue;
/*     */       }
/* 530 */       if ((value1 instanceof Byte)) {
/* 531 */         diffMap.put(key, new Byte((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue())));
/*     */       }
/* 534 */       else if ((value1 instanceof Short)) {
/* 535 */         diffMap.put(key, new Short((short)(((Short)value2).shortValue() - ((Short)value1).shortValue())));
/*     */       }
/* 537 */       else if ((value1 instanceof Integer)) {
/* 538 */         diffMap.put(key, new Integer(((Integer)value2).intValue() - ((Integer)value1).intValue()));
/*     */       }
/* 541 */       else if ((value1 instanceof Long)) {
/* 542 */         diffMap.put(key, new Long(((Long)value2).longValue() - ((Long)value1).longValue()));
/*     */       }
/* 545 */       else if ((value1 instanceof Float)) {
/* 546 */         diffMap.put(key, new Float(((Float)value2).floatValue() - ((Float)value1).floatValue()));
/*     */       }
/* 548 */       else if ((value1 instanceof Double)) {
/* 549 */         diffMap.put(key, new Double(((Double)value2).shortValue() - ((Double)value1).shortValue()));
/*     */       }
/* 552 */       else if ((value1 instanceof BigDecimal)) {
/* 553 */         diffMap.put(key, ((BigDecimal)value2).subtract((BigDecimal)value1));
/*     */       }
/* 555 */       else if ((value1 instanceof BigInteger)) {
/* 556 */         diffMap.put(key, ((BigInteger)value2).subtract((BigInteger)value1));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 561 */     return diffMap;
/*     */   }
/*     */ 
/*     */   public static List loadExtensions(Connection conn, Properties props, String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 567 */     List extensionList = new LinkedList();
/*     */ 
/* 569 */     List interceptorsToCreate = StringUtils.split(extensionClassNames, ",", true);
/*     */ 
/* 572 */     Iterator iter = interceptorsToCreate.iterator();
/*     */ 
/* 574 */     String className = null;
/*     */     try
/*     */     {
/* 577 */       while (iter.hasNext()) {
/* 578 */         className = iter.next().toString();
/* 579 */         Extension extensionInstance = (Extension)Class.forName(className).newInstance();
/*     */ 
/* 581 */         extensionInstance.init(conn, props);
/*     */ 
/* 583 */         extensionList.add(extensionInstance);
/*     */       }
/*     */     } catch (Throwable t) {
/* 586 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString(errorMessageKey, new Object[] { className }), exceptionInterceptor);
/*     */ 
/* 588 */       sqlEx.initCause(t);
/*     */ 
/* 590 */       throw sqlEx;
/*     */     }
/*     */ 
/* 593 */     return extensionList;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  57 */       systemNanoTimeMethod = System.class.getMethod("nanoTime", null);
/*     */     } catch (SecurityException e) {
/*  59 */       systemNanoTimeMethod = null;
/*     */     } catch (NoSuchMethodException e) {
/*  61 */       systemNanoTimeMethod = null;
/*     */     }
/*     */ 
/*  74 */     DEFAULT_TIMEZONE = TimeZone.getDefault();
/*     */ 
/*  90 */     enclosingInstance = new Util();
/*     */ 
/*  92 */     isJdbc4 = false;
/*     */ 
/*  94 */     isColdFusion = false;
/*     */     try
/*     */     {
/*  98 */       CAST_METHOD = class$java$lang$Class.getMethod("cast", new Class[] { Object.class });
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */     try
/*     */     {
/* 105 */       Class.forName("java.sql.NClob");
/* 106 */       isJdbc4 = true;
/*     */     } catch (Throwable t) {
/* 108 */       isJdbc4 = false;
/*     */     }
/*     */ 
/* 118 */     String loadedFrom = stackTraceToString(new Throwable());
/*     */ 
/* 120 */     if (loadedFrom != null)
/* 121 */       isColdFusion = loadedFrom.indexOf("coldfusion") != -1;
/*     */     else
/* 123 */       isColdFusion = false;
/*     */   }
/*     */ 
/*     */   class RandStructcture
/*     */   {
/*     */     long maxValue;
/*     */     double maxValueDbl;
/*     */     long seed1;
/*     */     long seed2;
/*     */ 
/*     */     RandStructcture()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Util
 * JD-Core Version:    0.6.0
 */