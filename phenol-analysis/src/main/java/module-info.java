module org.monarchinitiative.phenol.analysis {
  requires org.monarchinitiative.phenol.core;
  requires org.monarchinitiative.phenol.annotations;

  requires java.sql;
  requires org.apache.commons.codec;
  requires org.slf4j;

  // Exporting all packages as it's hard to say what the API should look like right now.
  exports org.monarchinitiative.phenol.analysis;
  exports org.monarchinitiative.phenol.analysis.mgsa;
  exports org.monarchinitiative.phenol.analysis.scoredist;
  exports org.monarchinitiative.phenol.analysis.stats;
  exports org.monarchinitiative.phenol.analysis.stats.mtc;
}
