package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Represent an HPO Term together with a Frequency and an Onset and modifiers. This is intended to
 * be used to represent a disease annotation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class HpoDiseaseAnnotationDefault implements HpoDiseaseAnnotation {

  // TODO - implement real comparator
  private static final Comparator<HpoDiseaseAnnotation> COMPARATOR = Comparator.comparing(HpoDiseaseAnnotation::id);

  /** The annotated {@link TermId}. */
  private final TermId termId;

  private final Collection<HpoDiseaseAnnotationMetadata> metadata;

  @Override
  public TermId id() {
    return termId;
  }

  static HpoDiseaseAnnotationDefault of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    return new HpoDiseaseAnnotationDefault(termId, metadata);
  }

  private HpoDiseaseAnnotationDefault(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    this.termId = Objects.requireNonNull(termId, "Term ID must not be null");
    this.metadata = Objects.requireNonNull(metadata, "Metadata must not be null");
  }

  @Override
  public Stream<HpoDiseaseAnnotationMetadata> metadata() {
    return metadata.stream();
  }

  @Override
  public int compareTo(HpoDiseaseAnnotation other) {
    return COMPARATOR.compare(this, other);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationDefault that = (HpoDiseaseAnnotationDefault) o;
    return Objects.equals(termId, that.termId) && Objects.equals(metadata, that.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(termId, metadata);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationDefault{" +
      "termId=" + termId +
      ", metadata=" + metadata +
      '}';
  }

}
