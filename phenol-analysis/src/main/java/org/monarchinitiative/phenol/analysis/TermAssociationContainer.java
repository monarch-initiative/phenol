package org.monarchinitiative.phenol.analysis;


import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.annotations.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
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
  private final List<TermAnnotation> rawAssociations;
  /**
   * Key -- TermId for a gene. Value: {@link ItemAnnotations} object with GO annotations for the gene.
   */
  private final Map<TermId, GeneAnnotations> gene2associationMap;
  /**
   * The total number of GO (or HP, MP, etc) terms that are annotating the items in this container.
   */
  private final int annotatingTermCount;

  private final Ontology ontology;


  /**
   * Constructs the container using a list of TermAnnotations (for instance, a
   * TermAnnotation can be one line of the GO GAF file).
   *
   * @param assocs gene ontology associations (annotations)
   */
  private TermAssociationContainer(List<TermAnnotation> assocs, Ontology ontology) {
    this.rawAssociations = assocs;
    this.ontology = ontology;
    Map<TermId, GeneAnnotations> tempMap = new HashMap<>();
    for (TermAnnotation a : assocs) {
      TermId tid = a.getLabel();
      tempMap.putIfAbsent(tid, new GeneAnnotations(tid));
      GeneAnnotations g2a = tempMap.get(tid);
      g2a.addAnnotation(a);
    }
    this.gene2associationMap = ImmutableMap.copyOf(tempMap);
    Set<TermId> tidset = new HashSet<>();
    for (GeneAnnotations a : this.gene2associationMap.values()) {
      List<TermId> tidlist = a.getAnnotatingTermIds();
      tidset.addAll(tidlist);
    }
    this.annotatingTermCount = tidset.size();
  }

  @Override
  public Map<TermId, List<TermId>> getOntologyTermToDomainItemsMap() {
    Map<TermId, List<TermId>> mp = new HashMap<>();
    for (Map.Entry<TermId, GeneAnnotations> entry : gene2associationMap.entrySet()) {
      TermId gene = entry.getKey();
      mp.putIfAbsent(gene, new ArrayList<>());
      for (TermId ontologyTermId : entry.getValue().getAnnotatingTermIds()) {
        mp.get(ontologyTermId).add(gene);
      }
    }
    return mp;
  }

  public int getAnnotatingTermCount() {
    return this.annotatingTermCount;
  }

  public List<TermAnnotation> getRawAssociations() {
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
      annotationMap.put(itemTermId, daiAnnots);
    }
    return annotationMap;
  }



  /**
   * Create an AssociationContainer from list of {@link TermAnnotation} objects representing the data in a Gene
   * Ontology annotation file, e.g., human_goa.gaf
   *
   * @param goAnnots List of ontology term annotations
   * @return an AssociationContainer
   */
  public static TermAssociationContainer fromGoTermAnnotations(List<TermAnnotation> goAnnots, Ontology ontology) {
    return new TermAssociationContainer(goAnnots, ontology);
  }

  /**
   * Create and return an {@link TermAssociationContainer} object from a Gene Ontology goa_human.gaf annotation file
   *
   * @param goGafFile File object representing the GO annotation file
   * @return An {@link TermAssociationContainer} object representing GO associations
   */
  public static TermAssociationContainer loadGoGafAssociationContainer(File goGafFile, Ontology ontology) {
    List<TermAnnotation> goAnnots = GoGeneAnnotationParser.loadTermAnnotations(goGafFile);
    return TermAssociationContainer.fromGoTermAnnotations(goAnnots, ontology);
  }

  /**
   * Create and return an {@link TermAssociationContainer} object from a Gene Ontology goa_human.gaf annotation file
   *
   * @param goGafPath Path to the GO annotation file
   * @return An {@link TermAssociationContainer} object representing GO associations
   */
  public static TermAssociationContainer loadGoGafAssociationContainer(String goGafPath, Ontology ontology) {
    return loadGoGafAssociationContainer(new File(goGafPath), ontology);
  }

}
