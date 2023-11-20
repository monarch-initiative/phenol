package org.monarchinitiative.phenol.graph.csr.poly;

import java.util.Iterator;

class NodeIndexIterator implements Iterator<Integer> {

  private final Integer source;
  private final boolean includeSource;
  private final Iterator<Integer> base;
  private boolean yieldedSource = false;

  NodeIndexIterator(Integer source, boolean includeSource, Iterator<Integer> base) {
    this.source = source;
    this.includeSource = includeSource;
    this.base = base;
  }

  @Override
  public boolean hasNext() {
    // We're either up to yield the source or there are still some terms left.
    return sourceShouldBeYielded() || base.hasNext();
  }

  @Override
  public Integer next() {
    if (sourceShouldBeYielded()) {
      yieldedSource = true;
      return source;
    }

    return base.next();
  }

  private boolean sourceShouldBeYielded() {
    return !yieldedSource && includeSource;
  }
}
