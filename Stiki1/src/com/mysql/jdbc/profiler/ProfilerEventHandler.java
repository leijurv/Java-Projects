package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Extension;

public abstract interface ProfilerEventHandler extends Extension
{
  public abstract void consumeEvent(ProfilerEvent paramProfilerEvent);
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfilerEventHandler
 * JD-Core Version:    0.6.0
 */