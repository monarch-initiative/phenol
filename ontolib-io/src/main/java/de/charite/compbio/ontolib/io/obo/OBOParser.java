package de.charite.compbio.ontolib.io.obo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Driver code for parsing HGVS strings into HGVSVariant objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OBOParser {

  /** The {@link Logger} object to use for logging. */
  private static final Logger LOGGER = LoggerFactory.getLogger(OBOParser.class);

  private boolean debug = false;

  public OBOParser() {}

  public OBOParser(boolean debug) {
    this.debug = debug;
  }

}
