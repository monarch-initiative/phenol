# README

This folder contains resource files for testing Ontology I/O. This file describes properties of selected resource files.

## ``hp.module.json``

The `hp.module.json` contains Human Phenotype Ontology module in Obographs format. For the *Phenotypic abnormality*
subhierarchy, the module contains *Arachnodactyly* (`HP:0001166`) and its ancestors. Besides *Arachnodactyly, the module
also contains complete sibling sub-hierarchies of *Phenotypic abnormality* (e.g. Clinical modifier, Mode of inheritance,
etc.).

```shell
module load robot/1.8.3

HPO=https://github.com/obophenotype/human-phenotype-ontology/releases/download/v2024-06-25/hp.obo
wget $HPO
robot extract --input hp.obo --method BOT --term HP:0001166  convert --output arachnodactyly.hp.obo
robot extract --input hp.obo --method TOP --term HP:0012823  convert --output clinical-modifier.hp.obo
robot extract --input hp.obo --method BOT --term HP:0012823  convert --output clinical-modifier-bot.hp.obo
robot extract --input hp.obo --method TOP --term HP:0032443  convert --output past-medical-history.hp.obo
robot extract --input hp.obo --method BOT --term HP:0032443  convert --output past-medical-history-bot.hp.obo
robot extract --input hp.obo --method TOP --term HP:0000005  convert --output moi.hp.obo
robot extract --input hp.obo --method BOT --term HP:0000005  convert --output moi-bot.hp.obo
robot extract --input hp.obo --method TOP --term HP:0032223  convert --output blood-group.hp.obo
robot extract --input hp.obo --method BOT --term HP:0032223  convert --output blood-group-bot.hp.obo
robot extract --input hp.obo --method TOP --term HP:0040279  convert --output frequency.hp.obo
robot extract --input hp.obo --method BOT --term HP:0040279  convert --output frequency-bot.hp.obo

# Merge into one file
robot merge --input arachnodactyly.hp.obo \
  --input clinical-modifier.hp.obo \
  --input clinical-modifier-bot.hp.obo \
  --input past-medical-history.hp.obo \
  --input past-medical-history-bot.hp.obo \
  --input moi.hp.obo \
  --input moi-bot.hp.obo \
  --input blood-group.hp.obo \
  --input blood-group-bot.hp.obo \
  --input frequency.hp.obo \
  --input frequency-bot.hp.obo \
  --output hp.module.json

rm *.obo
```

This was followed by few extra steps:

- replace the top-level `meta` element with the value we see in the full HP JSON file
- insert the following node as the 1st position of the `nodes` list:
  ```json
  {
    "id" : "http://purl.obolibrary.org/obo/HP_0034334",
    "lbl" : "allelic_requirement",
    "type" : "PROPERTY",
    "propertyType" : "ANNOTATION",
    "meta" : {
      "basicPropertyValues" : [ {
       "pred" : "http://purl.org/dc/elements/1.1/date",
       "val" : "2022-09-04T11:14:13Z"
      }, {
       "pred" : "http://purl.org/dc/terms/creator",
       "val" : "https://orcid.org/0000-0002-0736-9199"
      } ]
    }
  }
  ```
