package org.monarchinitiative.phenol.io;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

@Disabled("Requires local files")
public class TestLocalLoad {

  @Test
  public void loadHpo() throws Exception {
    Path path = Path.of("/home/ielis/data/ontologies/hpo/2023-07-21/hp.2023-07-21.json");
    MinimalOntology hpo = loadOntology("HPO", path);
    System.err.printf("Loaded ontology with %d terms%n", hpo.getTerms().size());
    Thread.sleep(15_000L);
    System.err.println("Done");
  }

  @Test
  public void loadMondo() {
    Path base = Path.of("/home/ielis/data/ontologies/mondo/2023-07-03/mondo-base.json");
    loadOntology("MONDO base", base);
    Path minimal = Path.of("/home/ielis/data/ontologies/mondo/2023-07-03/mondo-minimal.json");
    loadOntology("MONDO minimal", minimal);
    Path rare = Path.of("/home/ielis/data/ontologies/mondo/2023-07-03/mondo-rare.json");
    loadOntology("MONDO rare", rare);
  }

  @Test
  public void loadGo() {
    Path path = Path.of("/home/ielis/data/ontologies/go/2021-12-16/go.json");
    loadOntology("GO", path);
  }

  @Test
  public void loadMaxo() {
    // TODO
  }

  private static MinimalOntology loadOntology(String name, Path path) {
    System.err.printf("--------------------------------------------------------------------------------%n");
    System.err.printf("Loading %s from %s%n", name, path.toAbsolutePath());
    Instant start = Instant.now();
    MinimalOntology ontology = MinimalOntologyLoader.loadOntology(path.toFile());
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    System.err.printf("Loaded in %dms%n", duration.toMillis());
    return ontology;
  }
}
