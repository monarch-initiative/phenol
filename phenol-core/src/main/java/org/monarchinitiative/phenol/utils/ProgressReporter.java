package org.monarchinitiative.phenol.utils;

import org.slf4j.Logger;

/**
 * Helper class for reporting.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ProgressReporter {

  /** Logger to use. */
  private final Logger logger;

  /** Label for elements, plural. */
  private final String elementsLabel;

  /** Total number of elements to process. */
  private final int totalCount;

  /** Value at last step. */
  private int stepCount;

  /** Currently processed element. */
  private int current;

  /** Start timestamp. */
  private double startTime;

  /** Timestamp of last step. */
  private double lastStepTime;

  /**
   * Constructor.
   *
   * @param logger {@link Logger} to write to
   * @param elementsLabel Label for element type (plural).
   * @param totalCount Total number of elements to process.
   */
  public ProgressReporter(Logger logger, String elementsLabel, int totalCount) {
    this.logger = logger;
    this.elementsLabel = elementsLabel;
    this.totalCount = totalCount;
    this.current = 0;
    this.stepCount = 0;
  }

  /** Increment progress counter. */
  public synchronized void incCurrent() {
    setCurrent(this.current + 1);
  }

  /**
   * Set progress to <code>current</code>.
   *
   * @param current Current step's value.
   */
  public synchronized void setCurrent(int current) {
    int oldCurrent = this.current;
    this.current = current;
    if ((100 * oldCurrent / totalCount) != (100 * current / totalCount)) {
      printProgress();
    }
  }

  /** Print progress to the current position. */
  public synchronized void printProgress() {
    final long now = System.nanoTime();
    final double stepElapsedSec = (now - lastStepTime) / 1_000_000_000.0;
    final double elapsedSec = (now - startTime) / 1_000_000_000.0;
    final double elementsPerSec = (current - stepCount) / stepElapsedSec;
    final double estimatedToGoSec = (totalCount - current) / elementsPerSec;
    this.lastStepTime = now;
    this.stepCount = current;

    logger.info(
        "Resnik precomputation done by {}% ({} of {} done, step elapsed: {} s, elapsed: "
            + "{} s, processing {} {}/s, estimated to go: {} s",
        new Object[] {
          ((int) (100.0 * current / totalCount)),
          current,
          totalCount,
          stepElapsedSec,
          elapsedSec,
          elementsPerSec,
          elementsLabel,
          estimatedToGoSec
        });
  }

  /** Set start timer. */
  public synchronized void start() {
    this.startTime = System.nanoTime();
    this.lastStepTime = startTime;
  }

  /** Set current value to total value and print. */
  public synchronized void stop() {
    if (current != totalCount) {
      current = totalCount;
    }
    printProgress();
  }
}
