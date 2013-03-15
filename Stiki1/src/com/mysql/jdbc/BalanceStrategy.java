package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract interface BalanceStrategy extends Extension
{
  public abstract Connection pickConnection(LoadBalancingConnectionProxy paramLoadBalancingConnectionProxy, List paramList, Map paramMap, long[] paramArrayOfLong, int paramInt)
    throws SQLException;
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.BalanceStrategy
 * JD-Core Version:    0.6.0
 */