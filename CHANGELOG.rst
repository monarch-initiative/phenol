=========
Changelog
=========

------
latest
------

-----
2.0.4
-----

Minor release, no breaking changes.

- Fix broken implementation of `MinimalOntology.subOntology` method.
- Simplify implementation of CSR adjacency matrix that backs `OntologyGraph` by default.
- Improve `OntologyGraph` test coverage.
- Add microbenchmark to benchmark the ontology loading.
- Update dependencies, remove Guava usage.

------
2.0.3
------

Minor release, no breaking changes.

- `MinimalOntology` is enough for the `phenol-annotations` functionality to work.
- Add checks to see if the ontology meets the following:
  - we have `Term`s for all subjects and objects of the ontology graph edges
  - the ontology graph is simple - no self-loops and duplicated edges are present.
    Note: >1 edges between a pair of nodes is allowed as long as the edges are of a different relation type.
  - the ontology graph is *connected* - the graph consists of one connected component.
- Allow to opt out of compatibility checks by setting `OntologyLoaderOptions.forceBuild` to `true`.
- Improve Javadocs

------
2.0.2
------

Minor release, no breaking changes.

* deprecate several `MinimalOntology` and `Ontology` methods (to be removed in `v3.0.0`), and introduction of the `OntologyGraph` API.
* setup Javadoc & documentation deployment via CI

------
v2.0.1
------

Minor changes
#############

- Use Jackson annotations to configure serialization of `TermId` and `Identified`
- update dependencies

------
v2.0.0
------
- Upgrade to Java 11+, add ``module-info`` files
- Support for GO GAF 2.2 files
- Speed up build by adding a new build profile
- ``phenol-core``
  - ``MinimalOntology`` has a version
  - do not use non-propagating relationships during ontology traversals
- ``phenol-io``
  - Dropping support for reading OBO/OWL ontologies
  - drop non-modular `curie-util` dependency
- ``phenol-annotations``
  - Remodel ``HpoDisease``, ``HpoAnnotation``, ``HpoAssociationData``, ``GeneIdentifier``s, etc..
  - ``HpoDiseases`` has a version
  - Model temporal elements
  - Implement ``HGNCGeneIdentifierLoader`` for reading ``GeneIdentifiers`` from HGNC complete set archive.
  - Add new ``HpoOnset`` terms.
  - Consolidate hardcoded HPO constants (``TermId``s) into ``org.monarchinitiative.phenol.annotations.constants.hpo`` package
  - Standardize HPO annotations header, ensure the parsers can read the older releases.
  - Deprecate the code for parsing small files and move to hpoannotQC

------
v1.6.3
------
- added class for efficient precalculation of Resnik scores for HPO
- various bug fixes

------
v1.6.1
------
- Fixed issue with parsing Orphanet en_product4.xml

------
v1.6.0
------
- Simplified interface to GO overrepresentation analysis classes.
- Parent-Child Gene Ontology overrepresentation analysis with unit tests
- MGSA bugfix

------
v1.5.0
------
- improved functions for display of upper level HPO categories

------
v1.4.3
------
- fixing bug in parsing of MGI Genetic Marker file
- fixing bug in parsing of orphanet genes file, en_product6.xml

------
v1.4.2
------
- Prototype ingest of JSON ontology files
- flexible handling of relation types
- bug fix of previously incorrect handling of tRNA genes

------
v1.4.1
------
- Added workaround for duplicated lines in Homo_sapiens_gene_info file
- Added phenotype to gene extraction

------
v1.4.0
------
- Added Orphanet inheritance parser
- Aded several demonstration programs
- refactored files for constructing the phenotype.hpoa file


------
v1.3.3
------
- Various bug fixes
- Orphanet inheritance XML file ingest
- Adding additional demo app to show how to access Term information (hpdemo).

------
v1.3.0
------
- refactored TermAnnotation interface to use TermId instead of String to identify objects being annotation
- refactored GoGaf21Annotation class to use TermId internally instead of Strings for db and dbObjectId
- refactored to use junit 5 (allowing legacy use of junit 4, will migrate completely in coming releases)

---------------
v1.2.5-SNAPSHOT
---------------
- moving to SNAPSHOT version names to conform with maven standards
- fixed bug in initialized association lists for Gene Ontology analysis.

------
v1.1.4
------
- Adding parsing of onset, modifier, PMID/source to HpoAnnotation class
- Adding all relation types relevant to MONDO

------
v1.1.3
------
- Adding parsing of relations other than IS_A for Gene Ontology
- Fixing calculation of frequency (double) from frequency category
- allowing any valid curie as cross-ref

------
v1.1.2
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
