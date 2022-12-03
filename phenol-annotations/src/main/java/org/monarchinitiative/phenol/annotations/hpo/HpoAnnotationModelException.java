package org.monarchinitiative.phenol.annotations.hpo;

import org.monarchinitiative.phenol.base.PhenolException;

/**
 * Exceptions of this class are thrown if there are quality control issues with the
 * generation of a {@link org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel} or
 * {@link org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry} (single line of an HPO annotation file)
 * @author Peter Robinson
 */
@Deprecated(forRemoval = true)
public class HpoAnnotationModelException extends PhenolException {
  private final static long serialVersionUID = 2;
    public HpoAnnotationModelException(String msg) {super(msg);}
}
