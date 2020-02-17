package org.monarchinitiative.phenol.analysis;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;


/**
 * After AssociationParser was used to parse the gene_association.XXX file, this
 * class is used to store and process the information about Associations.
 * @author Peter Robinson, Sebastian Bauer
 */
public class AssociationContainer {
    /** Key -- TermId for a gene. Value: {@link ItemAssociations} object with GO annotations for the gene. */
    private final Map<TermId, ItemAssociations> gene2associationMap;
    /** The total number of GO (or HP, MP, etc) terms that are annotating the items in this container.
     * This variable is initialzed only if needed. The getter first checks if it is null, and if so
     * calculates the required count. */
    private Integer annotatingTermCount = null;


    /**
     * Constructs the container using a list of TermAnnitations (for instance, a
     * TermAnnotation can be one line of the GO GAF file).
     *
     * @param assocs gene ontology associations (annotations)
     */
    public AssociationContainer(List<TermAnnotation> assocs) {
        gene2associationMap=new HashMap<>();
        for (TermAnnotation a : assocs) {
          TermId tid = a.getLabel();
          this.gene2associationMap.putIfAbsent(tid,new ItemAssociations(tid));
          ItemAssociations g2a = gene2associationMap.get(tid);
          g2a.add(a);
        }
    }

    public Multimap<TermId, TermId> getTermToItemMultimap() {
      Multimap<TermId, TermId> mp = ArrayListMultimap.create();
      //Map<TermId, ItemAssociations> gene2associationMap;
      for (Map.Entry<TermId, ItemAssociations> entry : gene2associationMap.entrySet()) {
        TermId gene = entry.getKey();
        for (TermId ontologyTermId : entry.getValue().getAssociations()) {
          mp.put(ontologyTermId,gene);
        }
      }
      return mp;
    }

  /**
   * Calculates the number of unique ontology terms (e.g., GO, HP) used to annotated the
   * items in this container
   * @return total GO/HP term count
   */
  public int getOntologyTermCount() {
      if (this.annotatingTermCount == null) {
        Set<TermId> tidset = new HashSet<>();
        for (ItemAssociations a : this.gene2associationMap.values()) {
          List<TermId> tidlist = a.getAssociations();
          tidset.addAll(tidlist);
        }
        this.annotatingTermCount = tidset.size();
      }
      return this.annotatingTermCount;
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
    public ItemAssociations get(TermId dbObjectId) throws PhenolException {
       if (! this.gene2associationMap.containsKey(dbObjectId)) {
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
    public Set<TermId> getAllAnnotatedGenes()
    {
        return this.gene2associationMap.keySet();
    }


    public int getTotalNumberOfAnnotatedTerms(){
        return gene2associationMap.size();
    }



  public Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds, Ontology ontology) {
    Map<TermId, DirectAndIndirectTermAnnotations> annotationMap = new HashMap<>();
    for (TermId domainTermId : annotatedItemTermIds) {
      try {
        ItemAssociations assocs = get(domainTermId);
        for (TermAnnotation termAnnotation : assocs) {
          /* At first add the direct counts and remember the terms */
          TermId ontologyTermId = termAnnotation.getTermId();
          // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
          Term term = ontology.getTermMap().get(ontologyTermId);
          if (term == null) {
            System.err.println("[WARNING(phenol:AssociationContainer)] Unable to retrieve term "
              + ontologyTermId.getValue() + ", omitting.");
            continue;
          }
          // replace an alt_id with the primary id.
          // if we already have the primary id, nothing is changed.
          TermId primaryGoId = term.getId();
          annotationMap.putIfAbsent(primaryGoId, new DirectAndIndirectTermAnnotations());
          DirectAndIndirectTermAnnotations termAnnots = annotationMap.get(primaryGoId);
          termAnnots.addGeneAnnotationDirect(domainTermId);
          // In addition to the direct annotation, the gene is also indirectly annotated to all of the
          // GO Term's ancestors
          Set<TermId> ancs = getAncestorTerms(ontology, primaryGoId, true);
          for (TermId ancestor : ancs) {
            annotationMap.putIfAbsent(ancestor, new DirectAndIndirectTermAnnotations());
            DirectAndIndirectTermAnnotations termAncAnnots = annotationMap.get(ancestor);
            termAncAnnots.addGeneAnnotationTotal(domainTermId);
          }
        }
      } catch (PhenolException e) {
        System.err.println("[ERROR (StudySet.java)] " + e.getMessage());
      }
    }
    return annotationMap;
  }

}
