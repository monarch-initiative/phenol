package org.monarchinitiative.phenol.stats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HypergeometricTest {

  private static final Hypergeometric hypergeometric = new Hypergeometric();
  private static final double EPSILON = 0.00001;



  @Test
  void test_factorial() {
    // factorial(4)=24
    // log(24) = 3.178054
    double expected = 3.178054;
    assertEquals(expected, hypergeometric.logfact(4), EPSILON);
  }


  /**
   * choose(7,2)
   * [1] 21
   * log(21)
   * [1] 3.044522
   */
  @Test
  void test_lchoose() {
    double expected = 3.044522;
    assertEquals(expected, hypergeometric.lNchooseK(7, 2), EPSILON);
  }




  /**
   * x<-14
   * m<-70
   * n<-30
   * k<-20
   * dhyper(x=x,m=m,n=n,k=k)
   * [1] 0.2140911
   * y<- choose(m,x)*choose(n,k-x)/choose(m+n,k)
   * y
   * [1] 0.2140911
   * Note that the interface of our function is like this:
   * lNchooseK(M, x) + lNchooseK(N - M, n - x) - lNchooseK(N, n)
   * Therefore, we need to call it as
   * hypergeometric.dhyper(x, m+n, m, 20);
   */
  @Test
  void test_dhyper() {
    int x = 14; // # number of white balls drawn from urn
    int m = 70; // # white balls in urn
    int n = 30; // # number of other balls
    int k = 20; // # number of balls drawn from urn
    int N = m+n; // # total number of balls in urn
    double result = hypergeometric.dhyper(x, N, m, k);
    assertEquals(0.2140911, result, EPSILON);
  }

  /**
   * Not an interesting analysis for GO but just to test the code
   * m <- 60 # 60 genes in genome
   * n <- 13 # study set
   * m_t <- 5 # 5 genes annotated to t in population
   * n_t <- 2 # 2 genes in study set annotated to t
   * # test for underrepresentation
   *  phyper(n_t, n, m-n, m_t, lower.tail= TRUE)
   *  0.9370032
   */
  @Test
  void test_phyper_underrepresentation() {
    int N = 60; // # total number of genes
    int n = 13; // # study set
    int n_t = 2; // # number genes in study set annotated to t
    int m_t = 5; // # number of genes in population annotated to t
    double result = hypergeometric.phyper(n_t, N, m_t, n, true);
    assertEquals(0.9370032, result, EPSILON);
  }

  /**
   * m <- 60 # 60 genes in genome
   * n <- 13 # study set
   * m_t <- 5 # 5 genes annotated to t in population
   * n_t <- 2 # 2 genes in study set annotated to t
   * #test for overrepresentation
   * phyper(n_t-1, n, m-n, m_t, lower.tail=FALSE)
   * 0.2945756
   * Why Overlap-1 when testing over-representation?
   *
   * Because when we set parameter lower.tail=TRUE in
   * phyper the interpretation of the p-value is the probability
   * of observing equal or more depletion (P[X ≤ x]) when null hypothesis
   * is true. So, for instance p-value <= 0.05 we reject the null hypothesis
   * and assume a significant depletion.
   *
   * When we set parameter lower.tail=FALSE in phyper the interpretation
   * of the p-value is P[X > x] . But what we need to test is the null hypothesis P[X ≥ x],
   * so we subtract x by 1. When p-value <= 0.05, we can reject the null hypothesis and assume
   * a significant enrichment, that is, there is a small probablity of seeing an equal or bigger overlap than x.
   */
  @Test
  void test_phyper_overrepresentation() {
    int N = 60; // # total number of genes
    int n = 13; // # study set
    int n_t = 2; // # number genes in study set annotated to t
    int m_t = 5; // # number of genes in population annotated to t
    double result = hypergeometric.phyper(n_t-1, N, m_t, n, false);
    assertEquals(0.2945756, result, EPSILON);
  }

  @Test
  void test_compatibility_phyper_and_phypergeometric() {
    int N = 60; // # total number of genes
    int n = 13; // # study set
    int n_t = 2; // # number genes in study set annotated to t
    int m_t = 5; // # number of genes in population annotated to t
    double phyperResult = hypergeometric.phyper(n_t-1, N, m_t, n, false);
    double phypergeometricResult = hypergeometric.phypergeometric(N, (double)m_t/N, n, n_t);
    assertEquals(phyperResult, phypergeometricResult, EPSILON);
  }

  @Test
  void test_equality_phypergeometric_overloaded() {
    int N = 60; // # total number of genes
    int n = 13; // # study set
    int n_t = 2; // # number genes in study set annotated to t
    int m_t = 5; // # number o
    double r1 = hypergeometric.phypergeometric(N, (double)m_t/N, n, n_t);
    double r2 = hypergeometric.phypergeometric(N, m_t, n, n_t);
    assertEquals(r1, r2, EPSILON);
  }


  /**
   * m <- 324
   * n <- 74
   * m_t <- 82
   * n_t <- 38
   * ph <- phyper(n_t-1, n, m-n, m_t, lower.tail=FALSE)
   * 2.253392e-08
   */
  @Test
  void testHypergeometric() {
    int pop=324;
    int popAnnot=82;
    int study=74;
    int studyAnnot=38;
    double raw_pval = hypergeometric.phypergeometric(pop, popAnnot, study, studyAnnot);
    double expected = 2.253392e-08;
    assertEquals(expected, raw_pval, EPSILON);
  }


}

