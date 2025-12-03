# Working with HPO Annotations


Phenol ingests the ``phenotype.hpoa`` file to get information about diseases including HPO Frequency information.
See the [documentation](https://obophenotype.github.io/human-phenotype-ontology/annotations/phenotype_hpoa/) for more information about this file.



## Parsing Annotation Files


You can parse the phenotype-to-disease annotation files as follows.

```java
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.io.obo.hpo.HpoDiseaseAnnotationParser;
HpoDiseaseAnnotationParser annotationParser =
    new HpoDiseaseAnnotationParser(phenotypeAnnotationPath,ontology);
try {
  Map<TermId, HpoDisease> diseaseMap = annotationParser.parse();
  if (!annotationParser.validParse()) {
        int n = annotationParser.getErrors().size();
          logger.warn("Parse problems encountered with the annotation file at {}. Got {} errors",
                this.phenotypeAnnotationPath,n);
  }
  return diseaseMap; // or do something else with the data
} catch (PhenolException e) {
      e.printStackTrace(); // or do something else
}
```


## Parsing Annotation Files for Specific Sources


To limit the import to data representing diseases in the DECIPHER database, use the following
code (the rest is identical). Currently, DECIPHER, OMIM, and ORPHA are available.


```java
List<String> desiredDatabasePrefixes=ImmutableList.of("DECIPHER");
HpoDiseaseAnnotationParser annotationParser =
    new HpoDiseaseAnnotationParser(phenotypeAnnotationPath,ontology,desiredDatabasePrefixes);
```
