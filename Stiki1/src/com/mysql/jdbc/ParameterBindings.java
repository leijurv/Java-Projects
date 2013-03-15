package com.mysql.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public abstract interface ParameterBindings
{
  public abstract Array getArray(int paramInt)
    throws SQLException;

  public abstract InputStream getAsciiStream(int paramInt)
    throws SQLException;

  public abstract BigDecimal getBigDecimal(int paramInt)
    throws SQLException;

  public abstract InputStream getBinaryStream(int paramInt)
    throws SQLException;

  public abstract Blob getBlob(int paramInt)
    throws SQLException;

  public abstract boolean getBoolean(int paramInt)
    throws SQLException;

  public abstract byte getByte(int paramInt)
    throws SQLException;

  public abstract byte[] getBytes(int paramInt)
    throws SQLException;

  public abstract Reader getCharacterStream(int paramInt)
    throws SQLException;

  public abstract Clob getClob(int paramInt)
    throws SQLException;

  public abstract Date getDate(int paramInt)
    throws SQLException;

  public abstract double getDouble(int paramInt)
    throws SQLException;

  public abstract float getFloat(int paramInt)
    throws SQLException;

  public abstract int getInt(int paramInt)
    throws SQLException;

  public abstract long getLong(int paramInt)
    throws SQLException;

  public abstract Reader getNCharacterStream(int paramInt)
    throws SQLException;

  public abstract Reader getNClob(int paramInt)
    throws SQLException;

  public abstract Object getObject(int paramInt)
    throws SQLException;

  public abstract Ref getRef(int paramInt)
    throws SQLException;

  public abstract short getShort(int paramInt)
    throws SQLException;

  public abstract String getString(int paramInt)
    throws SQLException;

  public abstract Time getTime(int paramInt)
    throws SQLException;

  public abstract Timestamp getTimestamp(int paramInt)
    throws SQLException;

  public abstract URL getURL(int paramInt)
    throws SQLException;

  public abstract boolean isNull(int paramInt)
    throws SQLException;
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ParameterBindings
 * JD-Core Version:    0.6.0
 */