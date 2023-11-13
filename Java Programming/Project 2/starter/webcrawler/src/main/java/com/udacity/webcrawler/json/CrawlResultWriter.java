package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   */
  public void write(Path path) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(path);
    //  Fill in this method.
    try (BufferedWriter w = Files.newBufferedWriter(path)) {
      write(w);
    } catch (IOException ioException) {
      System.err.print("IO Exception in write to Path: ");
      System.err.println(ioException.getMessage());
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   */
  public void write(Writer writer) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(writer);
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

      objectMapper.writeValue(writer, result);
    }  catch (IOException ex) {
      System.err.println("Writing process is failed");
      System.err.println(ex.getMessage());
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
