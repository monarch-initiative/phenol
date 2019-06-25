Input
#####

The phenol library is mainly intended to support working with the Human Phenotype Ontology,
the Mammalian Phenotype Ontology, the Gene Ontology, MONDO, and ECTO, but has also been
tested with the OBO version of NCIT.


Human Phenotype Ontology
~~~~~~~~~~~~~~~~~~~~~~~~
To load the `Human Phenotype Ontology (HPO) <https://hpo.jax.org/app/>`_, use the following code.
The HPO is in the default  curie map and only contains known relationships (is-a) and HP terms. ::

  String hpoPath="/some/path/hp.obo";
  Ontology hpoOntology = OntologyLoader.loadOntology(new File(hpoPath));


Mammalian Phenotype Ontology
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
The `Mammalian Phenotype Ontology (MP) <http://www.informatics.jax.org/vocab/mp_ontology/>`_
can be loaded using the same command. ::

  String mpPath="/some/path/mp.obo";
  Ontology hpoOntology = OntologyLoader.loadOntology(new File(mpPath));

Gene Ontology
~~~~~~~~~~~~~

The `Gene Ontology (GO) <http://geneontology.org/>`_ is in the default curie map but also contains BFO and RO terms with unknown relationships
we want to ignore these so here we specify the term prefixes we want to use. It has three possible root
nodes (biological_process, cellular_component, biological_function) so an artificial GO:0000000 root is
added. ::

  String goPath="/some/path/go.obo";
  Ontology goOntology = OntologyLoader.loadOntology(goPath, "GO")


Environmental conditions, treatments and exposures ontology
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The `Environmental conditions, treatments and exposures ontology (ECTO) <https://github.com/EnvironmentOntology/environmental-exposure-ontology>`_
contains multiple relationships so we're going to simplify this graph by only
loading ECTO nodes (this ignores the true root term XCO:0000000) and other nodes from CHEBI.
BFO and UBERON among others. ::

  CurieUtil curieUtil = CurieUtilBuilder.withDefaultsAnd(ImmutableMap.of("ECTO", http://purl.obolibrary.org/obo/ECTO_"));
  Ontology ecto = OntologyLoader.loadOntology(ectoFile, curieUtil, "ECTO");
  ecto.getRelationMap().values().forEach(relationship -> assertEquals(RelationshipType.IS_A, relationship.getRelationshipType()));
  // test if you like..
  assertEquals(TermId.of("owl:Thing"), ecto.getRootTermId());
  assertEquals(2272, ecto.countNonObsoleteTerms());
  assertEquals(0, ecto.countObsoleteTerms());



