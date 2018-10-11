package org.monarchinitiative.phenol.io.obo.mpo;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.formats.mpo.MpGeneModel;
import org.monarchinitiative.phenol.formats.mpo.MpMarkerType;
import org.monarchinitiative.phenol.formats.mpo.MpSimpleModel;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * TODO Update tests!
 */
public class MpGeneParserTest {

  private static Map<TermId, MpGene> mpgenemap;
  private static String MGI_genePhenoPath;

  @BeforeClass
  public static void setup() throws IOException,PhenolException {
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
    MpGeneParser gmp = new MpGeneParser(markerFile,MGI_genePhenoPath,ontologyPath);
    mpgenemap = gmp.parseMarkers();
  }

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Look up a gene, a transgene, a nonsense MGI ID to make sure you get what you expect.
   * // MGI:1341858 is a 03B03F BAC/YAC
   */
  @Test
  public void parseMarkersTest() {
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

  /**
   * We have two models for RB1. Both of them have an annotation for MP:0000961
   * and then they each have other (disjoint) annotations.
   * @throws PhenolException
   */
  @Test @Ignore
    public void testMerge() throws PhenolException,FileNotFoundException {
    MpAnnotationParser parser = new MpAnnotationParser(MGI_genePhenoPath);
    Map<TermId, MpSimpleModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    List<MpSimpleModel> rb1Models=new ArrayList<>();
    TermId rb1Id=TermId.constructWithPrefix("MGI:97874");
    for (MpSimpleModel mod: modelmap.values()){
      //System.out.println(mod);
      if (rb1Id.equals(mod.getMarkerId())){
        rb1Models.add(mod);
      }
    }
    // there are two Rb1 models
    assertEquals(2,rb1Models.size());
    // GET FULL ONTOLOGY NOW USING LOCAL TEST
    // TODO -- TAILOR THE TOY TEST ONTOLOGY SO THAT THIS WORKS
    String localMpPath="/Users/peterrobinson/Documents/data/mgi/mp.obo";
    MpOboParser oboparser = new MpOboParser(localMpPath);
    Ontology ontology = oboparser.parse();


    MpGeneModel genemod = new MpGeneModel(rb1Id,ontology,rb1Models);
  }



}
