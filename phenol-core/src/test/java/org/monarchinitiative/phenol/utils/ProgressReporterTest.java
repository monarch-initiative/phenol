package org.monarchinitiative.phenol.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.slf4j.Logger;

public class ProgressReporterTest {

  Logger logger;

  @Before
  public void setUp() {
    logger = mock(Logger.class);
  }

  @Test
  public void testStartProgressEndInfoToLogger() {
    ProgressReporter reporter = new ProgressReporter(logger, "elements", 100);
    reporter.start();
    for (int i = 0; i < 100; ++i) {
      reporter.incCurrent();
    }
    reporter.stop();

    // Just check count and that String-varargs is there.
    verify(logger, times(101)).info(any(String.class), ArgumentMatchers.<Object[]>any());
  }
}
