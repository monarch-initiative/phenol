/**
 * Module with data model definitions and IO of HPO annotation data.
 */
module org.monarchinitiative.phenol.annotations {
  requires transitive org.monarchinitiative.phenol.core;

  requires java.xml;
  requires org.slf4j;

    /*                                             Exports                                                              */
  exports org.monarchinitiative.phenol.annotations.analysis;
  exports org.monarchinitiative.phenol.annotations.assoc;
  exports org.monarchinitiative.phenol.annotations.base;
  exports org.monarchinitiative.phenol.annotations.base.temporal;

  exports org.monarchinitiative.phenol.annotations.constants.hpo;

  exports org.monarchinitiative.phenol.annotations.formats;
  exports org.monarchinitiative.phenol.annotations.formats.go;
  exports org.monarchinitiative.phenol.annotations.formats.hpo;
  exports org.monarchinitiative.phenol.annotations.formats.hpo.category;
  exports org.monarchinitiative.phenol.annotations.formats.mpo;

  exports org.monarchinitiative.phenol.annotations.io.go;
  exports org.monarchinitiative.phenol.annotations.io.hpo;

  exports org.monarchinitiative.phenol.annotations.obo.mpo to org.monarchinitiative.phenol.cli;
  // We're not exporting all `org.monarchinitiative.phenol.annotations.obo` & child packages
  // unless somebody uses the code.
}
