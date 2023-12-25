package com.csrobot.course.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public class RequestContextHolder {
  private static final ThreadLocal<RequestContext> REQUEST_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

  public static void set(RequestContext requestContext) {
    REQUEST_CONTEXT_THREAD_LOCAL.set(requestContext);
  }

  public static RequestContext get() {
    return REQUEST_CONTEXT_THREAD_LOCAL.get();
  }

}
