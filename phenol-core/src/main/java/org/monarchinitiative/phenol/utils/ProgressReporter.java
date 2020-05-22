package org.monarchinitiative.phenol.utils;

/**
 * Helper class for reporting.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ProgressReporter {

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
   * @param elementsLabel Label for element type (plural).
   * @param totalCount Total number of elements to process.
   */
  public ProgressReporter(String elementsLabel, int totalCount) {
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
    System.out.printf(
        "Resnik precomputation done by %d%% (%d of %d done, step elapsed: %.1f s, elapsed: "
            + "%.1f s, processing \"%.1f %s/s, estimated to go: %.1f s).\n",
          ((int) (100.0 * current / totalCount)),
          current,
          totalCount,
          stepElapsedSec,
          elapsedSec,
          elementsPerSec,
          elementsLabel,
          estimatedToGoSec
        );
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
