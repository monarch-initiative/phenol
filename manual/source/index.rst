==================================
Phenotype Ontology Library: Phenol
==================================

Phenol is a modern Java (version 8 and above) library for working with
phenotype and other attribute ontologies (including Gene Ontology). Phenolib
was forked from `Ontolib <https://github.com/Phenomics/ontolib>`_  and has been extended to add additional functionality
for working with phenotype annotations, as well as support Gene Ontology overrepresentation analysis, and working with some other ontologies.


-------------
Quick Example
-------------

  .. code-block:: java

    String hpoOboFilePath=...; // initialize to path of hp.obo file
    Ontology hpo =  OntologyLoader.loadOntology(new File(hpoOboFilePath));
    if (ontology==null) {
      throw new PhenolRuntimeException("Could not load ontology from \"" + hpoOboFilePath +"\"");
    }
    // The following will process all of the terms of the Phenotypic Abnormality subontology
    // and ignore terms from other subontologies such as Clinical Modifier
    final TermId PHENOTYPIC_ABNORMALITY = TermId.of("HP:0000118");
    for (TermId tid : getDescendents(ontology, PHENOTYPIC_ABNORMALITY)) {
      Term hpoTerm = ontology.getTermMap().get(tid);
      // ... do something with the Term
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
    input
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
