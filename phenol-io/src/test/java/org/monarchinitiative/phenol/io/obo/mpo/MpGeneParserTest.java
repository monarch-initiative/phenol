package org.monarchinitiative.phenol.io.obo.mpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.formats.mpo.MpGeneModel;
import org.monarchinitiative.phenol.formats.mpo.MpMarkerType;
import org.monarchinitiative.phenol.formats.mpo.MpSimpleModel;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class MpGeneParserTest {

  private static Map<TermId, MpGene> mpgenemap;
  private static String MGI_genePhenoPath;

  @BeforeAll
  static void setup() throws IOException, PhenolException {
    ClassLoader classLoader = MpGeneParserTest.class.getClassLoader();
    URL url = classLoader.getResource("mgi/MRK_List2.rpt.excerpt");
    if (url == null) {
      throw new IOException("Cannot find MRK_List2.rpt.excerpt ");
    }
    String markerFile = url.getFile();
    url = classLoader.getResource("mgi/mp_head.obo");
    if (url == null) {
      throw new IOException("Cannot find mp_head.obo");
    }
    String ontologyPath = url.getFile();
    url = classLoader.getResource("mgi/MGI_GenePheno.rpt.excerpt");
    if (url == null) {
      throw new IOException("Cannot find MGI_GenePheno.rpt.excerpt.obo");
    }
    MGI_genePhenoPath = url.getFile();
    MpGeneParser gmp = new MpGeneParser(markerFile, MGI_genePhenoPath, ontologyPath);
    mpgenemap = gmp.parseMarkers();
  }

  /**
   * Look up a gene, a transgene, a nonsense MGI ID to make sure you get what you expect.
   * // MGI:1341858 is a 03B03F BAC/YAC
   */
  @Test
  void parseMarkersTest() {
    TermId tid = TermId.of("MGI:1341858");
    MpGene g = mpgenemap.get(tid);
    assertNotNull(g);
    assertEquals("03B03F", g.getGeneSymbol());
    assertSame(MpMarkerType.BAC_YAC_END, g.getMarkerType());
    assertEquals(tid, g.getMgiGeneId());
  }

  /**
   * We have two models for RB1. Both of them have an annotation for MP:0000961
   * and then they each have other (disjoint) annotations.
   *
   * @throws PhenolException upon parsae problems
   */

  @Test
  void testMergeGetsCorrectGeneId() throws PhenolException {
    MpAnnotationParser parser = new MpAnnotationParser(MGI_genePhenoPath);
    Map<TermId, MpSimpleModel> modelmap = parser.getGenotypeAccessionToMpModelMap();
    List<MpSimpleModel> rb1Models = new ArrayList<>();
    TermId rb1Id = TermId.of("MGI:97874");
    for (MpSimpleModel mod : modelmap.values()) {
      //System.out.println(mod);
      if (rb1Id.equals(mod.getMarkerId())) {
        rb1Models.add(mod);
      }
    }
    // there are two Rb1 models
    assertEquals(2, rb1Models.size());
    MpGeneModel genemod = new MpGeneModel(rb1Id, rb1Models);
    assertEquals(genemod.getMarkerId(),rb1Id);
  }


}
