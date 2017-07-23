.. _tutorial_hpo:

================
Working with HPO
================

OntoLib supports you in working with the HPO in the following ways:

- The ``HpoOntology`` class supports the standard ``Ontology`` interface.
  Besides this, the "phenotypic abnormality" sub ontology is extracted on construction and is available through ``HpoOntology.getPhenotypicAbnormalitySubOntology()``.
- The classes ``HpoFrequencyTermIds``, ``HpoModeOfInheritanceTermIds``, and ``HpoSubOntologyRootTermIds`` provide shortcuts to important/special terms that come in handy when using the HPO.
- OntoLib provides means to parse disease-to-phenotype and disease-to-gene annotations from the HPO project into ``HpoDiseaseAnnotation`` and ``HpoGeneAnnotation`` objects.

This section will demonstrate these three features.

---------------------
The HpoOntology Class
---------------------

When iterating over all primary (i.e., not alternative) and non-obsolete term IDs, you should use the ``getNonObsoleteTermIds()`` method for obtaining a ``Set`` of these ``TermId``s

.. code-block:: java

  // final HpoOntology hpo = ...
  System.err.println("Term IDs in HPO (primary, non-obsolete)");
  for (TermId termId : hpo.getNonObsoleteTermIds()) {
    System.err.println(termId);
  }

You can obtain the correct ``HpoTerm`` instance for the given ``TermId`` by consulting the resulting ``Map`` from calling ``getTermMap()``:

.. code-block:: java

  System.err.println("Term IDs+names in HPO (primary IDs, non-obsolete)");
  for (TermId termId : hpo.getNonObsoleteTermIds()) {
    final HpoTerm term = hpo.getTermMap().get(termId);
    System.err.println(termId + "\t" + term.getName());
  }

The "phenotypic abnormality" sub ontology, can be accessed with ease and then used just like all other ``Ontology`` objects.

.. code-block:: java

  // final HpoOntology hpo = ...
  final Ontology<HpoTerm, HpoTermRelation> subOntology =
      hpo.getPhenotypicAbnormalitySubOntology();
  System.err.println("Term IDs in phenotypic abnormality sub ontology");
  for (TermId termId : subOntology.getNonObsoleteTermIds()) {
    System.err.println(termId);
  }

-------------------------------
Shortcuts to Important Term IDs
-------------------------------

These can be accessed as follows.

.. code-block:: java

  System.err.println("ALWAYS_PRESENT\t", HpoFrequencyTermIds.ALWAYS_PRESENT);
  System.err.println("FREQUENT\t", HpoFrequencyTermIds.FREQUENT);

  System.err.println("X_LINKED_RECESSIVE\t", HpoModeOfInheritanceTermIds.X_LINKED_RECESSIVE);
  System.err.println("AUTOSOMAL_DOMINANT\t", HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT);

  System.err.println("PHENOTYPIC_ABNORMALITY\t", HpoSubOntologyRootTermIds.PHENOTYPIC_ABNORMALITY);
  System.err.println("FREQUENCY\t", HpoSubOntologyRootTermIds.FREQUENCY);
  System.err.println("MODE_OF_INHERITANCE\t", HpoSubOntologyRootTermIds.MODE_OF_INHERITANCE);

------------------------
Parsing Annotation Files
------------------------

You can parse the phenotype-to-disease annotation files as follows.

.. code-block:: java

  File inputFile = new File("phenotype_annotation.tab");
  try {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(inputFile);
    while (parser.hasNext()) {
      HpoDiseaseAnnotation anno = parser.next();
      // work with anno
    }
  } except (IOException e) {
    System.err.println("Problem reading from file.");
  } except (TermAnnotationException e) {
    System.err.println("Problem parsing file.");
  }

The phenotype-to-gene annotation file can be parsed as follows.

.. code-block:: java

  File inputFile = new File("phenotype_annotation.tab");
  try {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(inputFile);
    while (parser.hasNext()) {
      HpoDiseaseAnnotation anno = parser.next();
      // ...
    }
  } except (IOException e) {
    System.err.println("Problem reading from file.");
  } except (TermAnnotationException e) {
    System.err.println("Problem parsing file.");
  }
