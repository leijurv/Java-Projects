package com.mysql.jdbc.log;

public abstract interface Log
{
  public abstract boolean isDebugEnabled();

  public abstract boolean isErrorEnabled();

  public abstract boolean isFatalEnabled();

  public abstract boolean isInfoEnabled();

  public abstract boolean isTraceEnabled();

  public abstract boolean isWarnEnabled();

  public abstract void logDebug(Object paramObject);

  public abstract void logDebug(Object paramObject, Throwable paramThrowable);

  public abstract void logError(Object paramObject);

  public abstract void logError(Object paramObject, Throwable paramThrowable);

  public abstract void logFatal(Object paramObject);

  public abstract void logFatal(Object paramObject, Throwable paramThrowable);

  public abstract void logInfo(Object paramObject);

  public abstract void logInfo(Object paramObject, Throwable paramThrowable);

  public abstract void logTrace(Object paramObject);

  public abstract void logTrace(Object paramObject, Throwable paramThrowable);

  public abstract void logWarn(Object paramObject);

  public abstract void logWarn(Object paramObject, Throwable paramThrowable);
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.Log
 * JD-Core Version:    0.6.0
 */