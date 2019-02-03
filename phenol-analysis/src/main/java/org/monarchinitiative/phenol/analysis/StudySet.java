package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

/**
 * This class holds all gene names of a study and their associated
 * (optional) descriptions. The names are extracted directly by the
 * constructor given study file. The class takes a list of genes in the constructor; the list
 * can be for the population set or the study set. It stores these genes in {@link #annotatedItemTermIds}, and
 * creates a map for each Gene Ontology term used to directly or indirectly annotate the genes; each
 * gene is thereby associated with a list of all of the genes that annotate the terms.
 *
 * @author Peter Robinson, Sebastian Bauer
 */
public class StudySet {
  /**
   * Key: an Ontology id (usually GO or HP); value: a {@link TermAnnotations} object with the items that the
   * ontology term annotates.
   */
  private Map<TermId, TermAnnotations> annotationMap;
  /**
   * List containing genes, diseases or other items of this set. These are the items that have been annotated
   * with the terms of an ontology such as GO or HPO.
   */
  private final Set<TermId> annotatedItemTermIds;
  /**
   * The name of the study set
   */
  private final String name;

  /**
   * @param genes                The genes (or other items) that make up this set.
   * @param name                 The name of this set, e.g., study1 or population
   * @param associationContainer Container of all Gene Ontology associations for all annotated genes
   * @param ontology             reference to the Gene Ontology object
   */
  public StudySet(Set<TermId> genes, String name, AssociationContainer associationContainer, Ontology ontology) {
    this.annotatedItemTermIds = genes;
    this.name = name;
    initAssociationMap(associationContainer, ontology);
  }

  /**
   * @return a map with key: a GO id; value: an object with all annotated gene ids.
   */
  public Map<TermId, TermAnnotations> getAnnotationMap() {
    return annotationMap;
  }

  /**
   * @return set of all genes in this StudySet.
   */
  public Set<TermId> getAnnotatedItemTermIds() {
    return this.annotationMap.keySet();
  }

  /**
   * @return set of all GO/HP ids used to annotate the genes in this study set.
   */
  public Set<TermId> getAnnotatingTermIds() {
    return this.annotationMap.keySet();
  }

  /**
   * return the genes annotating any gene in this study set.
   *
   * @param goId a GO or HP id
   * @return TermAnnotations object with all of the items (genes or diseases) that annotate to goId
   */
  public TermAnnotations getAnnotatedGenes(TermId goId) {
    return this.annotationMap.get(goId);
  }

  private void initAssociationMap(AssociationContainer associationContainer, Ontology ontology) {
    this.annotationMap = new HashMap<>();
    for (TermId domainTermId : this.annotatedItemTermIds) {
      try {
        ItemAssociations assocs = associationContainer.get(domainTermId);
        for (TermAnnotation termAnnotation : assocs) {
          /* At first add the direct counts and remember the terms */
          TermId ontologyTermId = termAnnotation.getTermId();
          // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
          Term term = ontology.getTermMap().get(ontologyTermId);
          if (term == null) {
            System.err.println("Unable to retrieve term for id=" + ontologyTermId.getValue());
            continue;
          }
          // replace an alt_id with the primary id.
          // if we already have the primary id, nothing is changed.
          TermId primaryGoId = term.getId();
          annotationMap.putIfAbsent(ontologyTermId, new TermAnnotations());
          TermAnnotations termAnnots = annotationMap.get(primaryGoId);
          termAnnots.addGeneAnnotationDirect(domainTermId);
          // In addition to the direct annotation, the gene is also indirectly annotated to all of the
          // GO Term's ancestors
          Set<TermId> ancs = getAncestorTerms(ontology, primaryGoId, true);
          for (TermId ancestor : ancs) {
            annotationMap.putIfAbsent(ancestor, new TermAnnotations());
            TermAnnotations termAncAnnots = annotationMap.get(ancestor);
            termAncAnnots.addGeneAnnotationTotal(domainTermId);
          }
        }
      } catch (PhenolException e) {
        e.printStackTrace();// TODO are there exceptions
      }
    }
  }


  /**
   * @return the desired count of the number of genes or gene products within this studyset..
   */
  public int getAnnotatedItemCount() {
    return annotatedItemTermIds.size();
  }

  /**
   * @return name of the StudySet
   */
  public String getName() {
    return name;
  }

  /* for debugging */
  public String toString() {
    return name + " (n=" + (getAnnotatedItemCount()) + ")";
  }


  /**
   * Checks whether study set contains the given gene.
   *
   * @param geneName the name of the gene whose set inclusion should be checked.
   * @return true if study contains gene, else false.
   */
  public boolean contains(TermId geneName) {
    return annotatedItemTermIds.contains(geneName);
  }


}
