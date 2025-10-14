package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoClinicalModifierTermIds.CLINICAL_COURSE;
import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT;
import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoSubOntologyRootTermIds.*;

/**
 * An enum for aspect column of {@link HpoAnnotationLine}.
 */
public enum Aspect {

  P(PHENOTYPIC_ABNORMALITY),
  I(INHERITANCE_ROOT),
  C(CLINICAL_COURSE),
  M(CLINICAL_MODIFIER),
  H(PAST_MEDICAL_HISTORY);

  private final TermId termId;
  private static final Map<TermId, Aspect> BY_TERMID = new HashMap<>();


  static {
    for (Aspect e: values()) {
      BY_TERMID.put(e.termId, e);
    }
  }

  Aspect(TermId termId) {
    this.termId = termId;
  }

  public static Optional<Aspect> parse(String aspect) {
    switch (aspect.toUpperCase()) {
      case "P":
        return Optional.of(Aspect.P);
      case "I":
        return Optional.of(Aspect.I);
      case "C":
        return Optional.of(Aspect.C);
      case "M":
        return Optional.of(Aspect.M);
      case "H":
        return Optional.of(Aspect.H);
      default:
        return Optional.empty();
    }
  }

  public static Optional<Aspect> fromTermId(TermId termId) {
    return Optional.ofNullable(BY_TERMID.get(termId));
  }

}
