package org.monarchinitiative.phenol.analysis;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.annotations.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class GoAssociationContainer implements AssociationContainer<TermId> {
  private final Logger LOGGER = LoggerFactory.getLogger(GoAssociationContainer.class);
  /**
   * Fake root added for GO that we do not want to add to the associations.
   */
  private final static TermId fakeRoot = TermId.of("owl:Thing");

  private final List<GoGaf21Annotation> rawAssociations;
  /**
   * Key -- TermId for a gene. Value: {@link ItemAssociations} object with GO annotations for the gene.
   */
  private final Map<TermId, ItemAssociations> gene2associationMap;
  /**
   * Gene Ontology object.
   */
  private final Ontology ontology;

  /**
   * Constructs the container using a list of TermAnnotations (for instance, a
   * TermAnnotation can be one line of the GO GAF file).
   *
   * @param assocs gene ontology associations (annotations)
   */
  private GoAssociationContainer(List<GoGaf21Annotation> assocs, Ontology ontology) {
    rawAssociations = assocs;
    this.ontology = ontology;
    Map<TermId, ItemAssociations> tempMap = new HashMap<>();
    for (TermAnnotation a : assocs) {
      TermId tid = a.getLabel();
      if (tid.equals(fakeRoot)) {
        continue; // skip owl:Thing
      }
      tempMap.putIfAbsent(tid, new ItemAssociations(tid));
      tempMap.get(tid).add(a);
    }
    this.gene2associationMap = ImmutableMap.copyOf(tempMap);
    Set<TermId> tidset = new HashSet<>();
    for (ItemAssociations a : this.gene2associationMap.values()) {
      List<TermId> tidlist = a.getAssociations();
      tidset.addAll(tidlist);
    }
   // this.annotatingTermCount = tidset.size();
  }

  @Override
  public Map<TermId, List<TermId>> getOntologyTermToDomainItemsMap() {
    Map<TermId, List<TermId>> mp = new HashMap<>();
    for (Map.Entry<TermId, ItemAssociations> entry : gene2associationMap.entrySet()) {
      TermId gene = entry.getKey();
      mp.putIfAbsent(gene, new ArrayList<>());
      for (TermId ontologyTermId : entry.getValue().getAssociations()) {
        mp.get(ontologyTermId).add(gene);
      }
    }
    return mp;
  }

  public List<GoGaf21Annotation> getRawAssociations() {
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
      ItemAssociations assocs = this.gene2associationMap.get(domainTermId);
      for (TermAnnotation termAnnotation : assocs) {
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
        directAnnotationMap.putIfAbsent(domainTermId, new HashSet<>());
        directAnnotationMap.get(domainTermId).add(ontologyTermId);
      }
    }
    if (not_found > 0) {
      LOGGER.warn("Cound not find annotations for {} ontology term ids (are versions in synch?)", not_found);
    }
    Map<TermId, DirectAndIndirectTermAnnotations> annotationMap = new HashMap<>();
    for (Map.Entry<TermId, Set<TermId>> e : directAnnotationMap.entrySet()) {
      TermId itemTermId = e.getKey();
      DirectAndIndirectTermAnnotations daiAnnots =
        new DirectAndIndirectTermAnnotations(e.getValue(), ontology);
    }
    return annotationMap;
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
    for (GoGaf21Annotation annot : this.rawAssociations) {
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
  public static GoAssociationContainer loadGoGafAssociationContainer(File goGafFile, Ontology ontology) {
    List<GoGaf21Annotation> goAnnots = GoGeneAnnotationParser.loadAnnotations(goGafFile);
    return GoAssociationContainer.fromGoTermAnnotations(goAnnots, ontology);
  }

  /**
   * Create and return an {@link TermAssociationContainer} object from a Gene Ontology goa_human.gaf annotation file
   *
   * @param goGafFile File object representing the GO annotation file
   * @return An {@link TermAssociationContainer} object representing GO associations
   */
  public static GoAssociationContainer loadGoGafAssociationContainer(String goGafFile, Ontology ontology) {
    return loadGoGafAssociationContainer(new File(goGafFile), ontology);
  }

  /**
   * Create an AssociationContainer from list of {@link TermAnnotation} objects representing the data in a Gene
   * Ontology annotation file, e.g., human_goa.gaf
   *
   * @param goAnnots List of ontology term annotations
   * @return an AssociationContainer
   */
  public static GoAssociationContainer fromGoTermAnnotations(List<GoGaf21Annotation> goAnnots, Ontology ontology) {
    return new GoAssociationContainer(goAnnots, ontology);
  }


}
