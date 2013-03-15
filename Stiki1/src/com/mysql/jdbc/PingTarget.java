package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface PingTarget
{
  public abstract void doPing()
    throws SQLException;
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.PingTarget
 * JD-Core Version:    0.6.0
 */