package com.github.phenomics.ontolib.io.obo;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboLexer;

/**
 * Smoke tests for the ANTLR 4 OBO lexer.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Antlr4OboLexerSmokeTest extends SmokeTestBase {

  @Before
  public void setUp() throws Exception {}

  @Test
  public void testLexingMinimalFile() {
    final CodePointCharStream inputStream = CharStreams.fromString(MINIMAL_FILE);
    Antlr4OboLexer l = new Antlr4OboLexer(inputStream);
    l.getAllTokens();
  }

  @Test
  public void testLexingHeadOfHPO() {
    final CodePointCharStream inputStream = CharStreams.fromString(HEAD_HPO);
    Antlr4OboLexer l = new Antlr4OboLexer(inputStream);
    l.getAllTokens();
  }

}
