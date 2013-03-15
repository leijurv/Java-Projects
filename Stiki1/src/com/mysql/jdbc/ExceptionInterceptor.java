package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface ExceptionInterceptor extends Extension
{
  public abstract SQLException interceptException(SQLException paramSQLException, Connection paramConnection);
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ExceptionInterceptor
 * JD-Core Version:    0.6.0
 */