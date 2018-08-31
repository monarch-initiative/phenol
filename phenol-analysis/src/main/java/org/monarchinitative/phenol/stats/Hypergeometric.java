package org.monarchinitative.phenol.stats;


import java.util.Vector;

/**
 * Class with static methods to calculate probabilities according to the
 * hypergeometric distribution.
 *
 * @author Peter N. Robinson, Sebastian Bauer
 */

public class Hypergeometric
{

    /**
     * This Vector contains log factorials for each index value and acts as a
     * cache.
     */
    private Vector<Double> lfactorial;

    /**
     * <P>
     * For the hypergeometric distribution note the following.
     * </P>
     * <UL>
     * <LI>We set up the problem as sampling a set of genes (study genes, for
     * instance, the set of upregulated genes in some microarray experiment)
     * from a larger set of genes (say, the set of all genes of a species). The
     * sampling is done without replacement. </LI>
     * <LI>For each GO term, we can conceive of the population as being divided
     * into genes annotated to this term and those genes that are not annotated
     * to the term.</LI>
     * <LI>The probability of having a certain number of terms annotated to the
     * term in the study set can then be calculated by the hypergeometric
     * distribution. See GeneMerge by Castillo-Davis et al (Bioinformatics).
     * </LI>
     * <LI>To do this, we need to divide the population into two groups, genes
     * with and without annotation. The arguments to the function supply us with
     * <B>n</B>, the total number of genes in the population group, and <B>p
     * </B>, the proportion of genes with annotation to the term in question.
     * We can then calculate the number of genes in the population annotated to
     * the term by <B>round(n*p)</B>, and the number of genes not annotated to
     * the term by <B>round(n*(1-p))</B>.</LI>
     * </UL>
     *
     *
     * @param n
     *            Number of population genes
     * @param p
     *            Proportion of population genes
     * @param k
     *            Number of study genes
     * @param r
     *            Number of study genes in group
     */
    public double phypergeometric(int n, double p, int k, int r)
    {
        /*
         * Study group cannot be larger than population. If this happens there
         * is probably something wrong with the input data, but returning 1.0
         * prevents confusing and wrong output.
         */
        if (k >= n)
            return 1.0;

        if (r < 1)
        {
            return 1.0; // Not valid for r < 2, less than 2 study genes.
        }

        double q = 1.0 - p;
        int np = (int) Math.round(n * p); // Round to nearest int
        int nq = (int) Math.round(n * q);

        double log_n_choose_k = lNchooseK(n, k);
        int top = k;
        if (np < k)
        {
            top = np;
        }

        double lfoo = lNchooseK(np, top) + lNchooseK(nq, k - top);

        double sum = 0.0;

        for (int i = top; i >= r; --i)
        {
            sum += Math.exp(lfoo - log_n_choose_k);
            if (i > r)
            {
                lfoo = lfoo
                        + Math.log((double) i / (double) (np - i + 1))
                        + Math.log((double) (nq - k + i)
                        / (double) (k - i + 1));
            }
        }
        return sum;

    }

    /**
     * Calculates the probability that if you draw n balls from
     * an urn without replacement containing N balls where M among
     * them are white (and so N-M are black) you will get x white
     * balls.
     *
     * @param x number of white balls drawn without replacement
     * @param N number of balls in the urn
     * @param M number of white balls in the urn
     * @param n number of balls drawn from the urn
     *
     * @return the probability
     */
    public double dhyper(int x, int N, int M, int n)
    {
        /* It is not possible to draw more white balls
         * from an urn containing M white balls. Hence
         * the probability is 0.
         */
        if (x > M) return 0;

        /* Of course it is also not possible to draw
         * more white balls than the number of drawings.
         * The probability is 0.
         */
        if (x > n) return 0;

        /* Last but not least, it is also not possible
         * to draw more black balls than there are within
         * the urn.
         */
        if (n - x > N - M) return 0;

        return Math.exp(lNchooseK(M,x)+lNchooseK(N-M,n-x)-lNchooseK(N,n));
    }

    /**
     * Calculates P(X &gt; x) where X is the hypergeometric distribution
     * with indices N,M,n. If lowerTail is set to true, then P(X &lt;= x)
     * is calculated.
     *
     * @param x number of white balls drawn without replacement
     * @param N number of balls in the urn
     * @param M number of white balls in the urn
     * @param n number of balls drawn from the urn
     * @param lowerTail defines if the lower tail should be calculated, i.e., if the
     *      parameter is set to true then P(X &lt;= x) is calculated, otherwise P(X &gt; x) is
     *      calculated.
     * @return the probability
     */
    public double phyper(int x, int N, int M, int n, boolean lowerTail)
    {
        int i;
        int up;
        double p;

        up = Math.min(n,M);
        p = 0;

        if (x < up / 2)
        {
            for (i = x; i >= 0; i--)
                p += dhyper(i,N,M,n);

            if (lowerTail) return p;
            else return 1 - p;
        } else
        {
            for (i = x+1;i <= up;i++)
                p += dhyper(i,N,M,n);

            if (lowerTail) return 1 - p;
            else return p;
        }
    }

    public double lNchooseK(int n, int k)
    {
        double ans;
        ans = logfact(n) - logfact(k) - logfact(n - k);
        return ans;
    }

    /**
     * return the log factorial of i. Use a cache to avoid repeatedly
     * calculating this. If we have a cache miss, fill up all values from the
     * last valid cache value to the value we currently need.
     */
    public double logfact(int i)
    {
        /*
         * Make sure value is already in lfactorial. If not, calculate all
         * values up to that for i
         */
        if (i > (lfactorial.size() - 1))
        {
            for (int j = lfactorial.size(); j <= i; j++)
            {
                double lf = lfactorial.get(j - 1).doubleValue()
                        + Math.log(j);
                lfactorial.add(j, new Double(lf));
            }
        }

        return lfactorial.get(i).doubleValue();
    }

    /**
     * Initialize the object lfactorial, which will act as a cache for log
     * factorial calculations.
     */
    public Hypergeometric()
    {

        lfactorial = new Vector<Double>();
        lfactorial.add(0, new Double(0.0)); /* 0! = 1, therefore let log(0)=0 */
        lfactorial.add(1, new Double(0.0));

    }

}

