package org.monarchinitiative.phenol.analysis.mgsa;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

/**
 * This replaces the original CalculationUtils.java and uses Java8 collections.
 */

class TermToItemMatrix {

    private final int n_genes;
    private final int n_annotated_terms;
    /** The genes (usually) that are annotated to one or more GO term. The index is used in the {@link #annotatedItemToIndexMap} */
    private final List<TermId> annotatedItemList;
    /** List of Gene Ontology terms. The index is used in the {@link #goTermToIndexMap} */
    private final List<TermId> goTermList;
    /** The genes (usually) and their indices in {@link #annotatedItemList} */
    private final Map<TermId,Integer> annotatedItemToIndexMap;
    /** The GO terms and their indices in {@link #goTermList}. */
    private final Map<TermId,Integer> goTermToIndexMap;

    private final int [][] termLinks;


    TermToItemMatrix(AssociationContainer assocs) {
        Objects.requireNonNull(assocs);
        // termToGeneMultimap->key: A GO TermId; value: A Collection of Genes
        Multimap<TermId, TermId> termToGeneMultimap = assocs.getTermToItemMultimap();
        Set<TermId> genes = assocs.getAllAnnotatedGenes();
        n_genes = genes.size();
        //In Multimap, size() returns an actual number of values stored in a Map,
        // but keySet().size() returns the number of distinct keys.
        n_annotated_terms = termToGeneMultimap.keySet().size();
        // First get list and indices of the Terms
        int i = 0;
        ImmutableList.Builder<TermId> builder = new ImmutableList.Builder<>();
        ImmutableMap.Builder<TermId,Integer> mapBuilder = new ImmutableMap.Builder<>();
        for (TermId ontologyTermId : termToGeneMultimap.keySet()) {
          builder.add(ontologyTermId);
          mapBuilder.put(ontologyTermId, i);
          i++;
        }
        this.goTermList = builder.build();
        this.goTermToIndexMap = mapBuilder.build();
        // Now get list and indices of the genes
        builder = new ImmutableList.Builder<>();
        mapBuilder = new ImmutableMap.Builder<>();
        i = 0;
        for (TermId gene : genes) {
          builder.add(gene);
          mapBuilder.put(gene, i);
          i++;
        }
        this.annotatedItemList = builder.build();
        this.annotatedItemToIndexMap = mapBuilder.build();
        termLinks = new int[n_annotated_terms][];
        i = 0;
        for (TermId goTermId : goTermList) {
            Collection<TermId> annotatedsItems = termToGeneMultimap.get(goTermId);
            int goTermIndex = this.goTermToIndexMap.get(goTermId);
            termLinks[goTermIndex] = new int[annotatedsItems.size()];
            int j = 0;
            for (TermId geneIdx : annotatedsItems) {
              Integer idx = annotatedItemToIndexMap.get(geneIdx);
              if (idx == null || idx < 0) {
                // should never happen
                throw new PhenolRuntimeException("Could not find index of annotated item: " + geneIdx.getValue());
              }
              termLinks[goTermIndex][j] = idx;
              j++;
            }
          i++;
        }
    }

    public int getNumTerms() {
        return n_annotated_terms;
    }


    TermId getGoTermAtIndex(int i) {
        return this.goTermList.get(i);
    }

    public int getAnnotatedGeneCount(TermId goTermId) {
        Integer i = goTermToIndexMap.get(goTermId);
        if (i == null || i < 0) {
            System.err.println("[ERROR] Could not find index for GO Term " + goTermId);
            return 0;
        }
        int [] genes = this.termLinks[i];
        return genes.length;
    }

    public boolean []  getBooleanArrayobservedItems(Set<TermId> geneIds) {
        boolean [] observed = new boolean[this.n_genes];
        for (TermId gene : geneIds) {
            Integer i = this.annotatedItemToIndexMap.get(gene);
            if (i == null || i < 0) {
                System.err.println("[ERROR] Could not get index for gene " + gene.getValue());
                continue;
            }
            observed[i] = true;
        }
        return observed;
    }


    /**
     * Creates an array of term to item associations.
     * @return the array.
     */
    public int [][] getTermLinks()
    {
        return this.termLinks;
    }
}
