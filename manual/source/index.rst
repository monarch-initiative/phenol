==================================
Welcome to OntoLib's Documentation
==================================

OntoLib is a modern Java (version 8 and above) library for working with (biological) ontologies.
You can easily load an OBO file into an `Ontology` object and work with it in your code.
Additionally, there is special support for important biological entities such as HPO and GO that make working with them easier by making important sub ontologies and terms more accessible.

-------------
Quick Example
-------------

  .. code-block:: java

    HpoOntology hpo;
    try (HpoOboParser hpoOboParser = new HpoOboParser(new File("hp.obo"))) {
      hpo = hpoOboParser.parse();
    } except (IOException e) {
      System.exit(1);
    }

    Ontology<HpoTerm, HpoTermRelation> abnormalPhenoSubOntology =
      hpo.getPhenotypicAbnormalitySubOntology();
    for (TermId termId : abnormalPhenoSubOntology.getNonObsoleteTermIds()) {
      // ...
    }

--------
Feedback
--------

The best place to leave feedback, ask questions, and report bugs is the `OntoLib Issue Tracker <https://github.com/phenomics/ontolib/issues>`_.

.. toctree::
    :caption: Installation & Tutorial
    :name: tutorial
    :maxdepth: 1
    :hidden:

    install
    tutorial_io
    tutorial_hpo
    tutorial_similarity

.. toctree::
    :caption: Project Info
    :name: project-info
    :maxdepth: 1
    :hidden:

    contributing
    authors
    history
    license
