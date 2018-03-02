.. _tutorial_similarity:

=============================
Querying with Term Similarity
=============================

<Todo-- update>
Phenol provides multiple routines for computing the similarity of terms, given an ontology.
For the "classic" similarity measures, the `Similarity` interface provides the corresponding interface definition.

Here is how to compute the Jaccard similarity between two sets of terms.

.. code-block:: java

    // final HpoOntology hpo = ...
    JaccardSimilarity<HpoTerm, HpoTermRelation> similarity =
        new JaccardSimilarity<>(hpo);
    // List<TermId> queryTerms = ...
    // List<TermId> dbTerms = ...
    double score = similarity.computeScore(queryTerms, dbTerms);

The Resnik similarity is a bit more complicated as it requires the precomputation of the information content.

.. code-block:: java

    // final ArrayList<HpoDiseaseAnnotation> diseaseAnnotations = ...
    InformationContentComputation<HpoTerm, HpoTermRelation> computation =
        new InformationContentComputation<>(hpo);
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(hpo, diseaseAnnotations);
    Map<TermId, Double> informationContent =
        computation.computeInformationContent(termLabels);
    PairwiseResnikSimilarity<VegetableTerm, VegetableTermRelation> pairwise =
        new PairwiseResnikSimilarity<>(hpo, informationContent);
    ResnikSimilarity<HpoTerm, HpoTermRelation> similarity =
        new ResnikSimilarity<>(pairwise, /* symmetric = */true);
    // List<TermId> queryTerms = ...
    // List<TermId> dbTerms = ...
    double score = similarity.computeScore(queryTerms, dbTerms);
