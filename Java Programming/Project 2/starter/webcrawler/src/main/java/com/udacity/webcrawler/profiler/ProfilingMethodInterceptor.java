package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final ProfilingState profilingState;
  private final Object delegate;
  private ZonedDateTime startTime;

  // You will need to add more instance fields and constructor arguments to this class.


  public ProfilingMethodInterceptor(Clock clock, ProfilingState profilingState, Object delegate, ZonedDateTime startTime) {
    this.clock = clock;
    this.profilingState = profilingState;
    this.delegate = delegate;
    this.startTime = startTime;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    //  This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
    Object invokeObject;
    if (method.isAnnotationPresent(Profiled.class)) startTime = ZonedDateTime.now(clock);
    try {
      invokeObject = method.invoke(delegate, args);
    } catch (InvocationTargetException | IllegalAccessException e) {
      System.err.println(e.getMessage());
      throw e.getCause();
    } finally {
      if (method.isAnnotationPresent(Profiled.class)) {
        Duration duration = Duration.between(startTime, ZonedDateTime.now(clock));
        profilingState.record(delegate.getClass(), method, duration);
      }
    }

    return invokeObject;
  }
}
