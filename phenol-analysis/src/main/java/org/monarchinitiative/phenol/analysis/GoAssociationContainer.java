package org.monarchinitiative.phenol.analysis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf22Annotation;
import org.monarchinitiative.phenol.annotations.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

public class GoAssociationContainer implements AssociationContainer<TermId> {
  private final Logger LOGGER = LoggerFactory.getLogger(GoAssociationContainer.class);
  /**
   * Fake root added for GO that we do not want to add to the associations.
   */
  private final static TermId fakeRoot = TermId.of("owl:Thing");

  private final List<GoGaf22Annotation> rawAssociations;
  /**
   * Key -- TermId for a gene. Value: {@link ItemAnnotations} object with GO annotations for the gene.
   */
  private final Map<TermId, GeneAnnotations> gene2associationMap;
  /**
   * Gene Ontology object.
   */
  private final Ontology ontology;

  /**
   * total number of GO (or HP, MP, etc) terms that are annotating the items in this container.
   */
  private final int annotatingTermCount;

  /**
   * Constructs the container using a list of TermAnnotations (for instance, a TermAnnotation can be one line of the GO GAF file).
   *
   * @param rawAssociations gene ontology associations (annotations)
   */
  private GoAssociationContainer(Ontology ontology,
                                 List<GoGaf22Annotation> rawAssociations,
                                 Map<TermId, GeneAnnotations> gene2associationMap,
                                 int annotatingTermCount) {
    this.ontology = ontology;
    this.rawAssociations = rawAssociations;
    this.gene2associationMap = gene2associationMap;
    this.annotatingTermCount = annotatingTermCount;
  }

  public int getAnnotatingTermCount() {
    return this.annotatingTermCount;
  }

  @Override
  public Map<TermId, List<TermId>> getOntologyTermToDomainItemsMap() {
    Map<TermId, List<TermId>> mp = new HashMap<>();
    for (Map.Entry<TermId, GeneAnnotations> entry : gene2associationMap.entrySet()) {
      TermId gene = entry.getKey();
      for (TermId ontologyTermId : entry.getValue().getAnnotatingTermIds()) {
        mp.putIfAbsent(ontologyTermId, new ArrayList<>());
        mp.get(ontologyTermId).add(gene);
      }
    }
    return mp;
  }

  public List<GoGaf22Annotation> getRawAssociations() {
    return rawAssociations;
  }

  /**
   * * A way to get all annotated genes in the container
   *
   * @return The annotated genes as a Set
   */
  @Override
  public Set<TermId> getAllAnnotatedGenes() {
    return this.gene2associationMap.keySet();
  }

  /**
   * @return number of genes (or items) represented in this association container.
   */
  public int getTotalNumberOfAnnotatedItems() {
    return gene2associationMap.size();
  }

