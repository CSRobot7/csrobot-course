package com.csrobot.course;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 注解实现
 */
@Aspect
@Component
public class DisableTenantFilterAspect {

  @Pointcut("@annotation(DisableTenantFilter)")
  public void service() {
  }

  @Around("service()")
  public Object execute(ProceedingJoinPoint pjp) throws Throwable {
    DisableTenantHolder.set(Boolean.TRUE);
    return pjp.proceed();
  }
}
