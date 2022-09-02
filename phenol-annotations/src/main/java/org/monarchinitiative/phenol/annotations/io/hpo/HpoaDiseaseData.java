package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItem;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * All HPOA lines available for a single disease.
 */
public class HpoaDiseaseData implements AnnotatedItem {

  private final TermId id;
  private final String name;
  private final List<HpoAnnotationLine> annotationLines;

  HpoaDiseaseData(TermId id, String name, List<HpoAnnotationLine> annotationLines) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
    this.annotationLines = Objects.requireNonNull(annotationLines);
  }

  @Override
  public TermId id() {
    return id;
  }

  public String name() {
    return name;
  }

  @Override
  public Collection<? extends Identified> annotations() {
    // TODO - should we return all annotations or only non-negated annotations?
//    return annotationLines.stream()
//      .filter(a -> !a.isNegated())
//      .collect(Collectors.toList());
    return annotationLines;
  }

  public List<HpoAnnotationLine> annotationLines() {
    return annotationLines;
  }

}
