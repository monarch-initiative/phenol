/**
 * Routines for deserialization of ontology data.
 */
module org.monarchinitiative.phenol.io {
  exports org.monarchinitiative.phenol.io;
  exports org.monarchinitiative.phenol.io.utils;

  requires transitive org.monarchinitiative.phenol.core;

  requires obographs.core;
  requires org.yaml.snakeyaml;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.datatype.guava;
  requires org.slf4j;
}
