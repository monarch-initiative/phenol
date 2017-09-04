#!/bin/bash

# ---------------------------------------------------------------------------
# Enable unofficial Bash strict mode, see:
#
#   http://redsymbol.net/articles/unofficial-bash-strict-mode/ 
set -euo pipefail

# ---------------------------------------------------------------------------
# Get current date

TODAY=$(date +%Y-%m-%d)

# ---------------------------------------------------------------------------
# Parse CLI arguments

WORK_DIR=
CHUNK_SIZE=10
MIN_TERMS=1
MAX_TERMS=20

usage()
{
    >&2 echo "Usage: $0 -w work_dir"
    exit 1
}

set -x
while getopts "w:" o; do
    echo "o=$o"
    case "$o" in
        w)
            WORK_DIR=$(readlink -f $OPTARG)
            ;;
        *)
            usage
            ;;
    esac
done

if [[ -z "${WORK_DIR}" ]]; then
    usage
fi

# ---------------------------------------------------------------------------
# Make sure that java is available

export PATH=${PWD}/miniconda3/bin:$PATH

# ---------------------------------------------------------------------------
# Download Data

mkdir -p $WORK_DIR

test -f $WORK_DIR/hp-$TODAY.obo \
|| wget http://purl.obolibrary.org/obo/hp.obo \
   -O $WORK_DIR/hp-$TODAY.obo
test -f $WORK_DIR/ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype-$TODAY.txt \
|| wget http://compbio.charite.de/jenkins/job/hpo.annotations.monthly/lastStableBuild/artifact/annotation/ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype.txt \
   -O $WORK_DIR/ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype-$TODAY.txt

# ---------------------------------------------------------------------------
# Generate sorted gene ID list

test -f $WORK_DIR/genes-$TODAY.txt \
|| tail -n +2 work/ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype-$TODAY.txt \
   | cut -f 1 \
   | sort -nu \
   >$WORK_DIR/genes-$TODAY.txt

# ---------------------------------------------------------------------------
# Generate chunks and count

split --numeric-suffixes=1 -a 4 -l $CHUNK_SIZE $WORK_DIR/genes-$TODAY.txt $WORK_DIR/genes-$TODAY.split.
num_chunks=$(ls $WORK_DIR/genes-$TODAY.split.* | wc -l)

# ---------------------------------------------------------------------------
# Submit array job

mkdir -p $WORK_DIR/sge_log

for i in {1..20}; do
    qsub -pe smp 8 -l h_rt=04:00:00 -P critical -j y -cwd -o $WORK_DIR/sge_log -v PATH -v NUM_TERMS=$i -v TODAY=$TODAY -v WORK_DIR=$(readlink -f $WORK_DIR) -t 1-${num_chunks} build_dist_slave.sh
done
