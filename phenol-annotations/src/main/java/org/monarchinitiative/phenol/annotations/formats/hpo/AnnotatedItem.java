package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.Identified;

import java.util.Collection;

/**
 * An item that has an ID and a collection of annotations that have IDs as well.
 */
public interface AnnotatedItem extends Identified {

  Collection<? extends Identified> annotations();

}
