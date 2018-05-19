=========
Changelog
=========

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
