package org.monarchinitiative.phenol.annotations.formats.hpo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

class HpoGeneAnnotationsDefault implements HpoGeneAnnotations {

  private final List<HpoGeneAnnotation> annotations;

  HpoGeneAnnotationsDefault(Collection<HpoGeneAnnotation> annotations) {
    this.annotations = List.copyOf(Objects.requireNonNull(annotations));
  }

  @Override
  public Stream<HpoGeneAnnotation> stream() {
    return annotations.stream();
  }

  @Override
  public int size() {
    return annotations.size();
  }

  @Override
  public Iterator<HpoGeneAnnotation> iterator() {
    return annotations.iterator();
  }
}
