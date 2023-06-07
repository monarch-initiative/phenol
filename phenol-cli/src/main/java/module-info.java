module org.monarchinitiative.phenol.cli {
  requires org.monarchinitiative.phenol.io;
  requires org.monarchinitiative.phenol.analysis;

  requires org.apache.commons.csv;
  requires info.picocli;
  requires org.slf4j;

  exports org.monarchinitiative.phenol.cli.cmd to info.picocli;
}
