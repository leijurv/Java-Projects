package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface RowData
{
  public static final int RESULT_SET_SIZE_UNKNOWN = -1;

  public abstract void addRow(ResultSetRow paramResultSetRow)
    throws SQLException;

  public abstract void afterLast()
    throws SQLException;

  public abstract void beforeFirst()
    throws SQLException;

  public abstract void beforeLast()
    throws SQLException;

  public abstract void close()
    throws SQLException;

  public abstract ResultSetRow getAt(int paramInt)
    throws SQLException;

  public abstract int getCurrentRowNumber()
    throws SQLException;

  public abstract ResultSetInternalMethods getOwner();

  public abstract boolean hasNext()
    throws SQLException;

  public abstract boolean isAfterLast()
    throws SQLException;

  public abstract boolean isBeforeFirst()
    throws SQLException;

  public abstract boolean isDynamic()
    throws SQLException;

  public abstract boolean isEmpty()
    throws SQLException;

  public abstract boolean isFirst()
    throws SQLException;

  public abstract boolean isLast()
    throws SQLException;

  public abstract void moveRowRelative(int paramInt)
    throws SQLException;

  public abstract ResultSetRow next()
    throws SQLException;

  public abstract void removeRow(int paramInt)
    throws SQLException;

  public abstract void setCurrentRow(int paramInt)
    throws SQLException;

  public abstract void setOwner(ResultSetImpl paramResultSetImpl);

  public abstract int size()
    throws SQLException;

  public abstract boolean wasEmpty();

  public abstract void setMetadata(Field[] paramArrayOfField);
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.RowData
 * JD-Core Version:    0.6.0
 */