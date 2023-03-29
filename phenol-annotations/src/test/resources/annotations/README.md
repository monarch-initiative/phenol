# README

The folder contains files for testing phenol's annotation-parsing logic.

## `phenotype.fake.hpoa`

A small HPOA file approximating the real-world annotations with 3 made-up diseases:

- `ORPHA:123456` - Made up AD disease with Abnormality of finger (HP:0001167) and NOT Slender finger (HP:0001238).
- `OMIM:987654` - Made up AR disease with Abnormality of finger (HP:0001167) and NOT Slender finger (HP:0001238). Abnormality of finger is observed in 1/8 with childhood onset and in 4/5 with pediatric onset. Slender finger is occasionally (HP:0040283) absent.
- `OMIM:111111` - Made up AR disease with Abnormality of the upper limb (HP:0002817) with Adult onset (HP:0003581) (obviously odd, but just for the sake of testing), autosomal recessive inheritance (HP:0000007), and Childhood onset (HP:0011463). The disease is used to test if global disease onset is parsed correctly. The global disease onset should be Childhood and not Adult.

## `phenotype.fake.old.hpoa`

The same content as in `phenotype.fake.hpoa` but in the previous format where the header was not valid YAML
and comments (`#`) were allowed in the text body.
