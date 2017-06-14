package de.charite.compbio.ontolib.io.obo;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
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
    final CodePointCharStream inputStream = CharStreams.fromString(MINIMAL_FILE);
    Antlr4OBOLexer l = new Antlr4OBOLexer(inputStream);
    l.getAllTokens();
  }

  @Test
  public void testLexingHeadOfHPO() {
    final CodePointCharStream inputStream = CharStreams.fromString(HEAD_HPO);
    Antlr4OBOLexer l = new Antlr4OBOLexer(inputStream);
    l.getAllTokens();
  }

}
