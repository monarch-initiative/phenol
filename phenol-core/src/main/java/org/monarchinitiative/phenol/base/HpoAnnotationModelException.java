package org.monarchinitiative.phenol.base;

import org.monarchinitiative.phenol.base.PhenolException;

/**
 * Exceptions of this class are thrown if there are quality control issues with the
 * generation of a {@link org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel} or
 * {@link org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry} (single line of an HPO annotation file)
 * @author Peter Robinson
 */
public class HpoAnnotationModelException extends PhenolException {
    public HpoAnnotationModelException(String msg) {super(msg);}
}
