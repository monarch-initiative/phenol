.. _rsthpoannotations:

============================
Working with HPO Annotations
============================

Please see :ref:`rstterm` for information about how to access individual HPO terms.
This tutorial intends to explain how to access HPO annotation data as contained in
the `phenotype.hpoa <http://compbio.charite.de/jenkins/job/hpo.annotations.2018/lastSuccessfulBuild/artifact/misc_2018/phenotype.hpoa>`_
file.




------------------------
Parsing Annotation Files
------------------------

You can parse the phenotype-to-disease annotation files as follows.

.. code-block:: java

  import org.monarchinitiative.phenol.ontology.data.TermId;
  import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
  import org.monarchinitiative.phenol.io.obo.hpo.HpoDiseaseAnnotationParser;
  HpoDiseaseAnnotationParser annotationParser =
      new HpoDiseaseAnnotationParser(phenotypeAnnotationPath,ontology);
  try {
    Map<TermId, HpoDisease> diseaseMap = annotationParser.parse();
    if (!annotationParser.validParse()) {
          int n = annotationParser.getErrors().size();
           logger.warn("Parse problems encountered with the annotation file at {}. Got {} errors",
                 this.phenotypeAnnotationPath,n);
    }
    return diseaseMap; // or do something else with the data
  } catch (PhenolException e) {
       e.printStackTrace(); // or do something else
  }


---------------------------------------------
Parsing Annotation Files for Specific Sources
---------------------------------------------

To limit the import to data representing diseases in the DECIPHER database, use the following
code (the rest is identical). Currently, DECIPHER, OMIM, and ORPHA are available.


.. code-block:: java

  List<String> desiredDatabasePrefixes=ImmutableList.of("DECIPHER");
  HpoDiseaseAnnotationParser annotationParser =
      new HpoDiseaseAnnotationParser(phenotypeAnnotationPath,ontology,desiredDatabasePrefixes);
