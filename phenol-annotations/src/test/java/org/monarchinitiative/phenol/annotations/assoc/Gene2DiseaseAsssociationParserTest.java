package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Gene2DiseaseAsssociationParserTest {
  final Path mim2geneMedgenPath = Paths.get("src/test/resources/mim2gene_medgen.excerpt");
  final Path homoSapiensGeneInfoPath = Paths.get("src/test/resources/Homo_sapiens.gene_info.excerpt.gz");
  final Path orphanetPath = Paths.get("src/test/resources/orphanet_disease2gene_en_product6_head.xml");
  private final Gene2DiseaseAssociationParser parser = new
    Gene2DiseaseAssociationParser(homoSapiensGeneInfoPath.toFile(),
                                  mim2geneMedgenPath.toFile(),
                                  orphanetPath.toFile());

  /*
   * For this test, we extracted all mitochondrial genes associated with OMIM:540000 (MELAS syndrome) using
   * a Python script and enter them by hand into the BiMap builder.
   */

  @Test
  void testMim2GeneParser() throws IOException {
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
