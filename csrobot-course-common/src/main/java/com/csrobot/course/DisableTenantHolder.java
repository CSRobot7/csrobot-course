package com.csrobot.course;

import com.alibaba.ttl.TransmittableThreadLocal;

public class DisableTenantHolder {
  private static final ThreadLocal<Boolean> TENANT_DISABLE_HOLDER = new TransmittableThreadLocal<>();

  public static void set(Boolean status) {
    TENANT_DISABLE_HOLDER.set(status);
  }

  public static Boolean get() {
    return TENANT_DISABLE_HOLDER.get();
  }

  public static void clear() {
    TENANT_DISABLE_HOLDER.remove();
  }
}
