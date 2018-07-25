package org.monarchinitiative.phenol.formats.hpo.category;


import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class represents a category of HPO terms that we would like to display or treat as a group. Roughly, it
 * corresponds to the major organ abnormality categories, but it allows subcategories to be added, for instance,
 * gastrointestinal can have the subcategory liver.</p>
 * <p>The main use case for this class is to display a set of Terms that descend from this Category. The class
 * is intended to be used with HpoCategoryMap to display a set of terms that can belong to </p>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.13
 */
public class HpoCategory {
  /** The TermId of the HPO term that corresponds to this category. For example, the HPO Term Abnormality of the
   * voice (HP:0001608) would correspond to the category called "Voice". We display the stirng "Voice" in a browser (for instance)
   * when we show terms from the subontology that descends from Abnormality of the voice. */
  private final TermId tid;
  /** The display label for this category (e.g., "Voice"--see {@link #tid}). */
  private final String label;
  /** List of the HPO terms from the disease we want to display that are children of this category. */
  private final List<TermId> annotatedTerms;


  HpoCategory(TermId id, String labl) {
    tid=id;
    label=labl;
    annotatedTerms=new ArrayList<>();
  }

  /** @return true if at least one annotated term belongs to this category. */
  boolean hasAnnotation() { return annotatedTerms.size()>0;}
  /** @return Term ID of the current category. */
  public TermId getTid() {
    return tid;
  }
  /** @return Name of the current category. */
  public String getLabel() { return label; }
  /** @return List of terms in the current category that were used to annotate. */
  public List<TermId> getAnnotatingTermIds() { return annotatedTerms; }
  /** @return Number of terms in the current category that were used to annotate. */
  public int getNumberOfAnnotations() { return annotatedTerms.size(); }


  void addAnnotatedTerm(TermId tid){
        annotatedTerms.add(tid);
  }

    @Override
    public boolean equals(Object that) {
        if (that == null) return false;
        if (!(that instanceof HpoCategory)) return false;
        HpoCategory otherHpoCategory = (HpoCategory) that;

        return this.tid.equals(otherHpoCategory.tid);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + tid.hashCode();
        return result;
    }

}
