package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Iterator;

/**
 * {@linkplain AltTermIdIterator} iterates over the alternative IDs of {@linkplain Term}.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class AltTermIdIterator implements Iterator<TermId> {

  private final Iterator<Term> terms; // *
  private Iterator<TermId> altIds; // *
  private TermId next;

  AltTermIdIterator(Iterator<Term> terms) {
    this.terms = terms;
    this.next = init();
  }

  private TermId init() {
    while (terms.hasNext()) {
      Term next = terms.next();
      altIds = next.getAltTermIds().iterator();
      if (altIds.hasNext()) {
        return altIds.next();
      }
      // Try the next term.
    }
    return null;
  }

  private TermId advance() {
    if (altIds.hasNext())
      return altIds.next();
    else {
      while (terms.hasNext()) {
        Term next = terms.next();
        if (!next.getAltTermIds().isEmpty()) {
          altIds = next.getAltTermIds().iterator();
          return altIds.next();
        }
        // Try the next term.
      }
      return null;
    }
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public TermId next() {
    TermId next = this.next;
    this.next = advance();
    return next;
  }
}
