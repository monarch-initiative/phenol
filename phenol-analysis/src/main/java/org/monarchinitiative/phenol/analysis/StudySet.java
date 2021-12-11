package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.*;

/**
 * This class holds all gene names of a study and their associated
 * (optional) descriptions. The names are extracted directly by the
 * constructor given study file. The class takes a list of genes in the constructor; the list
 * can be for the population set or the study set. It stores these genes in {@link #geneSet}, and
 * creates a map for each Gene Ontology term used to directly or indirectly annotate the genes; each
 * gene is thereby associated with a list of all of the genes that annotate the terms.
 *
 * @author Peter Robinson, Sebastian Bauer
 */
public class StudySet implements ItemSet {
  /**
   * Key: an Ontology id (usually GO or HP); value: a {@link DirectAndIndirectTermAnnotations} object with the items that the
   * ontology term annotates.
   */
  private final Map<TermId, DirectAndIndirectTermAnnotations> annotationMap;
  /** List containing genes, diseases or other items of this set. These are the items that
   * have been annotated with the terms of an ontology such as GO or HPO. */
  private final Set<TermId> geneSet;
  /** The name of the study set. */
  private final String name;

  /** A list of gene symbols for which we could not identify the TermIds. This is used when we create a study set
   * from gene symbols rather than Gene Ids in order to give the user feedback about symbols that couldn't be mapped.
   */
  private final Set<String> unmappedGeneSymbols;

  /**
   * @param genes                The genes (or other items) that make up this set.
   * @param name                 The name of this set, e.g., study1 or population
   * @param associationContainer Container of all Gene Ontology associations for all annotated genes
   */
  public StudySet(Set<TermId> genes,
                  String name,
                  Map<TermId, DirectAndIndirectTermAnnotations> associationContainer) {
    this.geneSet = genes;
    this.name = name;
    this.annotationMap = associationContainer;
    this.unmappedGeneSymbols = new HashSet<>(); // empty set
  }

  /**
   * @param genes                The genes (or other items) that make up this set.
   * @param name                 The name of this set, e.g., study1 or population
   * @param associationContainer Container of all Gene Ontology associations for all annotated genes
   */
  public StudySet(Set<TermId> genes,
                  String name,
                  Map<TermId, DirectAndIndirectTermAnnotations> associationContainer,
                  Set<String> unmappedGenes) {
    this.geneSet = genes;
    this.name = name;
    this.annotationMap = associationContainer;
    this.unmappedGeneSymbols = unmappedGenes;
  }

  /**
   * @return a map with key: a GO id; value: an object with all annotated gene ids.
   */
  public Map<TermId, DirectAndIndirectTermAnnotations> getAnnotationMap() {
    return annotationMap;
  }

  /**
   * @return set of all genes in this StudySet.
   */
  public Set<TermId> getGeneSet() {
    return this.geneSet;
  }

  public int getUnmappedGeneSymbolCount() { return this.unmappedGeneSymbols.size(); }

  public List<String> getSortedUnmappedGeneSymbols() {
    List<String> unmapped = new ArrayList<>(this.unmappedGeneSymbols);
    Collections.sort(unmapped);
    return unmapped;
  }

  public Set<String> getUnmappedGeneSymbolSet() {
    return this.unmappedGeneSymbols;
  }

  /**
   * @return set of all GO/HP ids used to annotate the genes in this study set.
   */
  public Set<TermId> getOntologyTermIds() {
    return this.annotationMap.keySet();
  }

  /**
   * return the genes annotating any gene in this study set.
   *
   * @param goId a GO or HP id
   * @return TermAnnotations object with all of the items (genes or diseases) that annotate to goId
   */
  public DirectAndIndirectTermAnnotations getAnnotatedGenes(TermId goId) {
    return this.annotationMap.get(goId);
  }


  /**
   * @param tid a GO or HP id
   * @return the number of items directly annotated to this ontology term.
   */
  public int getDirectAnnotationCount(TermId tid) {
    if (this.annotationMap.containsKey(tid)) {
      return this.annotationMap.get(tid).directAnnotatedCount();
    } else {
      return 0;
    }
  }

  /**
   * @param tid a GO or HP id
   * @return the number of items directly or indirectly annotated to this ontology term.
   */
  public int getTotalAnnotationCount(TermId tid) {
    if (this.annotationMap.containsKey(tid)) {
      return this.annotationMap.get(tid).totalAnnotatedCount();
    } else {
      return 0;
    }
  }


  /**
   * @return the desired count of the number of genes or gene products within this studyset..
   */
  public int getAnnotatedItemCount() {
    return geneSet.size();
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
    return geneSet.contains(geneName);
  }


  static public StudySet populationSet(Set<TermId> genes,
                                       Map<TermId, DirectAndIndirectTermAnnotations> associationContainer) {
    return new StudySet(genes, "population", associationContainer);

  }



}
