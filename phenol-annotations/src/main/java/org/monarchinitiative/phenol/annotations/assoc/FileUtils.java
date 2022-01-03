package org.monarchinitiative.phenol.annotations.assoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

class FileUtils {

  static BufferedReader openForReading(Path geneInfoFile) throws IOException {
    return geneInfoFile.toFile().getName().endsWith(".gz")
      ? new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(geneInfoFile))))
      : Files.newBufferedReader(geneInfoFile);
  }
}
