package com.mysql.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

public abstract interface ConnectionLifecycleInterceptor extends Extension
{
  public abstract void close()
    throws SQLException;

  public abstract boolean commit()
    throws SQLException;

  public abstract boolean rollback()
    throws SQLException;

  public abstract boolean rollback(Savepoint paramSavepoint)
    throws SQLException;

  public abstract boolean setAutoCommit(boolean paramBoolean)
    throws SQLException;

  public abstract boolean setCatalog(String paramString)
    throws SQLException;

  public abstract boolean transactionBegun()
    throws SQLException;

  public abstract boolean transactionCompleted()
    throws SQLException;
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionLifecycleInterceptor
 * JD-Core Version:    0.6.0
 */