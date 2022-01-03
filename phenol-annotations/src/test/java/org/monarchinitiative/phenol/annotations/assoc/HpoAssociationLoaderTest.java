package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociation;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAssociationData;
import org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HpoAssociationLoaderTest {

  private static HpoAssociationData ASSOCIATION_DATA;

  private static GeneIdentifier makeGeneId(String termId, String symbol) {
    return GeneIdentifier.of(TermId.of(termId), symbol);
  }

  @BeforeAll
  public static void setUp() throws Exception {
    File hpoJson = new File(HpoAssociationLoaderTest.class.getResource("/hpo_toy.json").toURI());
    Ontology hpo = OntologyLoader.loadOntology(hpoJson);

    Path homoSapiensGeneInfo = Path.of(HpoAssociationLoaderTest.class.getResource("/Homo_sapiens.gene_info.excerpt.gz").toURI());
    Path mimToMedgen = Path.of(HpoAssociationLoaderTest.class.getResource("/mim2gene_medgen.excerpt").toURI());
    Path orphaToGene = Path.of(HpoAssociationLoaderTest.class.getResource("/orphanet_disease2gene_en_product6_head.xml").toURI());
    Path hpoAssociations = Path.of(HpoAssociationLoaderTest.class.getResource("/annotations/phenotype.excerpt.hpoa").toURI());
    Set<DiseaseDatabase> diseaseDatabases = Set.of(DiseaseDatabase.OMIM, DiseaseDatabase.ORPHANET);

    ASSOCIATION_DATA = HpoAssociationLoader.loadHpoAssociationData(hpo, homoSapiensGeneInfo, mimToMedgen, orphaToGene, hpoAssociations, diseaseDatabases);
  }

  @Test
  public void testNotNull() {
    assertThat(ASSOCIATION_DATA, is(notNullValue()));
  }

  /**
   * TBX5 is the only gene involved with Holt-Oram syndrome (OMIM:142900), and
   * TBX5 is not associated with other diseases. TBX5 has the EntrezGene id 6910
   */
  @Test
  public void testHoltOram() {
    TermId holtOramSyndromeId = TermId.of("OMIM:142900");
    GeneIdentifier tbx5 = GeneIdentifier.of(TermId.of("NCBIGene:6910"), "TBX5");

    DiseaseToGeneAssociation holtOramAssociations = ASSOCIATION_DATA.associationsByDiseaseId().get(holtOramSyndromeId);

    assertThat(holtOramAssociations.diseaseId(), equalTo(holtOramSyndromeId));
    assertThat(holtOramAssociations.associations(), hasItem(GeneToAssociation.of(tbx5, AssociationType.MENDELIAN)));

    Collection<TermId> diseasesAssociatedWithTbx5 = ASSOCIATION_DATA.geneToDiseases().get(tbx5.id());
    assertThat(diseasesAssociatedWithTbx5, hasSize(1));
    assertThat(diseasesAssociatedWithTbx5, hasItem(holtOramSyndromeId));
  }

  /**
   * There are several disease associated with FBN1.
   * 1. Acromicric dysplasia 	OMIM:102370
   * 2. Ectopia lentis, familial 	OMIM:129600
   * 3. Geleophysic dysplasia 2 	OMIM:614185
   * 4. Marfan lipodystrophy syndrome 	OMIM:616914
   * 5. Marfan syndrome 	OMIM:154700
   * 6. MASS syndrome 	OMIM:604308 		3
   * 7. Stiff skin syndrome 	OMIM:184900
   * 8. Weill-Marchesani syndrome 2, dominant 	OMIM:608328
   */
  @Test
  public void testFbn1() {
    TermId fbn1 = TermId.of("NCBIGene:2200");
    Collection<TermId> diseasesAssociatedWithFbn1 = ASSOCIATION_DATA.geneToDiseases().get(fbn1);
    assertThat(diseasesAssociatedWithFbn1, hasSize(8));
    TermId[] expectedDiseases = new TermId[]{
      TermId.of("OMIM:102370"),
      TermId.of("OMIM:129600"),
      TermId.of("OMIM:614185"),
      TermId.of("OMIM:616914"),
      TermId.of("OMIM:154700"),
      TermId.of("OMIM:604308"),
      TermId.of("OMIM:184900"),
      TermId.of("OMIM:608328")
    };
    assertThat(diseasesAssociatedWithFbn1, hasItems(expectedDiseases));
  }

  @Test
  public void testFamilialHypercholesterolemiaGenes() {
    TermId familialHypercholesterolemia = TermId.of("OMIM:143890");

    Collection<GeneIdentifier> genesAssociatedWithFH = ASSOCIATION_DATA.diseaseToGenes().get(familialHypercholesterolemia);

    assertThat(genesAssociatedWithFH, hasSize(7));
    assertThat(genesAssociatedWithFH, hasItems(
      makeGeneId("NCBIGene:10842", "PPP1R17"),
      makeGeneId("NCBIGene:336", "APOA2"),
      makeGeneId("NCBIGene:19", "ABCA1"),
      makeGeneId("NCBIGene:2053", "EPHX2"),
      makeGeneId("NCBIGene:3949", "LDLR"),
      makeGeneId("NCBIGene:2690", "GHR"),
      makeGeneId("NCBIGene:3700", "ITIH4")
    ));
  }

  @Test
  public void testDiseaseToGene() {
    Collection<GeneIdentifier> geneIdentifiers = ASSOCIATION_DATA.diseaseToGenes().get(TermId.of("OMIM:143890"));
    assertThat(geneIdentifiers, hasSize(7));
  }

  @Test
  public void testGeneIdToGeneSymbol() {
    Map<TermId, String> geneIdToSymbol = ASSOCIATION_DATA.geneIdToSymbol();
    assertThat(geneIdToSymbol, hasEntry(TermId.of("NCBIGene:2690"), "GHR"));
    assertThat(geneIdToSymbol, hasEntry(TermId.of("NCBIGene:2200"), "FBN1"));
  }
}
