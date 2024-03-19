package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HpoCategoryLookup {

  private final Logger LOGGER = LoggerFactory.getLogger(HpoCategoryLookup.class);

  private final OntologyGraph<TermId> graph;

  private final Term[] categories;

  public HpoCategoryLookup(OntologyGraph<TermId> graph, Term[] categories) {
    this.graph = graph;
    this.categories = categories;
  }

  /**
   * This finds the "best" category for a term. The categories do not cover all terms.
   * @param termId
   * @return the Term for a category
   */
  public Optional<Term> getPrioritizedCategory(TermId termId) {
    List<Term> categories = getCategoriesForId(termId);
    if(categories.isEmpty()){
      return Optional.empty();
    } else if (categories.stream().anyMatch(t -> t.equals(HpoCategories.NEOPLASM))) {
      return Optional.of(HpoCategories.NEOPLASM);
    }
    return Optional.of(categories.get(0));
  }

  private List<Term> getCategoriesForId(TermId termId){
    Set<TermId> ancestors = this.graph.getAncestorSet(termId);
    ancestors.add(termId);
   return Arrays.stream(this.categories).filter(c ->
       ancestors.stream().anyMatch(t -> t.equals(c.id()))
   ).collect(Collectors.toList());
  }
}
