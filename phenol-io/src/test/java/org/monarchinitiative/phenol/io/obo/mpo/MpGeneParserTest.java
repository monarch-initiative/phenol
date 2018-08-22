package org.monarchinitiative.phenol.io.obo.mpo;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.formats.mpo.MpMarkerType;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

public class MpGeneParserTest {

  private static Map<TermId, MpGene> mpgenemap;


  @BeforeClass
  public static void setup() throws IOException,PhenolException {
    ClassLoader classLoader = MpGeneParserTest.class.getClassLoader();
    URL url = classLoader.getResource("mgi/MRK_List2.rpt.excerpt");
    if (url == null) {
      throw new IOException("Cannot find MRK_List2.rpt.excerpt ");
    }
    MpGeneParser gmp = new MpGeneParser(url.getFile());
    mpgenemap = gmp.parseMarkers();
  }

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Look up a gene, a transgene, a nonsense MGI ID to make sure you get what you expect.
   * // MGI:1341858 is a 03B03F BAC/YAC
   */
  @Test
  public void parseMarkersTest() throws PhenolException {
    TermId tid = TermId.constructWithPrefix("MGI:1341858");
    MpGene g = mpgenemap.get(tid);
    assertNotNull(g);
    assertEquals("03B03F", g.getGeneSymbol());
    assertSame(MpMarkerType.BAC_YAC_END, g.getMarkerType());
    assertEquals(tid,g.getMgiGeneId());
//    for (MpGene mg : mpgenemap.values()) {
//      System.out.println(mg.toString());
//    }

//    assertEquals(g.getMgiId(), "MGI:97874");
//    assertEquals(g.getGeneSymbol(), "Rb1");
//    assertEquals(g.getMarkerType(), "Gene");
//    ImmutableGene t = genes.findGene("MGI:3623968");
//    assertNotNull(t);
//    assertEquals(t.getMgiId(), "MGI:3623968");
//    assertEquals(t.getGeneSymbol(), "Tg(CD2-CD4,HLA-DQA1,HLA-DQB1)1Ell");
//    assertTrue(t.isTransgene());
//    thrown.expect(DimorphDataException.class);
//    thrown.expectMessage("Could not find matching gene object for accession id ");
//    ImmutableGene n = genes.findGene("MGI:333");
  }



}
