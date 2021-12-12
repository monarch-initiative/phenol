package org.monarchinitiative.phenol.analysis;

import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This is the interface for the Ontology (usually: Gene Ontology) annotations.
 * It organizes the relationships netween the annotated items (e.g., genes/proteins)
 * and the ontology terms that annotate them (e.g., GO terms). It also allows bot
 * explicit (direct) and inferred (indirect) annotations to be retrieved. The
 * {@link GoAssociationContainer} is the implementation that we use mainly for
 * Gene Ontology annotations. Other implementations are made for isopret.
 * The AssociationContainer object contains all annotations that were derived from the
 * corresponding GoGaf file (or comparable source). The main purpose of the object is
 * to help construct StudySet objects with the {@link #getAssociationMap} function, which
 * takes a set of domain IDs and returns a map of domain IDs and corresponding
 * {@link DirectAndIndirectTermAnnotations} objects.
 * @author Peter N Robinson
 */
public interface AssociationContainer {

  Map<TermId, List<TermId>> getOntologyTermToDomainItemsMap();

  Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds);

  Set<TermId> getAllAnnotatedGenes();

}
