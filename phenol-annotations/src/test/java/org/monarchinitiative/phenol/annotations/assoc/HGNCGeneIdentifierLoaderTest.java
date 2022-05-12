package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class HGNCGeneIdentifierLoaderTest {

  private static final Path HGNC_HEAD20 = TestBase.TEST_BASE.resolve("hgnc_complete_set.head10_and_special.tsv");

  private HGNCGeneIdentifierLoader instance;

  @BeforeEach
  public void setUp() {
    instance = new HGNCGeneIdentifierLoader();
  }

  @Test
  public void load() throws Exception {
    GeneIdentifiers identifiers = instance.load(HGNC_HEAD20);

    assertThat(Math.toIntExact(identifiers.stream().count()), equalTo(9));

    List<TermId> ids = identifiers.stream()
      .map(GeneIdentifier::id)
      .collect(Collectors.toList());
    assertThat(ids, containsInRelativeOrder(tid("NCBIGene:1"), tid("NCBIGene:503538"), tid("NCBIGene:29974"), tid("NCBIGene:2"), tid("NCBIGene:144571"), tid("NCBIGene:144568"), tid("NCBIGene:100874108"), tid("NCBIGene:106478979"), tid("NCBIGene:3")));

    List<String> symbols = identifiers.stream()
      .map(GeneIdentifier::symbol)
      .collect(Collectors.toList());
    assertThat(symbols, containsInRelativeOrder("A1BG", "A1BG-AS1", "A1CF", "A2M", "A2M-AS1", "A2ML1", "A2ML1-AS1", "A2ML1-AS2", "A2MP1"));
  }

  private static TermId tid(String value) {
    return TermId.of(value);
  }
}
