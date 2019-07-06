[![Build Status](https://travis-ci.org/monarch-initiative/phenol.svg?branch=master)](https://travis-ci.org/monarch-initiative/phenol)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a0868b9dbdfd499fbcb5343275afc789)](https://www.codacy.com/app/monarch-initiative/phenol?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monarch-initiative/phenol&amp;utm_campaign=Badge_Grade)
[![Documentation Status](https://readthedocs.org/projects/phenol/badge/?version=latest)](http://phenol.readthedocs.io/en/latest/?badge=latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monarchinitiative.phenol/phenol-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monarchinitiative.phenol/phenol-core)

# Phenol: Ontology Library for Phenomics and Genomics

A Java library for working with OBO or OWL phenotype ontologies including especially
the [Human Phenotype Ontology](https://www.human-phenotype-ontology.org) and the
[Mammalian Phenotype Ontology](http://www.informatics.jax.org/vocab/mp_ontology) and
associate phenotype annotation files.


## In Brief

- **Language/Platform:** Java >=8
- **License:** BSD 3-Clause Clear
- **Version:** 1.4.1
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
    - Maven module `phenol-core` for dealing with (biological) ontologies ([Javadoc via javadoc.io](http://javadoc.io/doc/com.github.phenomics/ontolib-core/0.3)).
    - Maven module `phenol-io` for reading ontologies from OBO files ([Javadoc via javadoc.io](http://javadoc.io/doc/com.github.phenomics/ontolib-io/0.3)).
    - Maven module `phenol-cli` for performing empirical score distribution computation as a stand-alone program.
    - Maven module `phenol-analysis` -- several demo apps showing how to use phenol.

## Usage
We recommend indicating the
phenol version in the properties section of the pom file of the application.

```
<properties>
  (...)
  <phenol.version>1.4.0</phenol.version>
</properties>
```

Then import the phenol modules that are needed for your application.
```xml
<dependency>
    <groupId>org.monarchinitiative.phenol</groupId>
    <artifactId>phenol-core</artifactId>
    <version>${phenol.version}</version>
</dependency>
<dependency>
    <groupId>org.monarchinitiative.phenol</groupId>
    <artifactId>phenol-io</artifactId>
    <version>${phenol.version}</version>
</dependency>
```


## History
Phenol was initially forked from [ontolib]([https://github.com/Phenomics/ontolib) in February 2018, but was
extensively refactored and extended. The API of phenol and ontolib are not compatible with each other.
