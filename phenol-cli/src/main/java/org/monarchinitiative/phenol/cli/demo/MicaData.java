package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Map;

public class MicaData {

  private final Map<TermId, Collection<TermId>> diseaseIdToTermIds;
  // Number of diseases the term is observed in.
  private final Map<TermId, Integer> phenotypeIdToDiseaseIds;

  private final Map<TermId, Double> termToIc;

  public MicaData(Map<TermId, Collection<TermId>> diseaseIdToTermIds,
                  Map<TermId, Integer> phenotypeIdToDiseaseIds, Map<TermId, Double> termToIc) {
    this.diseaseIdToTermIds = diseaseIdToTermIds;
    this.phenotypeIdToDiseaseIds = phenotypeIdToDiseaseIds;
    this.termToIc = termToIc;
  }

  public Map<TermId, Collection<TermId>> diseaseIdToTermIds() {
    return diseaseIdToTermIds;
  }

  public Map<TermId, Integer> phenotypeIdToDiseaseIds() {
    return phenotypeIdToDiseaseIds;
  }

  public Map<TermId, Double> termToIc() {
    return termToIc;
  }

}
