[![CI CD](https://github.com/monarch-initiative/phenol/actions/workflows/maven.yml/badge.svg)](https://github.com/monarch-initiative/phenol/actions/workflows/maven.yml/badge.svg)
[![Documentation Status](https://readthedocs.org/projects/phenol/badge/?version=latest)](http://phenol.readthedocs.io/en/latest/?badge=latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monarchinitiative.phenol/phenol/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monarchinitiative.phenol/phenol)

# Phenol: Ontology Library for Phenomics and Genomics

A Java library for working with JSON phenotype ontologies including especially
the [Human Phenotype Ontology](https://www.human-phenotype-ontology.org) and the
[Mammalian Phenotype Ontology](http://www.informatics.jax.org/vocab/mp_ontology) and
associate phenotype annotation files.


## In Brief

- **Language/Platform:** Java >=11
- **License:** BSD 3-Clause Clear
- **Version:** 2.1.0
- **Authors:**
    - Sebastian Bauer
    - Peter N. Robinson
    - Sebastian Koehler
    - Max Schubach
    - Manuel Holtgrewe
    - HyeongSik Kim
    - Michael Gargano
    - Daniel Danis
    - Jules Jacobsen

- **Availability:**
    - `phenol-core` for dealing with (biological) ontologies.
    - `phenol-io` for reading ontologies from JSON files.
    - `phenol-annotations` for reading computational disease models of the HPO project
    - `phenol-analysis` several demo apps showing how to use phenol.
    - `phenol-cli` for performing empirical score distribution computation as a stand-alone program.

## Usage
We recommend indicating the phenol version in the `properties` section of the pom file of the application.

```
<properties>
  (...)
  <phenol.version>2.1.0</phenol.version>
</properties>
```

Then import the phenol modules that are needed for your application.
```xml
<dependency>
    <groupId>org.monarchinitiative.phenol</groupId>
    <artifactId>phenol-core</artifactId>
    <version>${phenol.version}</version>
</dependency>
<!-- ... and other modules -->
```


## History
Phenol was initially forked from [ontolib]([https://github.com/Phenomics/ontolib) in February 2018, but was
extensively refactored and extended. The API of phenol and ontolib are not compatible with each other.
