package org.monarchinitiative.phenol.stats.mtc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.PValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Values were calculated in R
 */
public class SidakTest {

  private static final double [] pvals = {.000024,.00296, .0031542, .0042578, .0068728, .0094186, .025553, .0412532, .0522945, .0674455,
    .1566575, .2703633, .2861805, .2884618, .3102804 };
  private static final double [] expected_adj_pvals = {0.0003599395,0.0434917280, 0.0462825016, 0.0619981462, 0.0982769960,
    0.1323340881, 0.3217757040, 0.4684318991, 0.5532124982, 0.6491565835, 0.9223645981, 0.9911569912,
    0.9936345939, 0.9939330095, 0.9961973311 };

  private static List<PValue> pvalList;

  private static final double EPSILON = 0.0001;

  @BeforeAll
  static void init() {
    pvalList = new ArrayList<>();
    for (int i = 0; i<pvals.length; i++) {
      String id = String.format("FAKE:%d", i);
      TermId tid = TermId.of(id);
      PValue pv = new PValue(tid, pvals[i]);
      pvalList.add(pv);
    }
    MultipleTestingCorrection sidak = new Sidak();
    sidak.adjustPvals(pvalList);
  }

  @Test
  void testConstructionOfList() {
    assertEquals(pvals.length, pvalList.size());
  }

  @Test
  void testCorrectionOfPvals() {
    for (int i = 0; i<pvalList.size(); i++) {
      assertEquals(expected_adj_pvals[i], pvalList.get(i).getAdjustedPValue(), EPSILON);
    }
  }



}
