Input
#####

The phenol library is mainly intended to support working with the Human Phenotype Ontology,
the Mammalian Phenotype Ontology, the Gene Ontology, MONDO, and ECTO, but has also been
tested with the OBO version of NCIT.

The same code is used to input any obo file. FOr instance, to ingest the HPO, use the following code.
The HPO is in the default  curie map and only contains known relationships (is-a) and HP terms. ::

  String hpoPath="/some/path/hp.obo";
  Ontology hpoOntology = OntologyLoader.loadOntology(new File(hpoPath));

The GO is in the default curie map but also contains BFO and RO terms with unknown relationships
we want to ignore these so here we specify the term prefixes we want to use. It has three possible root
nodes (biological_process, cellular_component, biological_function) so an artificial GO:0000000 root is
added. ::

  Ontology goOntology = OntologyLoader.loadOntology(goFile, "GO")


ECTO isn't mapped in the default Curie mappings, so we need to add it here (the PURL isn't correct).



ECTO also contains a bunch of unknown relationships so we're going to simplify this graph by only
loading ECTO nodes (this ignores the true root term XCO:0000000) and other nodes from CHEBI.
BFO and UBERON among others. ::

  CurieUtil curieUtil = CurieUtilBuilder.withDefaultsAnd(ImmutableMap.of("ECTO", http://purl.obolibrary.org/obo/ECTO_"));
  Ontology ecto = OntologyLoader.loadOntology(ectoFile, curieUtil, "ECTO");
  ecto.getRelationMap().values().forEach(relationship -> assertEquals(RelationshipType.IS_A, relationship.getRelationshipType()));
  // test if you like..
  assertEquals(TermId.of("owl:Thing"), ecto.getRootTermId());
  assertEquals(2272, ecto.countNonObsoleteTerms());
  assertEquals(0, ecto.countObsoleteTerms());


ToDo -- add documentation for other target ontologies.
