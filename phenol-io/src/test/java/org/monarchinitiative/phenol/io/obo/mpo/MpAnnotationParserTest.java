package org.monarchinitiative.phenol.io.obo.mpo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpModel;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MpAnnotationParserTest {

  private static String genePhenoPath;

  private static String phenoSexPath;

  @BeforeClass
  public static void setup() throws IOException,PhenolException {
    ClassLoader classLoader = MpGeneParserTest.class.getClassLoader();
    URL url = classLoader.getResource("mgi/MGI_GenePheno.rpt.excerpt");
    if (url == null) {
      throw new IOException("MGI_GenePheno.rpt.excerpt");
    }
    genePhenoPath=url.getPath();
    url = classLoader.getResource("mgi/MGI_Pheno_Sex.rpt.excerpt");
    if (url == null) {
      throw new IOException("MGI_Pheno_Sex.rpt.excerpt");
    }
    phenoSexPath=url.getPath();
  }



  @Test
  public void testCtr() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    assertEquals(2,modelmap.size());
  }

  @Test
  public void testSexSpecificParser() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath,phenoSexPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.constructWithPrefix("MGI:2167486");
    MpModel model = modelmap.get(kit);
    assertNotNull(model);
    System.out.println(model);
    assertTrue(model.hasSexSpecificAnnotation());
  }


}
