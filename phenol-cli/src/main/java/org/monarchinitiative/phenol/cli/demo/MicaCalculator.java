package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.constants.hpo.HpoSubOntologyRootTermIds;
import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItem;
import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItemContainer;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Calculate information content of the <em>most informative common ancestor</em> (IC<sub>MICA</sub>) for the concepts
 * in given {@link MinimalOntology} using a corpus of annotated items.
 * <p>
 * For instance, calculate IC<sub>MICA</sub> of HPO terms that annotate Mendelian diseases.
 * <p>
 * The IC<sub>MICA</sub> values are in <em>nats</em>.
 */
public class MicaCalculator {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicaCalculator.class);

  private final MinimalOntology hpo;
  /**
   * {@code true} if the count of annotations that are not used to annotate at least one corpus item should be set to 1
   * to prevent ic being {@link Double#POSITIVE_INFINITY}.
   */
  private final boolean assumeAnnotated;

  public MicaCalculator(MinimalOntology hpo, boolean assumeAnnotated) {
    this.assumeAnnotated = assumeAnnotated;
    this.hpo = Objects.requireNonNull(hpo);
  }

  public MicaData calculateMica(AnnotatedItemContainer<? extends AnnotatedItem> itemContainer) {
    Instant start = Instant.now();
    Map<TermId, Integer> phenotypeIdToDiseaseIds = new HashMap<>();
    Map<TermId, Collection<TermId>> diseaseIdToTermIds = new HashMap<>();

    Set<TermId> termsAndAncestorsBuilder = new HashSet<>();
    for (AnnotatedItem item: itemContainer) {
      for (Identified annotation : item.annotations()) {
        TermId id = annotation.id();
        for (TermId ancestor : hpo.graph().getAncestors(id, true)) {
          termsAndAncestorsBuilder.add(ancestor);
        }
      }

      for (TermId tid : termsAndAncestorsBuilder) {
        // We can't do this within the hot loop above because there is no guarantee of uniqueness of the ancestors.
        phenotypeIdToDiseaseIds.compute(tid, (key, val) -> val == null ? 0 : val + 1);
        diseaseIdToTermIds.computeIfAbsent(item.id(), key -> new HashSet<>()).add(tid); // Note that this MUST be a Set
      }
      termsAndAncestorsBuilder.clear();
    }

    Map<TermId, Double> termToIc = new HashMap<>();
    double totalPopulationHpoTerms = phenotypeIdToDiseaseIds.get(HpoSubOntologyRootTermIds.PHENOTYPIC_ABNORMALITY);
    for (Map.Entry<TermId, Integer> e : phenotypeIdToDiseaseIds.entrySet()) {
      int annotatedCount = assumeAnnotated ? Math.max(e.getValue(), 1) : e.getValue();
      double ic = Math.log(totalPopulationHpoTerms / annotatedCount);
      termToIc.put(e.getKey(), ic);
    }

    Duration duration = Duration.between(start, Instant.now());
    long seconds = duration.toMillis() / 1000;
    double ms = (double) duration.toMillis() % 1000;
    LOGGER.debug("Calculated information content in {}s {}us", seconds, ms);

    return new MicaData(diseaseIdToTermIds, phenotypeIdToDiseaseIds, termToIc);
  }

}
