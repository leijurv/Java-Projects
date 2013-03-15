/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ConnectionImpl;
/*     */ import com.mysql.jdbc.Constants;
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class MysqlXAConnection extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private ConnectionImpl underlyingConnection;
/*     */   private static final Map MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
/*     */   private Log log;
/*     */   protected boolean logXaCommands;
/*     */   private static final Constructor JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
/*     */ 
/*     */   protected static MysqlXAConnection getInstance(ConnectionImpl mysqlConnection, boolean logXaCommands)
/*     */     throws SQLException
/*     */   {
/* 115 */     if (!Util.isJdbc4()) {
/* 116 */       return new MysqlXAConnection(mysqlConnection, logXaCommands);
/*     */     }
/*     */ 
/* 119 */     return (MysqlXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlConnection, Boolean.valueOf(logXaCommands) }, mysqlConnection.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public MysqlXAConnection(ConnectionImpl connection, boolean logXaCommands)
/*     */     throws SQLException
/*     */   {
/* 130 */     super(connection);
/* 131 */     this.underlyingConnection = connection;
/* 132 */     this.log = connection.getLog();
/* 133 */     this.logXaCommands = logXaCommands;
/*     */   }
/*     */ 
/*     */   public XAResource getXAResource()
/*     */     throws SQLException
/*     */   {
/* 146 */     return this;
/*     */   }
/*     */ 
/*     */   public int getTransactionTimeout()
/*     */     throws XAException
/*     */   {
/* 163 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean setTransactionTimeout(int arg0)
/*     */     throws XAException
/*     */   {
/* 188 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isSameRM(XAResource xares)
/*     */     throws XAException
/*     */   {
/* 208 */     if ((xares instanceof MysqlXAConnection)) {
/* 209 */       return this.underlyingConnection.isSameResource(((MysqlXAConnection)xares).underlyingConnection);
/*     */     }
/*     */ 
/* 213 */     return false;
/*     */   }
/*     */ 
/*     */   public Xid[] recover(int flag)
/*     */     throws XAException
/*     */   {
/* 254 */     return recover(this.underlyingConnection, flag);
/*     */   }
/*     */ 
/*     */   protected static Xid[] recover(Connection c, int flag)
/*     */     throws XAException
/*     */   {
/* 278 */     boolean startRscan = (flag & 0x1000000) > 0;
/* 279 */     boolean endRscan = (flag & 0x800000) > 0;
/*     */ 
/* 281 */     if ((!startRscan) && (!endRscan) && (flag != 0)) {
/* 282 */       throw new MysqlXAException(-5, "Invalid flag, must use TMNOFLAGS, or any combination of TMSTARTRSCAN and TMENDRSCAN", null);
/*     */     }
/*     */ 
/* 295 */     if (!startRscan) {
/* 296 */       return new Xid[0];
/*     */     }
/*     */ 
/* 299 */     ResultSet rs = null;
/* 300 */     Statement stmt = null;
/*     */ 
/* 302 */     List recoveredXidList = new ArrayList();
/*     */     try
/*     */     {
/* 306 */       stmt = c.createStatement();
/*     */ 
/* 308 */       rs = stmt.executeQuery("XA RECOVER");
/*     */ 
/* 310 */       while (rs.next()) {
/* 311 */         int formatId = rs.getInt(1);
/* 312 */         int gtridLength = rs.getInt(2);
/* 313 */         int bqualLength = rs.getInt(3);
/* 314 */         byte[] gtridAndBqual = rs.getBytes(4);
/*     */ 
/* 316 */         byte[] gtrid = new byte[gtridLength];
/* 317 */         byte[] bqual = new byte[bqualLength];
/*     */ 
/* 319 */         if (gtridAndBqual.length != gtridLength + bqualLength) {
/* 320 */           throw new MysqlXAException(105, "Error while recovering XIDs from RM. GTRID and BQUAL are wrong sizes", null);
/*     */         }
/*     */ 
/* 325 */         System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
/*     */ 
/* 327 */         System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
/*     */ 
/* 330 */         recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 334 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 336 */       if (rs != null) {
/*     */         try {
/* 338 */           rs.close();
/*     */         } catch (SQLException sqlEx) {
/* 340 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         }
/*     */       }
/*     */ 
/* 344 */       if (stmt != null) {
/*     */         try {
/* 346 */           stmt.close();
/*     */         } catch (SQLException sqlEx) {
/* 348 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 353 */     int numXids = recoveredXidList.size();
/*     */ 
/* 355 */     Xid[] asXids = new Xid[numXids];
/* 356 */     Object[] asObjects = recoveredXidList.toArray();
/*     */ 
/* 358 */     for (int i = 0; i < numXids; i++) {
/* 359 */       asXids[i] = ((Xid)asObjects[i]);
/*     */     }
/*     */ 
/* 362 */     return asXids;
/*     */   }
/*     */ 
/*     */   public int prepare(Xid xid)
/*     */     throws XAException
/*     */   {
/* 384 */     StringBuffer commandBuf = new StringBuffer();
/* 385 */     commandBuf.append("XA PREPARE ");
/* 386 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 388 */     dispatchCommand(commandBuf.toString());
/*     */ 
/* 390 */     return 0;
/*     */   }
/*     */ 
/*     */   public void forget(Xid xid)
/*     */     throws XAException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void rollback(Xid xid)
/*     */     throws XAException
/*     */   {
/* 426 */     StringBuffer commandBuf = new StringBuffer();
/* 427 */     commandBuf.append("XA ROLLBACK ");
/* 428 */     commandBuf.append(xidToString(xid));
/*     */     try
/*     */     {
/* 431 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 433 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void end(Xid xid, int flags)
/*     */     throws XAException
/*     */   {
/* 465 */     StringBuffer commandBuf = new StringBuffer();
/* 466 */     commandBuf.append("XA END ");
/* 467 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 469 */     switch (flags) {
/*     */     case 67108864:
/* 471 */       break;
/*     */     case 33554432:
/* 473 */       commandBuf.append(" SUSPEND");
/* 474 */       break;
/*     */     case 536870912:
/* 476 */       break;
/*     */     default:
/* 478 */       throw new XAException(-5);
/*     */     }
/*     */ 
/* 481 */     dispatchCommand(commandBuf.toString());
/*     */   }
/*     */ 
/*     */   public void start(Xid xid, int flags)
/*     */     throws XAException
/*     */   {
/* 508 */     StringBuffer commandBuf = new StringBuffer();
/* 509 */     commandBuf.append("XA START ");
/* 510 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 512 */     switch (flags) {
/*     */     case 2097152:
/* 514 */       commandBuf.append(" JOIN");
/* 515 */       break;
/*     */     case 134217728:
/* 517 */       commandBuf.append(" RESUME");
/* 518 */       break;
/*     */     case 0:
/* 521 */       break;
/*     */     default:
/* 523 */       throw new XAException(-5);
/*     */     }
/*     */ 
/* 526 */     dispatchCommand(commandBuf.toString());
/*     */ 
/* 528 */     this.underlyingConnection.setInGlobalTx(true);
/*     */   }
/*     */ 
/*     */   public void commit(Xid xid, boolean onePhase)
/*     */     throws XAException
/*     */   {
/* 553 */     StringBuffer commandBuf = new StringBuffer();
/* 554 */     commandBuf.append("XA COMMIT ");
/* 555 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 557 */     if (onePhase) {
/* 558 */       commandBuf.append(" ONE PHASE");
/*     */     }
/*     */     try
/*     */     {
/* 562 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 564 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private ResultSet dispatchCommand(String command) throws XAException {
/* 569 */     Statement stmt = null;
/*     */     try
/*     */     {
/* 572 */       if (this.logXaCommands) {
/* 573 */         this.log.logDebug("Executing XA statement: " + command);
/*     */       }
/*     */ 
/* 577 */       stmt = this.underlyingConnection.createStatement();
/*     */ 
/* 580 */       stmt.execute(command);
/*     */ 
/* 582 */       ResultSet rs = stmt.getResultSet();
/*     */ 
/* 584 */       localResultSet1 = rs;
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/*     */       ResultSet localResultSet1;
/* 586 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 588 */       if (stmt != null)
/*     */         try {
/* 590 */           stmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
/* 599 */     Integer xaCode = (Integer)MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Constants.integerValueOf(sqlEx.getErrorCode()));
/*     */ 
/* 602 */     if (xaCode != null) {
/* 603 */       return new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), null);
/*     */     }
/*     */ 
/* 607 */     return new MysqlXAException(sqlEx.getMessage(), null);
/*     */   }
/*     */ 
/*     */   private static String xidToString(Xid xid) {
/* 611 */     byte[] gtrid = xid.getGlobalTransactionId();
/*     */ 
/* 613 */     byte[] btrid = xid.getBranchQualifier();
/*     */ 
/* 615 */     int lengthAsString = 6;
/*     */ 
/* 617 */     if (gtrid != null) {
/* 618 */       lengthAsString += 2 * gtrid.length;
/*     */     }
/*     */ 
/* 621 */     if (btrid != null) {
/* 622 */       lengthAsString += 2 * btrid.length;
/*     */     }
/*     */ 
/* 625 */     String formatIdInHex = Integer.toHexString(xid.getFormatId());
/*     */ 
/* 627 */     lengthAsString += formatIdInHex.length();
/* 628 */     lengthAsString += 3;
/*     */ 
/* 630 */     StringBuffer asString = new StringBuffer(lengthAsString);
/*     */ 
/* 632 */     asString.append("0x");
/*     */ 
/* 634 */     if (gtrid != null) {
/* 635 */       for (int i = 0; i < gtrid.length; i++) {
/* 636 */         String asHex = Integer.toHexString(gtrid[i] & 0xFF);
/*     */ 
/* 638 */         if (asHex.length() == 1) {
/* 639 */           asString.append("0");
/*     */         }
/*     */ 
/* 642 */         asString.append(asHex);
/*     */       }
/*     */     }
/*     */ 
/* 646 */     asString.append(",");
/*     */ 
/* 648 */     if (btrid != null) {
/* 649 */       asString.append("0x");
/*     */ 
/* 651 */       for (int i = 0; i < btrid.length; i++) {
/* 652 */         String asHex = Integer.toHexString(btrid[i] & 0xFF);
/*     */ 
/* 654 */         if (asHex.length() == 1) {
/* 655 */           asString.append("0");
/*     */         }
/*     */ 
/* 658 */         asString.append(asHex);
/*     */       }
/*     */     }
/*     */ 
/* 662 */     asString.append(",0x");
/* 663 */     asString.append(formatIdInHex);
/*     */ 
/* 665 */     return asString.toString();
/*     */   }
/*     */ 
/*     */   public synchronized Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 674 */     Connection connToWrap = getConnection(false, true);
/*     */ 
/* 676 */     return connToWrap;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  80 */     HashMap temp = new HashMap();
/*     */ 
/*  82 */     temp.put(Constants.integerValueOf(1397), Constants.integerValueOf(-4));
/*  83 */     temp.put(Constants.integerValueOf(1398), Constants.integerValueOf(-5));
/*  84 */     temp.put(Constants.integerValueOf(1399), Constants.integerValueOf(-7));
/*  85 */     temp.put(Constants.integerValueOf(1400), Constants.integerValueOf(-9));
/*  86 */     temp.put(Constants.integerValueOf(1401), Constants.integerValueOf(-3));
/*  87 */     temp.put(Constants.integerValueOf(1402), Constants.integerValueOf(100));
/*     */ 
/*  89 */     MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
/*     */ 
/*  95 */     if (Util.isJdbc4())
/*     */       try {
/*  97 */         JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection").getConstructor(new Class[] { ConnectionImpl.class, Boolean.TYPE });
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/* 102 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/* 104 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/* 106 */         throw new RuntimeException(e);
/*     */       }
/*     */     else
/* 109 */       JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlXAConnection
 * JD-Core Version:    0.6.0
 */