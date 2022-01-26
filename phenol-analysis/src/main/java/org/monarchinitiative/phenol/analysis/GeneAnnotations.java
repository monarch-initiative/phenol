package org.monarchinitiative.phenol.analysis;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;

/**
 * This class contains an annotated gene (represented as a {@link TermId}) as well as
 * the explicit (direct) Ontology Terms that annotate the gene.
 */
public class GeneAnnotations implements ItemAnnotations<TermId> {

    /** TermId of the item (e.g., gene) for which this object stores 0 - n Associations (e.g., GO associations). */
    private final TermId annotatedGene;

    /** List of annotations (associations of the annotatedItem with Ontology Terms. */
    private final List<TermAnnotation> annotations;

  public static GeneAnnotations of(TermId annotatedGene, List<TermAnnotation> annotations) {
    return new GeneAnnotations(annotatedGene, annotations);
  }

    /**
     * @param annotatedGene name of the gene or other item being annotated
     * @param annotations Gene Ontology annotations for the annotatedGene
     */
    private GeneAnnotations(TermId annotatedGene, List<TermAnnotation> annotations) {
      this.annotatedGene = Objects.requireNonNull(annotatedGene, "Annotated gene term ID must not be null");
      this.annotations = Objects.requireNonNull(annotations, "Annotations must not be null");
    }

    @Override
    public TermId annotatedItem() {
        return annotatedGene;
    }

    @Override
    public List<TermAnnotation> getAnnotations() {
        return this.annotations;
    }

  @Override
  public int getAnnotationCount() {
    return this.annotations.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneAnnotations that = (GeneAnnotations) o;
    return Objects.equals(annotatedGene, that.annotatedGene) && Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotatedGene, annotations);
  }

  @Override
  public String toString() {
    return "GeneAnnotations{" +
      "annotatedGene=" + annotatedGene +
      ", annotations=" + annotations +
      '}';
  }
}
