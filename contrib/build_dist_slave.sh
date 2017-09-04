#!/bin/bash

set -euo pipefail
set IFS=$"\n"

set -x

chunk=$(ls $WORK_DIR/genes-$TODAY.split.* | sort | awk "(NR == $SGE_TASK_ID)")
first=$(head -n 1 $chunk)
last=$(tail -n 1 $chunk)

test -f $WORK_DIR/output-$TODAY-$SGE_TASK_ID.txt \
|| java -jar $(ls ontolib-cli-*.jar | tail) \
    precompute-scores \
    --gene-to-term-file $WORK_DIR/ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype-$TODAY.txt \
    --num-threads 10 \
    --input-obo-file $WORK_DIR/hp-$TODAY.obo \
    --min-num-terms $NUM_TERMS \
    --max-num-terms $NUM_TERMS \
    --output-score-dist $WORK_DIR/output-$TODAY-$NUM_TERMS-$SGE_TASK_ID.txt \
    --min-object-id $first \
    --max-object-id $last \
    --num-iterations 100000
