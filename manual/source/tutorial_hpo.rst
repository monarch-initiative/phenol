.. _tutorial_hpo:

================
Working with HPO
================

Phenol supports many different operations on the `Human Phenotype Ontology (HPO) <https://hpo.jax.org/app/>`_.
Many applications will w
ant to input both the hp ontology file as well as the HPO annotations,
the `phenotype.hpoa <http://compbio.charite.de/jenkins/job/hpo.annotations.2018/lastSuccessfulBuild/artifact/misc_2018/phenotype.hpoa>`_
file.


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
