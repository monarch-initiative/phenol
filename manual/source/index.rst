==================================
Phenotype Ontology Library: Phenol
==================================

Phenol is a modern Java (version 8 and above) library for working with
phenotype and other attribute ontologies (including Gene Ontology). Phenol
provides full support for working with the
`Human Phenotype Ontology <https://hpo.jax.org/app/>`_ and HPO-based disease
annotations.



-------------
Quick Example
-------------

The following snippet will load the ``hp.obo`` file (which can be downloaded from the
`HPO website <https://hpo.jax.org/app/>`_) into an ``Ontology`` object. The HPO
has multiple subontologies, and the following code extracts all of the terms
of the `Phenotypic Abnormality <https://hpo.jax.org/app/browse/term/HP:0000118>`_ subontology.

  .. code-block:: java

    String hpoOboFilePath=...; // initialize to path of hp.obo file
    Ontology ontology =  OntologyLoader.loadOntology(new File(hpoOboFilePath));
    final TermId PHENOTYPIC_ABNORMALITY = TermId.of("HP:0000118");
    for (TermId tid : getDescendents(ontology, PHENOTYPIC_ABNORMALITY)) {
      Term hpoTerm = ontology.getTermMap().get(tid);
      // ... do something with the Term
    }



-------
History
-------

Phenolib was forked from `Ontolib <https://github.com/Phenomics/ontolib>`_  in April 2018,
and since has been extensively refactored and extended to provide
additional functionality for working with phenotype annotations. Several classes from the
`Ontologizer <http://ontologizer.de/>`_, which we initially programmed in Java 1.5, have been
refactored to support Gene Ontology overrepresentation analysis
(see `Bauer et al., 2008 <https://www.ncbi.nlm.nih.gov/pubmed/18511468>`_).




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
    tutorial_go
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
