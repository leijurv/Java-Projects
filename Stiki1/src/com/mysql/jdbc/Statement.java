package com.mysql.jdbc;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface Statement extends java.sql.Statement
{
  public abstract void enableStreamingResults()
    throws SQLException;

  public abstract void disableStreamingResults()
    throws SQLException;

  public abstract void setLocalInfileInputStream(InputStream paramInputStream);

  public abstract InputStream getLocalInfileInputStream();

  public abstract void setPingTarget(PingTarget paramPingTarget);

  public abstract ExceptionInterceptor getExceptionInterceptor();

  public abstract void removeOpenResultSet(ResultSet paramResultSet);

  public abstract int getOpenResultSetCount();
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Statement
 * JD-Core Version:    0.6.0
 */