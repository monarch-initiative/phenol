package org.monarchinitiative.phenol.graph.csr.mono;

/**
 * Essentially a record with {@link StaticCsrArray}s for getting parents and children.
 *
 * @param <T> type of data.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
class CsrData<T> {

  private final StaticCsrArray<T> parents;
  private final StaticCsrArray<T> children;

  CsrData(StaticCsrArray<T> parents, StaticCsrArray<T> children) {
    this.parents = parents;
    this.children = children;
  }

  StaticCsrArray<T> getParents() {
    return parents;
  }

  StaticCsrArray<T> getChildren() {
    return children;
  }
}
