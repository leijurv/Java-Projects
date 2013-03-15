package com.mysql.jdbc;

import com.mysql.jdbc.log.Log;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

public abstract interface Connection extends java.sql.Connection, ConnectionProperties
{
  public abstract void changeUser(String paramString1, String paramString2)
    throws SQLException;

  public abstract void clearHasTriedMaster();

  public abstract PreparedStatement clientPrepareStatement(String paramString)
    throws SQLException;

  public abstract PreparedStatement clientPrepareStatement(String paramString, int paramInt)
    throws SQLException;

  public abstract PreparedStatement clientPrepareStatement(String paramString, int paramInt1, int paramInt2)
    throws SQLException;

  public abstract PreparedStatement clientPrepareStatement(String paramString, int[] paramArrayOfInt)
    throws SQLException;

  public abstract PreparedStatement clientPrepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException;

  public abstract PreparedStatement clientPrepareStatement(String paramString, String[] paramArrayOfString)
    throws SQLException;

  public abstract int getActiveStatementCount();

  public abstract long getIdleFor();

  public abstract Log getLog()
    throws SQLException;

  public abstract String getServerCharacterEncoding();

  public abstract TimeZone getServerTimezoneTZ();

  public abstract String getStatementComment();

  public abstract boolean hasTriedMaster();

  public abstract boolean isInGlobalTx();

  public abstract void setInGlobalTx(boolean paramBoolean);

  public abstract boolean isMasterConnection();

  public abstract boolean isNoBackslashEscapesSet();

  public abstract boolean isSameResource(Connection paramConnection);

  public abstract boolean lowerCaseTableNames();

  public abstract boolean parserKnowsUnicode();

  public abstract void ping()
    throws SQLException;

  public abstract void resetServerState()
    throws SQLException;

  public abstract PreparedStatement serverPrepareStatement(String paramString)
    throws SQLException;

  public abstract PreparedStatement serverPrepareStatement(String paramString, int paramInt)
    throws SQLException;

  public abstract PreparedStatement serverPrepareStatement(String paramString, int paramInt1, int paramInt2)
    throws SQLException;

  public abstract PreparedStatement serverPrepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException;

  public abstract PreparedStatement serverPrepareStatement(String paramString, int[] paramArrayOfInt)
    throws SQLException;

  public abstract PreparedStatement serverPrepareStatement(String paramString, String[] paramArrayOfString)
    throws SQLException;

  public abstract void setFailedOver(boolean paramBoolean);

  public abstract void setPreferSlaveDuringFailover(boolean paramBoolean);

  public abstract void setStatementComment(String paramString);

  public abstract void shutdownServer()
    throws SQLException;

  public abstract boolean supportsIsolationLevel();

  public abstract boolean supportsQuotedIdentifiers();

  public abstract boolean supportsTransactions();

  public abstract boolean versionMeetsMinimum(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException;

  public abstract void reportQueryTime(long paramLong);

  public abstract boolean isAbonormallyLongQuery(long paramLong);

  public abstract void initializeExtension(Extension paramExtension)
    throws SQLException;

  public abstract int getAutoIncrementIncrement();

  public abstract boolean hasSameProperties(Connection paramConnection);

  public abstract Properties getProperties();
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Connection
 * JD-Core Version:    0.6.0
 */