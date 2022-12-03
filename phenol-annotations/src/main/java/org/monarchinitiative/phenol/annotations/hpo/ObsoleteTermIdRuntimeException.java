package org.monarchinitiative.phenol.annotations.hpo;


import org.monarchinitiative.phenol.base.PhenolRuntimeException;

/**
 * TODO check for obsoleted primary term ids when parsing phenotype.hpoa
 */
public class ObsoleteTermIdRuntimeException extends PhenolRuntimeException  {
  private final static long serialVersionUID = 2;
    public ObsoleteTermIdRuntimeException(String msg) {super(msg);}
}
