package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);

    // Use a dynamic proxy (java.lang.reflect.Proxy) to "wrap" the delegate in a
    //       ProfilingMethodInterceptor and return a dynamic proxy from this method.
    //       See https://docs.oracle.com/javase/10/docs/api/java/lang/reflect/Proxy.html.

    if (klass.getDeclaredMethods().length == 0 ||
            Arrays.stream(klass.getDeclaredMethods())
            .noneMatch(method -> method.getAnnotation(Profiled.class) != null))
      throw new IllegalArgumentException("This class does not has Profiled annotation");

      return (T) Proxy.newProxyInstance(ProfilerImpl.class.getClassLoader(), new Class[] {klass},
              new ProfilingMethodInterceptor(clock, state, delegate, startTime));
  }

  @Override
  public void writeData(Path path) {
    //  Write the ProfilingState data to the given file path. If a file already exists at that
    //       path, the new data should be appended to the existing file.

    if (path != null) {
      try (BufferedWriter w = Files.newBufferedWriter(path)) {
        writeData(w);
      } catch (IOException ioException) {
        System.err.print("IO Exception in write to Path: ");
        System.err.println(ioException.getMessage());
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}
