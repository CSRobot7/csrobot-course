package com.csrobot.course.context;

public class ContextUtils {

  public static Long currentTenantId() {
    return RequestContextHolder.get().getTenantId();
  }

}
