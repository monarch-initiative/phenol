package org.monarchinitiative.phenol.analysis.mgsa;

public abstract class MgsaScoreBase
{
    /** Indicates the activation state of a term */
    private boolean [] isActive;

    /** Maps a term id to the ids of the genes to which the term is annotated */
    private int [][] termLinks;

    /**
     * Contains indices to terms of termsArray.
     */
    protected int [] termPartition;

    /**
     * The current number of inactive terms. Represents the
     * first part of the partition.
     */
    protected int numInactiveTerms;

    protected final int numTerms;

    /**
     * Contains the position/index of the terms in the partition
     * (i.e., termPartition[positionOfTermInPartition[i]] = i must hold)
     */
    protected int [] positionOfTermInPartition;

    /** Array indicating the genes that have been observed */
    protected boolean [] observedGenes;

    /** Array that indicate the activation counts of the genes */
    private int [] activeHiddenGenes;

    public MgsaScoreBase(int [][] termLinks, int numItems)
    {
        this.termLinks = termLinks;

        numTerms = termLinks.length;
        numInactiveTerms = termLinks.length;
        isActive = new boolean[termLinks.length];
        termPartition = new int[termLinks.length];
        positionOfTermInPartition = new int[termLinks.length];
        observedGenes = new boolean[numItems];
        activeHiddenGenes = new int[numItems];

        for (int i=0; i < termLinks.length; i++)
        {
            termPartition[i] = i;
            positionOfTermInPartition[i] = i;
        }
    }

    /**
     * Switch the state of a given term identified by its integer id.
     *
     * @param toSwitch the id of the term to switch
     */
    public void switchState(int toSwitch)
    {
        int [] geneIDs = termLinks[toSwitch];

        isActive[toSwitch] = !isActive[toSwitch];
        if (isActive[toSwitch])
        {
            /* A term was added, activate/deactivate genes */
            for (int gid : geneIDs)
            {
                if (activeHiddenGenes[gid] == 0)
                {
                    activeHiddenGenes[gid] = 1;
                    hiddenGeneActivated(gid);
                } else
                {
                    activeHiddenGenes[gid]++;
                }
            }

            /* Move the added set from the 0 partition to the 1 partition (it essentially becomes the
             * new first element of the 1 element, while the last 0 element gets the original position
             * of the added set) */
            numInactiveTerms--;
            if (numInactiveTerms != 0)
            {
                int pos = positionOfTermInPartition[toSwitch];
                int e0 = termPartition[numInactiveTerms];

                /* Move last element in the partition to left */
                termPartition[pos] = e0;
                positionOfTermInPartition[e0] = pos;
                /* Let be the newly added term the first in the partition */
                termPartition[numInactiveTerms] = toSwitch;
                positionOfTermInPartition[toSwitch] = numInactiveTerms;
            }
        } else
        {
            /* Update hiddenActiveGenes */
            for (int gid : geneIDs)
            {
                if (activeHiddenGenes[gid] == 1)
                {
                    activeHiddenGenes[gid] = 0;
                    hiddenGeneDeactivated(gid);
                } else
                {
                    activeHiddenGenes[gid]--;
                }
            }

            /* Converse of above. Here the removed set, which belonged to the 1 partition,
             * is moved at the end of the 0 partition while the element at that place is
             * pushed to the original position of the removed element. */
            if (numInactiveTerms != (isActive.length - 1))
            {
                int pos = positionOfTermInPartition[toSwitch];
                int b1 = termPartition[numInactiveTerms];
                termPartition[pos] = b1;
                positionOfTermInPartition[b1] = pos;
                termPartition[numInactiveTerms] = toSwitch;
                positionOfTermInPartition[toSwitch] = numInactiveTerms;
            }
            numInactiveTerms++;

        }
    }

    /**
     * The given gene is now activated
     *
     * @param gid
     */
    public abstract void hiddenGeneActivated(int gid);

    /**
     * The given gene is now deactivated.
     *
     * @param gid
     */
    public abstract void hiddenGeneDeactivated(int gid);
}
