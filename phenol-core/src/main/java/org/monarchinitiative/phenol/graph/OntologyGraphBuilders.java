package org.monarchinitiative.phenol.graph;


import org.monarchinitiative.phenol.graph.csr.CsrOntologyGraphBuilder;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Static factory functions for getting {@link OntologyGraphBuilder}s.
 *
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class OntologyGraphBuilders {

  private OntologyGraphBuilders(){}

  /**
   * Get an {@linkplain OntologyGraphBuilder} for building a graph backed by an adjacency matrix in CSR format.
   * <p>
   * The relationship can be encoded into data of different widths:
   * <ul>
   *   <li>{@linkplain Byte}</li>
   *   <li>{@linkplain Short}</li>
   *   <li>{@linkplain Integer}, or</li>
   *   <li>{@linkplain Long}</li>
   * </ul>
   * depending on the number of {@link RelationType}s present in the ontology data.
   *
   * @param clz class of the data type to use for storing {@link RelationType}s between the graph nodes.
   * @return the builder
   * @param <E> data type to use for storing {@link RelationType}s between the graph nodes.
   *
   */
  public static <E> OntologyGraphBuilder<TermId> csrBuilder(Class<E> clz) {
    return CsrOntologyGraphBuilder.builder(clz);
  }

}
