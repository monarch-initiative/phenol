.. _tutorial_io:

==============
Input / Output
==============

Phenol provides support for loading OBO files into objects implementing the `Ontology` interface.
Currently, there is no generic parsing of OBO files (yet), so you just select one of the supported ontologies (GO, HPO, MPO, or ZPO) and use the specialized parser.
For example, for the Gene Ontology:

.. code-block:: java

    final GoOboParser parser = new GoOboParser(inputFile);
    final GoOntology go;
    try {
      go = parser.parse();
    } catch (IOException e) {
      // handle error
    }

Similarly, for the Human Phenotype Ontology:

.. code-block:: java

    final HpoOboParser parser = new HpoOboParser(inputFile);
    final HpoOntology hpo;
    try {
      hpo = parser.parse();
    } catch (IOException e) {
      // handle error
    }
