==================================
Phenotype Ontology Library: Phenol
==================================

Phenol is a modern Java (version 8 and above) library for working with
phenotype and other attribute ontologies (including Gene Ontology). Phenolib
was forked from `Ontolib <https://github.com/Phenomics/ontolib>`_  and is being extended to add additional functionality
for working with phenotype annotations.


-------
Warning
-------
Phenol is undergoing substantial refactoring and is currently not intended for the faint of heart. We anticipate that
phenol will be suitable for external use by Summer 2018.




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

The best place to leave feedback, ask questions, and report bugs is the `Phenol Issue Tracker <https://github.com/monarchinitiative/phenol/issues>`_.

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
    release_howto
