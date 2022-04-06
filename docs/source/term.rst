.. _rstterm:

===========================
Working with the Term class
===========================

Each of the concepts, or terms, of an ontology in represented using the class *Term*.
The following excerpt shows how to access information contained in a typical term from
the `Human Phenotype Ontology (HPO) <https://hpo.jax.org/app/>`_, but the code would
work for other OBO ontologies such as MP, GO, and ECTO.


Getting a list of all terms
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

  String hpoPath = ....; // get the path to the hp.obo file
  Ontology hpo = OntologyLoader.loadOntology(new File(hpoPath));
  System.err.println("Term IDs in HPO (primary, non-obsolete)");
  for (TermId termId : hpo.getNonObsoleteTermIds()) {
    System.err.println(termId);
  }


Getting information about an individual term
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    Ontology hpo = OntologyLoader.loadOntology(new File(hpoPath));
    TermId rootTermId = hpo.getRootTermId();
    String rootLabel = hpo.getTermMap().get(rootTermId).getName();

    System.out.println("root term: " + rootLabel + " (" + rootTermId.getValue() + ")");

    TermId tid = TermId.of("HP:0001807");
    Term ridgedNail = hpo.getTermMap().get(tid);
    System.out.println("Data contained in term: " + ridgedNail.getName() +" ("+tid.getValue()+")");
    String definition = ridgedNail.getDefinition();
    System.out.println("Definition: "+definition);
    List<TermSynonym> synonyms = ridgedNail.getSynonyms();
    System.out.println("Synonyms:");
    for (TermSynonym syn : synonyms) {
      String val =syn.getValue();
      String scope = syn.getScope().name();
      List<TermXref> xrefs = syn.getTermXrefs();
      String xrefstring = xrefs.stream().map(TermXref::getDescription).collect(Collectors.joining("; "));
      System.out.println("\tval:"+val +", scope: "+scope + ", xrefs: " + xrefstring);
    }
    System.out.println("Alternative IDs:");
    List<TermId> alternateIds = ridgedNail.getAltTermIds();
    for (TermId altId : alternateIds) {
      System.out.println("\t"+altId.getValue());
    }
    String comment = ridgedNail.getComment();
    System.out.println("Comment: "+comment);
    List<Dbxref> xrefs = ridgedNail.getXrefs();
    System.out.println("Cross references:");
    for (Dbxref xref: xrefs) {
      System.out.println("\t" + xref.getName());
    }
    System.out.println("Database Cross references:");
    List<SimpleXref> databaseXrefs = ridgedNail.getDatabaseXrefs();
    for (SimpleXref dbxref : databaseXrefs) {
      System.out.println("\t" + dbxref.toString());
    }
    List<SimpleXref> pmids = ridgedNail.getPmidXrefs();
    System.out.println("PubMed ids:");
    for (SimpleXref pmid : pmids) {
      System.out.println("\t" + pmid.toString());
    }
    List<String> subsets = ridgedNail.getSubsets();
    System.out.println("Subsets:");
    for (String sset : subsets) {
      System.out.println("\t" + sset);
    }



The output of this code is as follows. ::

  root term: All (HP:0000001)
  Data contained in term: Ridged nail (HP:0001807)
  Definition: Longitudinal, linear prominences in the nail plate.
  Synonyms:
  	val:Grooved nails, scope: EXACT, xrefs:
	  val:Longitudinal ridging, scope: EXACT, xrefs:
	  val:Nail ridging, scope: EXACT, xrefs:
	  val:Ridged nails, scope: RELATED, xrefs:
  Alternative IDs:
  	HP:0001801
	  HP:0001811
  Comment: There may be only one, or several ridges. The affected digits should be specified.
  Cross references:
	  UMLS:C0423820
	  SNOMEDCT_US:271768001
  Database Cross references:
	  PMID:19125433
  PubMed ids:
	  PMID:19125433
  Subsets:
	  http://purl.obolibrary.org/obo/hp.obo#hposlim_core



Please see HpDemo.java in the  ``phenol-cli`` branch. The demo can be run with the following command. ::

  $ mvn package # if necessary
  $ java -jar phenol-cli/target/phenol-cli.jar hp-demo -o hp.obo -a phenotype.hpoa

