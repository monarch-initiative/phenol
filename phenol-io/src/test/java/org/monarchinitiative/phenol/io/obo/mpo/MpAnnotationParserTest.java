package org.monarchinitiative.phenol.io.obo.mpo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;

import java.io.IOException;
import java.net.URL;

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
  public void testCtr() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);

  }


}
