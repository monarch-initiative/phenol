.. _tutorial_go:

==============================
Using phenol for Gene Ontology
==============================

phenol supports working with the GO in several following ways including some GO term enrichment analysis approaches.


Loading the data
~~~~~~~~~~~~~~~~

To perform enrichment analysis, we require the GO ontology file, the annotation file,
as well as a population set (e.g., all genes in a genome) and a study set (e.g., some
set of genes determined to be differentially expressed).


.. code-block:: java

  Ontology gontology = OntologyLoader.loadOntology(new File(pathGoObo), "GO");
  final GoGeneAnnotationParser annotparser = new GoGeneAnnotationParser(pathGoGaf);
  List<TermAnnotation> goAnnots = annotparser.getTermAnnotations();
  AssociationContainer associationContainer = new AssociationContainer(goAnnots);
  Set<TermId> populationGenes = getPopulationSet(goAnnots);
  StudySet populationSet = new StudySet(populationGenes,"population",associationContainer,gontology);
  Set<TermId> studyGenes = ... // get list of genes from study set
  StudySet studySet = new StudySet(studyGenes,"study",associationContainer,gontology);


Perform testing for overrepresentation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In this example, we show how to use the exact Fisher test to assess term overrepresentation.

.. code-block:: java
  Hypergeometric hgeo = new Hypergeometric();
  MultipleTestingCorrection<Item2PValue> bonf = new Bonferroni<>();
  TermForTermPValueCalculation tftpvalcal = new TermForTermPValueCalculation(gontology,
        associationContainer,
        populationSet,
        studySet,
        hgeo,
        bonf);

  int popsize=populationGenes.size();
  int studysize=studyGenes.size();
  List<GoTerm2PValAndCounts> pvals = tftpvalcal.calculatePVals();
  for (GoTerm2PValAndCounts item : pvals) {
    // output or do something else
  }


See the implementation in ``GoEnrichmentDemo.java``  for more details.
