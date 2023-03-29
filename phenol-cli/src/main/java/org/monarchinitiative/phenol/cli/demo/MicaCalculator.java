package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.constants.hpo.HpoSubOntologyRootTermIds;
import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItem;
import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItemContainer;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for calculating information content of the <em>most informative common ancestor</em> (MICA)
 * for {@link Ontology} concepts using a corpus of annotated.
 */
public class MicaCalculator {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicaCalculator.class);

  private final Ontology hpo;
  private final boolean assumeAnnotated;

  public MicaCalculator(Ontology hpo) {
    this(hpo, false);
  }

  public MicaCalculator(Ontology hpo, boolean assumeAnnotated) {
    this.assumeAnnotated = assumeAnnotated;
    this.hpo = Objects.requireNonNull(hpo);
  }

  public MicaData calculateMica(AnnotatedItemContainer<? extends AnnotatedItem> itemContainer) {
    Instant start = Instant.now();
    Map<TermId, Integer> phenotypeIdToDiseaseIds = new HashMap<>();
    Map<TermId, Collection<TermId>> diseaseIdToTermIds = new HashMap<>();

    for (AnnotatedItem item: itemContainer) {
      List<TermId> annotationTermIds = item.annotations().stream()
        .map(Identified::id)
        .collect(Collectors.toList());
      // add term ancestors
      Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(hpo, annotationTermIds, true);

      for (TermId tid : inclAncestorTermIds) {
        phenotypeIdToDiseaseIds.compute(tid, (key, val) -> val == null ? 0 : val + 1);
        diseaseIdToTermIds.computeIfAbsent(item.id(), key -> new HashSet<>()).add(tid); // Note that this MUST be a Set
      }
    }

    Map<TermId, Double> termToIc = new HashMap<>();
    int totalPopulationHpoTerms = phenotypeIdToDiseaseIds.get(HpoSubOntologyRootTermIds.PHENOTYPIC_ABNORMALITY);
    for (Map.Entry<TermId, Integer> e : phenotypeIdToDiseaseIds.entrySet()) {
      int annotatedCount = assumeAnnotated ? Math.max(e.getValue(), 1) : e.getValue();
      double ic = -1 * Math.log((double)annotatedCount/totalPopulationHpoTerms);
      termToIc.put(e.getKey(), ic);
    }

    Duration duration = Duration.between(start, Instant.now());
    long seconds = duration.toMillis() / 1000;
    double ms = (double) duration.toMillis() % 1000;
    LOGGER.debug("Calculated information content in {}s {}us", seconds, ms);

    return new MicaData(diseaseIdToTermIds, phenotypeIdToDiseaseIds, termToIc);
  }

}
