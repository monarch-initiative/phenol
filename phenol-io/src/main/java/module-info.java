module org.monarchinitiative.phenol.io {
  exports org.monarchinitiative.phenol.io;

  requires transitive org.monarchinitiative.phenol.core;
  requires obographs.core;
  requires curie.util;
  requires org.yaml.snakeyaml;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.datatype.guava;
  requires org.slf4j;
}
