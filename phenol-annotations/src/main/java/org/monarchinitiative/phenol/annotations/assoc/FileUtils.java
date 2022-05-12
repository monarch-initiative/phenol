package org.monarchinitiative.phenol.annotations.assoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

class FileUtils {

  /**
   * Opens a file for reading, returning a <code>BufferedReader</code>.
   * Bytes are decoded into characters using {@link java.nio.charset.StandardCharsets#UTF_8}.
   * <p>
   * The file is assumed to be gzipped (and will be uncompressed on-the-fly) if the file name ends with <em>*.gz</em>.
   *
   * @param path path to a file
   * @return buffered reader for the <code>path</code>
   * @throws IOException if the <code>path</code> is not a file
   */
  static BufferedReader newBufferedReader(Path path) throws IOException {
    return fileNameEndsWithGz(path)
      ? new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(path)), StandardCharsets.UTF_8))
      : Files.newBufferedReader(path, StandardCharsets.UTF_8);
  }

  /**
   * Opens a file for reading, returning an unbuffered {@link InputStream}.
   * Bytes are decoded into characters using {@link java.nio.charset.StandardCharsets#UTF_8}.
   * <p>
   * The file is assumed to be gzipped (and will be uncompressed on-the-fly) if the file name ends with <em>*.gz</em>.
   *
   * @param path path to a file
   * @return input stream for the <code>path</code>
   * @throws IOException if the <code>path</code> is not a file
   */
  static InputStream newInputStream(Path path) throws IOException {
    return fileNameEndsWithGz(path)
      ? new GZIPInputStream(Files.newInputStream(path))
      : Files.newInputStream(path);

  }

  private static boolean fileNameEndsWithGz(Path path) {
    return path.toFile().getName().endsWith(".gz");
  }
}