  @Override
  public Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds) {
    return getAssociationMap(annotatedItemTermIds, false);
  }


  public Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds,
                                                                          boolean verbose) {
    Map<TermId, Set<TermId>> directAnnotationMap = new HashMap<>();
    int not_found = 0;
    for (TermId domainTermId : annotatedItemTermIds) {
      if (!this.gene2associationMap.containsKey(domainTermId)) {
        LOGGER.error("Could not find annotations for  {}", domainTermId.getValue());
        continue;
      }
      GeneAnnotations assocs = this.gene2associationMap.get(domainTermId);
      for (TermAnnotation termAnnotation : assocs.getAnnotations()) {
        /* At first add the direct counts and remember the terms */
        TermId ontologyTermId = termAnnotation.getTermId();
        // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
        Term term = this.ontology.getTermMap().get(ontologyTermId);
        if (term == null) {
          not_found++;
          if (verbose) {
            LOGGER.warn("[WARNING(phenol:AssociationContainer)] Unable to retrieve term {} (omitted).",
              ontologyTermId.getValue());
          }
          continue;
        }
        // if necessary, replace with the latest primary term id
        ontologyTermId = this.ontology.getPrimaryTermId(ontologyTermId);
        directAnnotationMap.computeIfAbsent(domainTermId, k -> new HashSet<>()).add(ontologyTermId);
      }
    }
    if (not_found > 0) {
      LOGGER.warn("Cound not find annotations for {} ontology term ids (are versions in synch?)", not_found);
    }
    Map<TermId, DirectAndIndirectTermAnnotations> annotationMap = new HashMap<>();
    for (Map.Entry<TermId, Set<TermId>> entry : directAnnotationMap.entrySet()) {
      TermId domainItemTermId = entry.getKey();
      for (TermId ontologyId : entry.getValue()) {
        annotationMap.putIfAbsent(ontologyId, new DirectAndIndirectTermAnnotations(ontologyId));
        annotationMap.get(ontologyId).addDirectAnnotatedItem(domainItemTermId);
        // In addition to the direct annotation, the gene is also indirectly annotated
        // to all of the GO Term's ancestors
        Set<TermId> ancs = OntologyAlgorithm.getAncestorTerms(ontology, ontologyId, false);
        for (TermId ancestor : ancs) {
          annotationMap.putIfAbsent(ancestor, new DirectAndIndirectTermAnnotations(ancestor));
          annotationMap.get(ancestor).addIndirectAnnotatedItem(domainItemTermId);
        }
      }
    }
    return annotationMap; // TODO Java 11, Map.copyOf to make immutable
  }

  /**
   * The {@link StudySet} uses TermIds to represent genes/annotated objects, but often our input data
   * has gene symbols (as Strings). This function looks up the TermIds associated with gene symbols
   * in the GO GAF data and creates a StudySet
   *
   * @param geneSymbols Set of Gene symbols of a study set
   * @return corresponding study set
   */
  public StudySet fromGeneSymbols(Set<String> geneSymbols, String label) {
    Map<String, TermId> symbolToTermIdMap = new HashMap<>();
    for (GoGaf22Annotation annot : this.rawAssociations) {
      String symbol = annot.getDbObjectSymbol();
      TermId tid = annot.getDbObjectTermId();
      symbolToTermIdMap.putIfAbsent(symbol, tid);
    }
    Set<TermId> studyTermIds = new HashSet<>();
    Set<String> unmappableSymbols = new HashSet<>();
    for (String symbol : geneSymbols) {
      if (symbolToTermIdMap.containsKey(symbol)) {
        studyTermIds.add(symbolToTermIdMap.get(symbol));
      } else {
        unmappableSymbols.add(symbol);
      }
    }
    return new StudySet(label, getAssociationMap(studyTermIds), unmappableSymbols);
  }

  public StudySet fromGeneIds(Set<TermId> geneIds, String label) {
    return new StudySet(label, getAssociationMap(geneIds));
  }


  /**
   * Create and return an {@link TermAssociationContainer} object from a Gene Ontology goa_human.gaf annotation file
   *
   * @param goGafFile File object representing the GO annotation file
   * @return An {@link TermAssociationContainer} object representing GO associations
   */
  public static GoAssociationContainer loadGoGafAssociationContainer(Path goGafFile, Ontology ontology) {
    List<GoGaf22Annotation> goAnnots = GoGeneAnnotationParser.loadAnnotations(goGafFile);
    return fromGoTermAnnotations(goAnnots, ontology);
  }

  /**
   * Create an AssociationContainer from list of {@link TermAnnotation} objects representing the data in a Gene
   * Ontology annotation file, e.g., human_goa.gaf
   *
   * @param goTermAnnotations List of ontology term annotations
   * @return an AssociationContainer
   */
  public static GoAssociationContainer fromGoTermAnnotations(List<GoGaf22Annotation> goTermAnnotations, Ontology ontology) {
    Map<TermId, Set<TermAnnotation>> annotationsBuilder = new HashMap<>();

    for (TermAnnotation a : goTermAnnotations) {
      TermId tid = a.getItemId();
      if (tid.equals(fakeRoot)) {
        continue; // skip owl:Thing
      }
      annotationsBuilder.computeIfAbsent(tid, annotatedGene -> new HashSet<>())
        .add(a);
    }

    // TODO Use Native Java 11
    ImmutableMap.Builder<TermId, GeneAnnotations> tempMap = ImmutableMap.builder();
    annotationsBuilder.forEach((k, v) -> tempMap.put(k, GeneAnnotations.of(k, ImmutableList.copyOf(v))));
    ImmutableMap<TermId, GeneAnnotations> gene2associationMap = tempMap.build();

    long size = gene2associationMap.values().stream()
      .map(ItemAnnotations::getAnnotatingTermIds)
      .distinct()
      .count();

    return new GoAssociationContainer(ontology, goTermAnnotations, gene2associationMap, Math.toIntExact(size));
  }


}
