package org.monarchinitiative.phenol.annotations.formats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class GeneIdentifiersTest {

  private GeneIdentifiers identifiers;

  @BeforeEach
  public void setUp() {
    identifiers = GeneIdentifiers.of(
      List.of(
        GeneIdentifier.of(TermId.of("NCBIGene:3949"), "LDLR"),
        GeneIdentifier.of(TermId.of("NCBIGene:6834"), "SURF1"),
        GeneIdentifier.of(TermId.of("NCBIGene:2153"), "F5")
      ));
  }

  @ParameterizedTest
  @CsvSource({
    "NCBIGene:3949, LDLR",
    "NCBIGene:6834, SURF1",
    "NCBIGene:2153, F5",
  })
  public void geneIdById(TermId id, String symbol) {
    Optional<GeneIdentifier> gio = identifiers.geneIdById(id);
    assertThat(gio.isPresent(), equalTo(true));

    GeneIdentifier gi = gio.get();
    assertThat(gi.id(), equalTo(id));
    assertThat(gi.symbol(), equalTo(symbol));
  }

  @Test
  public void geneIdById_missing() {
    Optional<GeneIdentifier> gio = identifiers.geneIdById(TermId.of("NCBIGene:6835"));
    assertThat(gio.isPresent(), equalTo(false));
  }

  @ParameterizedTest
  @CsvSource({
    "NCBIGene:3949, LDLR",
    "NCBIGene:6834, SURF1",
    "NCBIGene:2153, F5",
  })
  public void geneIdBySymbol(TermId id, String symbol) {
    Optional<GeneIdentifier> gio = identifiers.geneIdBySymbol(symbol);
    assertThat(gio.isPresent(), equalTo(true));

    GeneIdentifier gi = gio.get();
    assertThat(gi.id(), equalTo(id));
    assertThat(gi.symbol(), equalTo(symbol));
  }

  @Test
  public void geneIdBySymbol_missing() {
    Optional<GeneIdentifier> gio = identifiers.geneIdBySymbol("SURF2");
    assertThat(gio.isPresent(), equalTo(false));
  }

  @Test
  public void streamAndSize() {
    List<String> symbols = identifiers.stream()
      .sequential()
      .map(GeneIdentifier::symbol)
      .collect(Collectors.toList());
    assertThat(symbols, containsInRelativeOrder("LDLR", "SURF1", "F5"));

    assertThat(identifiers.size(), equalTo(3));
  }
}
