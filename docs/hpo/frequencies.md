# Frequencies and HPO Annotations

It is important to know what proportion of individuals with a given disease have a certain phenotypic feature. In general, some phenotypic features are found in all or nearly all individuals with a given disease. For instance, according to current data, all individuals with [ReNU syndrome](https://hpo.jax.org/browse/disease/OMIM:620851) have [Global developmental delay](https://hpo.jax.org/browse/term/HP:0001263), and the HPO database currently lists a frequency of 16/16 individuals. On the other hand, other manifestations are found in only some individuals; for instance, {target="_blank"} 
[Low-set ears](https://hpo.jax.org/browse/term/HP:0000369) were reported in 5/20 individuals. Some manifestations are only very rarely found
[Posteriorly rotated ears](https://hpo.jax.org/browse/term/HP:0000358) were reported in only 1/49 individuals.


## How do the HPO annotations represent frequency?


There are three formats for representing frequency data in the HPO (see the `documentation <https://obophenotype.github.io/human-phenotype-ontology/annotations/frequency/>`_ for more information). For analysis, it is convenient to have a unified representation of frequency. Therefore, phenol maps data from all three formats to the same class that is intended to be used for analysis. 



### HpoDiseaseAnnotation

This is a key class to understanding how frequencies are represented. The class contains an HPO term that is being associated to a disease as well as metadata for that term, including frequency data.


### Ratio 


This class represents the fact that <em>n</em> out of <em>m</em> subjects meet a condition. For instance,
 * <em>9</em> out of <em>10</em> kids love lasagna.

 - numerator: number of subjects observed to manifest a phenotypic abnormality (HPO term).
 - denominator: total number of evaluated subjects.
 - frequency: proportion of subjects observed to manifest a phenotypic abnormality, calculated as numerator/denominator
 - isZero: return true if the numerator is zero
 - isPositive: return true if the numerator is greater than zero

```java
public interface Ratio {
  int numerator();
  int denominator();
  float frequency();
  boolean isZero();
  boolean isPositive();
}
```


The parsing of frequency data from the HPOA file is performed by the parseFrequency method in the 
``HpoDiseaseLoaderDefault`` class that is in the phenol-annotations module in the package ``org.monarchinitiative.phenol.annotations.io.hpo``.
Note that the class has a field called cohortSize, which is the assumed size of the cohort if we do not have that information (e.g., because we only have an HPO term annotation such as [Occasional (HP:0040283)](https://hpo.jax.org/browse/term/HP:0040283).
The frequency field is processed as follows.

1. If it is empty, then we assume a ratio of 1/1
2. if it is a term, e.g., HPO term, e.g. HP:0040280 (Obligate), then we calculate the ratio as follows:
```java
// HPO term, e.g. HP:0040280 (Obligate)
if (HPO_PATTERN.matcher(frequency).matches()) {
    HpoFrequency hpoFrequency = HpoFrequency.fromTermId(TermId.of(frequency));
    numerator = isNegated
    ? 0
    : Math.round(hpoFrequency.frequency() * cohortSize);
    denominator = cohortSize;
}
```
3. if an actual frequency is given (e.g., 3/7), then the same is used for the Ratio
4. if a percentage is used (e.g., 10%), the the following calculation is performed:
```java 
float percentage = Float.parseFloat(matcher.group("value"));
numerator = Math.round(percentage * cohortSize / 100F);
denominator = cohortSize;
```. 


### Gotchas


Parsing HPO term or Percentage poses an issue, because we do not know about the cohort size; it is not in HPOA file. So, we must make some assumptions, namely, the "typical" cohort size. The phenol library sets this to five by default. This may lead to unexpected behavior, because  5% is mapped to zero of five owing to rounding.

In this case, it may be advisable to set the cohort size to 100 to avoid this kind of rounding error.
This can be done with the following code.

```java 
var databasePrefixes = Set.of(DiseaseDatabase.OMIM);
boolean salvageNegatedFrequencies = false;
int cohortSize = 100;
var loaderOptions = HpoDiseaseLoaderOptions.of(databasePrefixes,  salvageNegatedFrequencies, cohortSize);
``` 

And use these options for the HpoDiseaseLoader.


### HpoFrequency


HpoFrequency is an enumeration that is used to represent HPO Frequency terms such as [Occasional (HP:0040283)](https://hpo.jax.org/browse/term/HP:0040283), which is used to denote HPO features that occur in  5% to 29% of individuals affected by the disease being annotated. It is used as a helper to create the Ratio for HPO Term frequencies.

The class is an enum located in the ``phenol-annotations module`` in the package package ``org.monarchinitiative.phenol.annotations.formats.hpo``.

To understand how the class works, consider the enumeration for Occasional.

```java
  OCCASIONAL(HpoFrequencyTermIds.OCCASIONAL, "Occasional", 9),
```

Each enumeration has a numerator and a denominator; the denominator is hard-coded to 50, and in this case the numerator is 9, corresponding to a frequency of 18/100 or 18%, which is the mean of 5% and 29%.

If we call the frequency method, we get 

```java
case OCCASIONAL:
    return 0.05F + 0.5F * (0.29F - 0.05F);
```
which returns 5 + 0.5(29-5) = 17%.


`