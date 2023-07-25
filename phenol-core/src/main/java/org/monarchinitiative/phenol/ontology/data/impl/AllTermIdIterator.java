package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Iterator;

/**
 * {@linkplain AllTermIdIterator} iterates over the primary {@link TermId}s followed by the alternative
 * {@linkplain TermId}s of a {@link java.util.Collection} of {@link Term}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class AllTermIdIterator implements Iterator<TermId> {

  private final Iterator<Term> terms; // *
  private Iterator<TermId> altIds; // *
  private TermId next;

  AllTermIdIterator(Iterator<Term> terms) {
    this.terms = terms;
    this.next = init();
  }

  private TermId init() {
    if (terms.hasNext()) {
      Term currentTerm = terms.next();
      altIds = currentTerm.getAltTermIds().iterator();
      return currentTerm.id();
    } else
      return null;
  }

  private TermId advance() {
    if (altIds.hasNext()) {
      return altIds.next();
    } else {
      if (terms.hasNext()) {
        Term currentTerm = terms.next();
        altIds = currentTerm.getAltTermIds().iterator();
        return currentTerm.id();
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
