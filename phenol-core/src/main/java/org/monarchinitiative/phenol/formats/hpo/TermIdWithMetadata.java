package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;


/**
 * A {@link TermId} with Frequency and Onset metadata
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.0.1
 */
public interface TermIdWithMetadata extends TermId {



    /**
     * @return The annotated {@link TermId}.
     */
    TermId getTermId();

    /**
     * @return The annotating {@link HpoFrequency}.
     */
    HpoFrequency getFrequency() ;


    HpoOnset getOnset() ;

    /**
     * Query for term ID's prefix.
     *
     * @return {@link TermPrefix} of the identifier
     */
    TermPrefix getPrefix();

    /**
     * Query for term ID.
     */
    String getId();

    /**
     * Return the full term ID including prefix.
     *
     * @return The full Id.
     */
    String getIdWithPrefix();



}

