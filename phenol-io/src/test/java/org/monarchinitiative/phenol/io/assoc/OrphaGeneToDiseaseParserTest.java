package org.monarchinitiative.phenol.io.assoc;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;

public class OrphaGeneToDiseaseParserTest {

  // Get XML file from
  //http://www.orphadata.org/cgi-bin/inc/product6.inc.php

  @ClassRule
  public static TemporaryFolder tmpFolder = new TemporaryFolder();

  private static File orphaProduct6;

  @BeforeClass
  public static void init() throws IOException, PhenolException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(

    //orphaProduct6 = tmpFolder.newFile("orphaProduct6");
    //ResourceUtils.copyResourceToFile("/home/robinp/data/orpha/en_product6.xml", orphaProduct6);
orphaProduct6=new File("/home/robinp/data/orpha/en_product6.xml");



  }



  @Test
  public void testContructor() {
    OrphaGeneToDiseaseParser parser = new OrphaGeneToDiseaseParser(orphaProduct6);
  }



}
