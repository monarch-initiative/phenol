package org.monarchinitiative.phenol.annotations.formats.hpo;

import java.util.Collection;
import java.util.stream.Stream;

public interface HpoGeneAnnotations extends Iterable<HpoGeneAnnotation> {

  static HpoGeneAnnotations of(Collection<HpoGeneAnnotation> annotations) {
    return new HpoGeneAnnotationsDefault(annotations);
  }

  Stream<HpoGeneAnnotation> stream();

  int size();

}
