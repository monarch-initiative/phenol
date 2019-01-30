package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

/**
 * This class holds all gene names of a study and their associated
 * (optional) descriptions. The names are extracted directly by the
 * constructor given study file. The class takes a list of genes in the constructor; the list
 * can be for the population set or the study set. It stores these genes in {@link #geneIds}, and
 * creates a map for each Gene Ontology term used to directly or indirectly annotate the genes; each
 * gene is thereby associated with a list of all of the genes that annotate the terms.
 *
 * @author Peter Robinson, Sebastian Bauer
 */
public class StudySet {
  private final static Logger logger = Logger.getLogger(StudySet.class.getName());

  /**
   * AKey: a GO id; value: a {@link TermAnnotations} object with the gene annotations of the Go Term.
   */
  private Map<TermId, TermAnnotations> annotationMap;
  /**
   * List containing genes (or other items) of this set.
   */
  private final Set<TermId> geneIds;
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
    this.geneIds = genes;
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
  public Set<TermId> getAllGeneIds() {
    return this.annotationMap.keySet();
  }

  /**
   * @return set of all GO ids used to annotate the genes in this study set.
   */
  public Set<TermId> getAllAnnotatingTerms() {
    return this.annotationMap.keySet();
  }

  /**
   * return the genes annotating any gene in this study set.
   *
   * @param goId a GeneOntology id
   * @return TermAnnotations object with all of the genes that annotate to goId
   */
  public TermAnnotations getAnnotatedGenes(TermId goId) {
    return this.annotationMap.get(goId);
  }

  private void initAssociationMap(AssociationContainer associationContainer, Ontology ontology) {
    this.annotationMap = new HashMap<>();
    for (TermId geneId : this.geneIds) {
      try {
        //int idx = associationContainer.getIndex(geneId);
        ItemAssociations assocs = associationContainer.get(geneId);
        for (TermAnnotation goAnnotation : assocs) {
          /* At first add the direct counts and remember the terms */
          TermId goId = goAnnotation.getTermId();
          // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
          Term term = ontology.getTermMap().get(goId);
          if (term == null) {
            System.err.println("Unable to retrieve term for id=" + goId.getValue());
            continue;
          }
          // replace an alt_id with the primary id.
          // if we already have the primary id, nothing is changed.
          TermId primaryGoId = term.getId();
          annotationMap.putIfAbsent(goId, new TermAnnotations());
          TermAnnotations termAnnots = annotationMap.get(primaryGoId);
          termAnnots.addGeneAnnotationDirect(geneId);
          // In addition to the direct annotation, the gene is also indirectly annotated to all of the
          // GO Term's ancestors
          Set<TermId> ancs = getAncestorTerms(ontology, primaryGoId, true);
          for (TermId ancestor : ancs) {
            annotationMap.putIfAbsent(ancestor, new TermAnnotations());
            TermAnnotations termAncAnnots = annotationMap.get(ancestor);
            termAncAnnots.addGeneAnnotationTotal(geneId);
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
  public int getGeneCount() {
    return geneIds.size();
  }

  /**
   * @return name of the StudySet
   */
  public String getName() {
    return name;
  }

  /* for debugging */
  public String toString() {
    return name + " (n=" + (getGeneCount()) + ")";
  }


  /**
   * Checks whether study set contains the given gene.
   *
   * @param geneName the name of the gene whose set inclusion should be checked.
   * @return true if study contains gene, else false.
   */
  public boolean contains(TermId geneName) {
    return geneIds.contains(geneName);
  }


}
