package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.analysis.util.Util;
import org.monarchinitiative.phenol.annotations.io.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


/**
 * After AssociationParser was used to parse the gene_association.XXX file, this
 * class is used to store and process the information about Associations.
 *
 * @author Peter Robinson, Sebastian Bauer
 */
public class TermAssociationContainer implements AssociationContainer<TermId> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TermAssociationContainer.class);

  private final List<? extends TermAnnotation> rawAssociations;
  /**
   * Key -- TermId for a gene. Value: {@link ItemAnnotations} object with GO annotations for the gene.
   */
  private final Map<TermId, GeneAnnotations> gene2associationMap;
  /**
   * The total number of GO (or HP, MP, etc) terms that are annotating the items in this container.
   */
  private final int annotatingTermCount;

  private final MinimalOntology ontology;


  /**
   * Constructs the container using a list of TermAnnotations (for instance, a
   * TermAnnotation can be one line of the GO GAF file).
   * @param ontology Ontology object
   * @param rawAssociations each entry represents one item to Ontology term annotation
   * @param gene2associationMap key - domain object (e.g., gene), value, list of GO annotations
   * @param annotatingTermCount number of annotating terms TODO DO WE NEED THIS?
   */
  private TermAssociationContainer(MinimalOntology ontology,
                                  List<? extends TermAnnotation> rawAssociations,
                                  Map<TermId, GeneAnnotations> gene2associationMap,
                                  int annotatingTermCount) {
    this.ontology = ontology;
    this.rawAssociations = rawAssociations;
    this.gene2associationMap = gene2associationMap;
    this.annotatingTermCount = annotatingTermCount;
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
    return Map.copyOf(mp);
  }

  public int getAnnotatingTermCount() {
    return this.annotatingTermCount;
  }

  @Override
  public int getTotalAnnotationCount() {
    return rawAssociations.size();
  }

  public List<? extends TermAnnotation> getRawAssociations() {
    return rawAssociations;
  }

  /**
   * get a ItemAssociations object corresponding to a given gene name. If the
   * name is not initially found as dbObject Symbol, (which is usually a
   * database name with meaning to a biologist), try dbObject (which may be an
   * accession number or some other term from the bla32 database), and
   * finally, look for a synonym (another entry in the gene_association file
   * that will have been parsed into the present object).
   *
   * @param dbObjectId id (e.g., MGI:12345) of the gene whose goAssociations are interesting
   * @return goAssociations for the given gene
   */

  public GeneAnnotations get(TermId dbObjectId) throws PhenolException {
    if (!this.gene2associationMap.containsKey(dbObjectId)) {
      throw new PhenolException("Could not find annotations for " + dbObjectId.getValue());
    } else {
      return this.gene2associationMap.get(dbObjectId);
    }
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

  @Override
  public Set<TermId> getDomainItemsAnnotatedByOntologyTerm(TermId tid) {
    return Util.getDomainItemsAnnotatedByOntologyTerm(tid, ontology, gene2associationMap);
  }

  /**
   * @return number of genes (or items) represented in this association container.
   */
  public int getTotalNumberOfAnnotatedItems() {
    return gene2associationMap.size();
  }

  /*
  Map<TermId, DirectAndIndirectTermAnnotations> annotationMap = new HashMap<>();


      } catch (PhenolException e) {
        System.err.println("[ERROR (StudySet.java)] " + e.getMessage());
        }
        }
        if (not_found > 0) {
        System.err.printf("[WARNING (AssociationContainer)] Cound not find annotations for %d ontology term ids" +
        " (are versions of the GAF and obo file compatible?).\n", not_found);
        }
        return annotationMap;
   */


  /**
   *
   * @param annotatedItemTermIds set of gene (on in general) item TermIds that belong to a {@link StudySet},
   *                             and for which we want to do GO analysis
   *
   * @return Map with key: gene ontology id, value, {@link DirectAndIndirectTermAnnotations} object with domain ids
   */
  @Override
  public Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds) {
   // 1. Get all of the direct GO annotations to the genes. Key: domain item; value: annotating Ontlogy terms
    Map<TermId, Set<TermId>> directAnnotationMap = new HashMap<>();
    int not_found = 0;
    for (TermId domainTermId : annotatedItemTermIds) {
      if (!this.gene2associationMap.containsKey(domainTermId)) {
        LOGGER.error("Could not find annotations for  {}", domainTermId.getValue());
        continue;
      }
      GeneAnnotations assocs = this.gene2associationMap.get(domainTermId);
      for (TermAnnotation termAnnotation : assocs.getAnnotations()) {
        /* In this step add the direct annotations only */
        TermId ontologyTermId = termAnnotation.id();
        // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
        Optional<TermId> primary = ontology.termForTermId(ontologyTermId).map(Term::id);
        if (primary.isEmpty()) {
          not_found++;
          LOGGER.warn("Unable to retrieve ontology term {} (omitted).", ontologyTermId.getValue());
          continue;
        }
        // if necessary, replace with the latest primary term id
        ontologyTermId = primary.get();
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
        for (TermId ancestor : ontology.graph().getAncestors(ontologyId, false)) {
          annotationMap.putIfAbsent(ancestor, new DirectAndIndirectTermAnnotations(ancestor));
          annotationMap.get(ancestor).addIndirectAnnotatedItem(domainItemTermId);
        }
      }
    }
    return Map.copyOf(annotationMap);
  }



  /**
   * Create an AssociationContainer from list of {@link TermAnnotation} objects representing the data in a Gene
   * Ontology annotation file, e.g., human_goa.gaf
   *
   * @param goAnnots List of ontology term annotations
   * @param ontology {@link Ontology} object
   * @return an AssociationContainer
   */
  public static TermAssociationContainer fromGoTermAnnotations(List<? extends TermAnnotation> goAnnots,
                                                               MinimalOntology ontology) {
    // key: a gene id; value: set of direct GO annotations
    Map<TermId, Set<TermAnnotation>> domainItemToAnnotationMap = new HashMap<>();
    for (TermAnnotation annot : goAnnots) {
      // a TermAnnotation is a simple object with (mainly) the itemId and the Ontology Id
      TermId itemId = annot.getItemId();
      domainItemToAnnotationMap.computeIfAbsent(itemId, k -> new HashSet<>()).add(annot);
    }
    // Key: a gene id; value: GeneAnnotations object with direct GO annotations
    Map<TermId, GeneAnnotations> gene2associationMap = new HashMap<>();
    domainItemToAnnotationMap.forEach((k, v) ->
      gene2associationMap.put(k, GeneAnnotations.of(k, List.copyOf(v))));

    long count = gene2associationMap.values().stream()
      .map(ItemAnnotations::getAnnotatingTermIds)
      .distinct()
      .count();

    return new TermAssociationContainer(ontology, goAnnots, gene2associationMap, Math.toIntExact(count));
  }

  /**
   * Create and return an {@link TermAssociationContainer} object from a Gene Ontology goa_human.gaf annotation file
   *
   * @param goGafFile File object representing the GO annotation file
   * @return An {@link TermAssociationContainer} object representing GO associations
   */
  public static TermAssociationContainer loadGoGafAssociationContainer(File goGafFile, Ontology ontology) {
    List<? extends TermAnnotation> goAnnots = GoGeneAnnotationParser.loadAnnotations(goGafFile.toPath());
    return TermAssociationContainer.fromGoTermAnnotations(goAnnots, ontology);
  }

}
