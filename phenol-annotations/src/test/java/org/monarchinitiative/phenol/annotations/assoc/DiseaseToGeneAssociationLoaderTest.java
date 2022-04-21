package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociations;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiseaseToGeneAssociationLoaderTest {

  /*
   * For this test, we extracted all mitochondrial genes associated with OMIM:540000 (MELAS syndrome) using
   * a Python script and enter them by hand into the BiMap builder.
   */

  @Test
  public void testMim2GeneParser() throws Exception {
    Path homoSapiensGeneInfo = Path.of(DiseaseToGeneAssociationLoaderTest.class.getResource("/Homo_sapiens.gene_info.excerpt.gz").toURI());
    Path mim2gene = Path.of(DiseaseToGeneAssociationLoaderTest.class.getResource("/mim2gene_medgen.excerpt").toURI());
    Path orphanet = Path.of(DiseaseToGeneAssociationLoaderTest.class.getResource("/orphanet_disease2gene_en_product6_head.xml").toURI());

    GeneIdentifiers geneIdentifiers = HumanGeneInfoLoader.loadGeneIdentifiers(homoSapiensGeneInfo);

    // 16 genes associated with OMIM:540000 are in the mim2gene_medgen.excerpt file
    // TRNC,COX1,COX2,COX3,CYTB,ND1,ND5,ND6,TRNF,TRNK,TRNL1,TRNQ,TRNS1,TRNS2,TRNV,TRNW
    // All of these genes were put into the gene info excerpt
    DiseaseToGeneAssociations associations = DiseaseToGeneAssociationLoader.loadDiseaseToGeneAssociations(mim2gene, orphanet, geneIdentifiers);
    Map<TermId, Collection<GeneToAssociation>> diseaseIdToGeneAssociations = associations.diseaseIdToGeneAssociations();
    TermId melas = TermId.of("OMIM:540000");
    Collection<GeneToAssociation> g2a_collection = diseaseIdToGeneAssociations.get(melas);
    assertEquals(16, g2a_collection.size());
    assertThat(g2a_collection, hasSize(16));
  }


}
