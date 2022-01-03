package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This class is used to store the numbers we need to calculate a P Value for individual GO Terms
 * using the Term for Term approach and also to store the numbers of study and population genes
 * that were annotated to the term
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */

public class GoTerm2PValAndCounts extends PValue {

  private final int annotatedStudyGenes;
  private final int annotatedPopulationGenes;
  private final int totalStudyGenes;
  private final int totalPopulationGenes;

  public GoTerm2PValAndCounts(TermId goId, double raw_pval, int annotatedStudyGenes, int totalStudyGenes,
                              int annotatedPopulationGenes, int totalPopulationGenes){
    super(goId,raw_pval);
    this.annotatedStudyGenes = annotatedStudyGenes;
    this.totalStudyGenes = totalStudyGenes;
    this.annotatedPopulationGenes = annotatedPopulationGenes;
    this.totalPopulationGenes = totalPopulationGenes;
  }

  public int getAnnotatedStudyGenes() {
    return annotatedStudyGenes;
  }

  public int getTotalStudyGenes() { return totalStudyGenes; }

  public int getAnnotatedPopulationGenes() {
    return annotatedPopulationGenes;
  }

  public int getTotalPopulationGenes() { return totalPopulationGenes; }
  /** GetItem from the superclass returns a Go ID for this class. Add a convenience function for clarity. */
  public TermId getGoTermId() { return getItem(); }


  /**
   * A convenience method to get an array of fields that can be used to display the results
   * The fields are identical to those in {@link #header()}.
   * @param ontology reference to Gene (or other) Ontology
   * @return array of fields with data about this
   */
  public  String[] getRowData(Ontology ontology) {
    String label = ontology.getTermMap().get(this.item).getName();
    String study = String.format("%d/%d(%.1f%%)",
      this.annotatedStudyGenes, this.totalStudyGenes,
      100.0*(double)this.annotatedStudyGenes/this.totalStudyGenes);
    String population = String.format("%d/%d(%.1f%%)",
      this.annotatedPopulationGenes, this.totalPopulationGenes,
      100.0*(double)this.annotatedPopulationGenes/this.totalPopulationGenes);

    return new String[]{label,
      this.item.getValue(),
      study,
      population,
      String.format("%.2e", this.p_raw),
      String.format("%.2e", this.p_adjusted)
    };
  }

  public boolean passesThreshold(double alphaThreshold) {
    return (this.p_adjusted <= alphaThreshold);
  }

  public  String getRow(Ontology ontology) {
    return String.join("\t", getRowData(ontology));
  }


  public static String header() {
    String [] fields = {"id", "study", "population", "p.val", "adj.p.val"};
    return String.join("\t", fields);
  }

  @Override
  public String toString() {
    return String.format("%s: study: %d; population: %d; p.val: %e; adj.p.val: %e",
      this.item.getValue(),
      this.annotatedStudyGenes,
      this.annotatedPopulationGenes,
      this.p_raw,
      this.p_adjusted);
  }

}
