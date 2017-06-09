package de.charite.compbio.ontolib.io.obo;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOLexer;

/**
 * Smoke tests for the ANTLR 4 OBO lexer.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Antlr4OBOLexerSmokeTest extends SmokeTestBase {

  @Before
  public void setUp() throws Exception {}

  @Test
  public void testLexingMinimalFile() {
    ANTLRInputStream inputStream = new ANTLRInputStream(MINIMAL_FILE);
    Antlr4OBOLexer l = new Antlr4OBOLexer(inputStream);
    l.getAllTokens();
  }

  @Test
  public void testLexingHeadOfHPO() {
    ANTLRInputStream inputStream = new ANTLRInputStream(HEAD_HPO);
    Antlr4OBOLexer l = new Antlr4OBOLexer(inputStream);
    l.getAllTokens();
  }

}
