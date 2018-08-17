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

public class MpAnnotationParserTest {

  private static String genePhenoPath;

  @BeforeClass
  public static void setup() throws IOException,PhenolException {
    ClassLoader classLoader = MpGeneParserTest.class.getClassLoader();
    URL url = classLoader.getResource("mgi/MGI_GenePheno.rpt.excerpt");
    if (url == null) {
      throw new IOException("MGI_GenePheno.rpt.excerpt");
    }
    genePhenoPath=url.getPath();
  }

  @Test
  public void name() {
  }

  @Test
  public void testCtr() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    assertEquals(1,modelmap.size());
  }


}
