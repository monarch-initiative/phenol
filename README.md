[![Build Status](https://travis-ci.org/monarch-initiative/phenol.svg?branch=master)](https://travis-ci.org/monarch-initiative/phenol)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a0868b9dbdfd499fbcb5343275afc789)](https://www.codacy.com/app/monarch-initiative/phenol?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monarch-initiative/phenol&amp;utm_campaign=Badge_Grade)
[![Documentation Status](https://readthedocs.org/projects/phenol/badge/?version=latest)](http://phenol.readthedocs.io/en/latest/?badge=latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.phenomics/ontolib-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.phenomics/ontolib-core)

# Phenol: Ontology Library for Phenomics and Genomics

A Java library for working with OBO or OWL phenotype ontologies including especially
the [Human Phenotype Ontology](https://www.human-phenotype-ontology.org) and the
[Mammalian Phenotype Ontology](http://www.informatics.jax.org/vocab/mp_ontology) and
associate phenotype annotation files.


## In Brief

- **Language/Platform:** Java >=8
- **License:** BSD 3-Clause Clear
- **Version:** 0.1.8
- **Authors:**
    - Sebastian Bauer
    - Peter N. Robinson
    - Sebastian Koehler
    - Max Schubach
    - Manuel Holtgrewe
    - HyeongSik Kim
    - Michael Gargano
    
- **Availability:**
    - Maven module `phenol-core` for dealing with (biological) ontologies ([Javadoc via javadoc.io](http://javadoc.io/doc/com.github.phenomics/ontolib-core/0.3)).
    - Maven module `phenol-io` for reading ontologies from OBO files ([Javadoc via javadoc.io](http://javadoc.io/doc/com.github.phenomics/ontolib-io/0.3)).
    - Maven module `phenol-cli` for performing empirical score distribution computation as a stand-alone program.

## Usage
We are working on the first public distribution of phenol, which we will make available on maven central.
For now, to use phenol in application code, it must be installed locally by cloning this GitHub
repository and entering the following command.
```bash
$ mvn install
```
This will install the phenol library code into your local maven repository (.m2 directory) and
thereby make it available to application code, which can use phenol by adding the following
stanza to the pom.xml file.
```xml
<dependency>
    <groupId>org.monarchinitiative.phenol</groupId>
    <artifactId>phenol-io</artifactId>
    <version>${phenol.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
The above stanza shows the code that is needed for the phenol-io component, and also shows
how to exclude the slf4j version from the application code (which can help to avoid maven enforcer errors).
Add analogous stanzas for other phenol modules.

## Input files


## History
Phenol was initially forked from [ontolib]([https://github.com/Phenomics/ontolib) in February 2018, but was
extensively refactored and extended. The API of phenol and ontolib are not compatible with each other.
