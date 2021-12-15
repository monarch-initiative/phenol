package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Gene2DiseaseAsssociationParserTest {

  private static Gene2DiseaseAssociationParser parser;

  @BeforeAll
  private static void init() throws IOException {
    final String mim2genePath = "mim2gene_medgen.excerpt";
    ClassLoader classLoader = Gene2DiseaseAsssociationParserTest.class.getClassLoader();
    URL mim2geneURL = classLoader.getResource(mim2genePath);
    if (mim2geneURL == null) {
      throw new IOException("Could not find mim2gene_medgen at " + mim2genePath);
    }
    final String homoSapiensGeneInfoPath = "Homo_sapiens.gene_info.excerpt.gz";
    URL homoSapiensGeneInfoURL = classLoader.getResource(homoSapiensGeneInfoPath);
    if (homoSapiensGeneInfoURL == null) {
      throw new IOException("Could not find Homo_sapiens.gene_info.excerpt.gz at " + homoSapiensGeneInfoPath);
    }
    final String orphanetPath = "orphanet_disease2gene_en_product6_head.xml";
    URL orphanetURL = classLoader.getResource(orphanetPath);
    if (orphanetURL == null) {
      throw new IOException("Could not find orphanet_disease2gene_en_product6_head.xml at " + orphanetPath);
    }
    parser = new
            Gene2DiseaseAssociationParser(homoSapiensGeneInfoURL.getFile(),
            mim2geneURL.getFile(),
            orphanetURL.getFile());
  }

  /*
   * For this test, we extracted all mitochondrial genes associated with OMIM:540000 (MELAS syndrome) using
   * a Python script and enter them by hand into the BiMap builder.
   */

  @Test
  void testMim2GeneParser() {
    // 16 genes associated with OMIM:540000 are in the mim2gene_medgen.excerpt file
    // TRNC,COX1,COX2,COX3,CYTB,ND1,ND5,ND6,TRNF,TRNK,TRNL1,TRNQ,TRNS1,TRNS2,TRNV,TRNW
    // All of these genes were put into the gene info excerpt
    Multimap<TermId, GeneToAssociation> assocs = parser.getAssociationMap();
    TermId melas = TermId.of("OMIM:540000");
    Collection<GeneToAssociation> g2a_collection = assocs.get(melas);
    assertEquals(16, g2a_collection.size());
    assertThat(g2a_collection, hasSize(16));
  }


}
