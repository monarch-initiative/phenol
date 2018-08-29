=========
Changelog
=========
------
v1.2.3
------
- Adding parsing of relations other than IS_A for Gene Ontology

------
v1.2.2
------
- Adding MP annotation parser for MGI_GenePheno.rst and MGI_Pheno_Sex.rst


------
v1.1.1
------
- HPO Annotation parser now indexes diseases as a TermId representing the disease CURIE, e.g., MONDO:0000042.
- HPO Annotation parser now uses new 'big-file' format (with updated treatment of biocuration field)

------
v1.0.3
------
- refactored MP and GO parsing to use new OWLAPI-based parser
- adding support for adding artificial root to ontologies such as GO with multiple root terms.
- upgraded to obographs v0.1.1

------
v1.0.2
------
- refactored TermId to remove superfluous interface and renamed ImmutableTermId to TermId
- refactored TermSynonym to remove superfluous interface
- adding support for alt term ids to Owl2OboTermFactory (class renamed from GenericOwlFactory)
- adding support for database_cross_reference (usually PMID, ISBM, HPO, or MGI--added to term definitions)

------
v1.0.0
------
- completed refactoring to use single Term/Relationship. The API is not backwards compatible with versions prior to v0.1.9.

------
v0.1.9
------
- refactored to use just a single Term and Relationship instead of having separate types for each ontology. Simplified
classes that were templated to allow e.g., MpoTerm, MpoRelationship by hardcoding Term,Relationship and removing template.

------
v0.1.8
------
- refactored HpoAnnotation from HpoTermId

------
v0.1.7
------
- refactored phenol to use JGraphT library
- Adding OWLAPI based parser
- Refactoring HPO Disease annotation parser

------
v0.1.6
------
- refactored HPO disease annotation parser (changed API)

------
v0.1.5
------
- changed package and project name to phenol - Phenotype Ontology Library

------
v0.1.4
------
- fix to GOA parser
- added HPODiseaseWithMetaData parser
- added functions to calculate Term relationships (sibling, subclass, related, not-related)

------
v0.1.2
------
- refactored HpoFreqeuncy class to return frequencies (i.e., a number in [0,1]) rather than percentage
- Added HpoOnset classes
- Added HpoDiseaseWithMetadata class to encompass frequency and onset data


-----------
v0.4/v0.1.1
-----------
- forked from ontolib
- fixed mp.obo parse error
- fixed subontology creation error (TermMap, TermRelation)
- Adding Adding class ``OntologyAlgorithm`` with test class ``OntologyAlgorithmTest``.
Implements functions to get children, parents, descendents and ancestors.

----
v0.3
----

- ``xref`` tags are now parsed and their content is available in ``Term``.
  Added appropriate classes for representation.
- Added ``Ontology.getParent()``.
- Removed ``JaccardIcWeightedSimilarity``, ``JiangSimilarity``, ``LinSimilarity``, supporting code and tests.
- Refactoring the code for object score I/O into ``ontolib-io`` package.
- Adding support for score distribution reading and writing to H2 database files.
- ``Ontology.getAncestorTermIds()`` now also resolves alternative term IDs.
- Fixing dependency on slf4j components in ``ontolib-core`` and ``ontolib-io``.
- Adding ``getPrimaryTermId()`` in ``Ontology``.

----
v0.2
----

- Making date parser for HPO annotation files more robust.
  It works now for positive and negative associations.
- Small bug fix in HPO OBO parser.
- Adding ``ontolib-cli`` package that allows score distribution precomputation from the command line.
- Removed some dead code.
- Added various tests, minor internal refactoring.
- Moved ``OntologyTerms`` into ``ontology.algo`` package.

----
v0.1
----

- Everything is new.
