package org.monarchinitiative.phenol.annotations;

import java.nio.file.Path;

public class TestBase {

  public static final double ERROR = 5e-4;
  public static final Path TEST_BASE = Path.of("src/test/resources");

  private TestBase() {
  }

}
