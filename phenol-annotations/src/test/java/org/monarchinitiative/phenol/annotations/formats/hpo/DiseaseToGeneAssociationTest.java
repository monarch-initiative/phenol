package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DiseaseToGeneAssociationTest {

  @Test
  public void base() {
    TermId diseaseId = TermId.of("OMIM:123456");
    List<GeneToAssociation> associations = List.of(
      GeneToAssociation.of(GeneIdentifier.of(TermId.of("NCBIGene:2200"), "FBN1"), AssociationType.MENDELIAN),
      GeneToAssociation.of(GeneIdentifier.of(TermId.of("NCBIGene:2201"), "FBN_XYZ"), AssociationType.MENDELIAN),
      GeneToAssociation.of(GeneIdentifier.of(TermId.of("NCBIGene:2201"), "FBN_XYZ"), AssociationType.POLYGENIC));

    DiseaseToGeneAssociation diseaseToGeneAssociation = DiseaseToGeneAssociation.of(diseaseId, associations);

    assertThat(diseaseToGeneAssociation.diseaseId(), equalTo(diseaseId));
    assertThat(diseaseToGeneAssociation.associations(), hasItems(associations.toArray(GeneToAssociation[]::new)));
  }

  @Test
  public void noDuplicateGeneIdentifiersIfMultipleAssociationTypesForAGene() {
    DiseaseToGeneAssociation diseaseToGeneAssociation = DiseaseToGeneAssociation.of(
      TermId.of("OMIM:123456"),
      List.of(
        GeneToAssociation.of(GeneIdentifier.of(TermId.of("NCBIGene:2200"), "FBN1"), AssociationType.MENDELIAN),
        GeneToAssociation.of(GeneIdentifier.of(TermId.of("NCBIGene:2201"), "FBN_XYZ"), AssociationType.MENDELIAN),
        GeneToAssociation.of(GeneIdentifier.of(TermId.of("NCBIGene:2201"), "FBN_XYZ"), AssociationType.POLYGENIC)));


    List<GeneIdentifier> geneIdentifiers = diseaseToGeneAssociation.geneIdentifiers();

    assertThat(geneIdentifiers, hasSize(2));
    assertThat(diseaseToGeneAssociation.geneIdentifiers(), hasItems(
      GeneIdentifier.of(TermId.of("NCBIGene:2200"), "FBN1"),
      GeneIdentifier.of(TermId.of("NCBIGene:2201"), "FBN_XYZ")
    ));
  }
}
