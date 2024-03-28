package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assign an HPO term into a broader group represented by a high-level HPO term.
 * <p>
 * If a term can be assigned into more than one groups, the first group is chosen.
 * <p>
 * In case of one of groups corresponding to a <a href="https://hpo.jax.org/app/browse/term/HP:0002664">Neoplasm</a>,
 * the neoplasm is chosen.
 *
 * @author Daniel Danis
 * @author Mike Gargano
 * @since 2.1.0
 */
public class HpoCategoryLookup {

  private final OntologyGraph<TermId> graph;

  private final Term[] categories;

  public HpoCategoryLookup(OntologyGraph<TermId> graph, Term[] categories) {
    this.graph = graph;
    this.categories = categories;
  }

  /**
   * This finds the "best" category for a term. The categories do not cover all terms.
   *
   * @param termId of the target term
   * @return an optional with a {@link Term} corresponding to  a category or an
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
