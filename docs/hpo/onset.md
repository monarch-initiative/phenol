# HPO Onset

In the HPO annotations, diseases can have onset annotations (this refers to the age at which the first disease-associated manifestation appears), and individual HPO terms can have onset
annotations (this refers to the age at which the HPO manifestation appears).

phenol derives the global onset si derived from the clinical modifier HPOA lines. If it is absent, then it is derived from the Aspect.P annotations (fall back). If it is still not available, then the disease simply has no known onset.

## Workwith with onset annotations

For instance, if you want to identifiy all diseases with [Infantile onset]() or [Childhood onset](), the following code can be used


```java
private boolean hasChildhoodOrInfantileOnset(HpoDisease disease) {
    HpoOnset infantileOnset = HpoOnset.INFANTILE_ONSET;
    HpoOnset childhoodOnset = HpoOnset.CHILDHOOD_ONSET;
    TemporalInterval neoAndChildInterval = TemporalInterval.of(infantileOnset.start(), childhoodOnset.end());
    Optional<TemporalInterval> opt = disease.diseaseOnset();
    if (opt.isPresent()) {
        TemporalInterval interval = opt.get();
        return interval.overlapsWith(neoAndChildInterval);
    } else {
        return false;
    }
}
```

## TODO -- explain the most important classes