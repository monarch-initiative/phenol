# Test resources

`hpo_toy.json` - Subset of HPO containing all complete HPO sub-hierarchies but the `Phenotypic abnormality`, where only
the ancestors of `Arachnodactyly` are present.

## `annotations`

`phenotype.excerpt.hpoa` - subset of the real-life HPO annotation file containing:
- `OMIM:608328`	Weill-Marchesani syndrome 2, dominant
- `OMIM:614185`	Geleophysic dysplasia 2
- `OMIM:540000`	Mitochondrial myopathy, encephalopathy, lactic acidosis, and stroke-like episodes
- `OMIM:100300`	Adams-Oliver syndrome 1
- `OMIM:616914`	Marfan lipodystrophy syndrome
- `OMIM:142900`	Holt-Oram syndrome
- `OMIM:604308`	Mass syndrome
- `ORPHA:166035` Brachydactyly-short stature-retinitis pigmentosa syndrome
- `OMIM:184900`	Stiff skin syndrome
- `OMIM:154700`	Marfan syndrome
- `OMIM:129600`	Ectopia lentis, familial
- `OMIM:102370`	Acromicric dysplasia
- `OMIM:143890`	Hypercholesterolemia, familial, 1

`phenotype.fake.hpoa` - a collection of made-up diseases to test `HpoDiseaseAnnotationLoader`. All phenotypes are
present in `hpo_toy.json`.

*TODO* - add description for the remaining test resources
