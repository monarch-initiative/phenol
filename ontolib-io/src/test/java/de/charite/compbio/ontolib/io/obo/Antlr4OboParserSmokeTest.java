package de.charite.compbio.ontolib.io.obo;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

/**
 * Smoke tests for the ANTLR 4 OBO lexer.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Antlr4OboParserSmokeTest extends SmokeTestBase {

  @Test
  public void testParsingMinimalFile() throws Exception {
    final CodePointCharStream inputStream = CharStreams.fromString(MINIMAL_FILE);
    final OboLexer l = new OboLexer(inputStream);
    final Antlr4OboParser p = new Antlr4OboParser(new CommonTokenStream(l));
    p.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
          int charPositionInLine, String msg, RecognitionException e) {
        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
      }
    });
    p.addErrorListener(new DiagnosticErrorListener());
    try {
      p.oboFile();
    } catch (IllegalStateException e) {
      throw new Exception("Problem parsing \"" + MINIMAL_FILE + "\"", e);
    }
  }

  @Test
  public void testParsingHeadOfHPO() throws Exception {
    final CodePointCharStream inputStream = CharStreams.fromString(HEAD_HPO);
    final OboLexer l = new OboLexer(inputStream);
    final Antlr4OboParser p = new Antlr4OboParser(new CommonTokenStream(l));
    p.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
          int charPositionInLine, String msg, RecognitionException e) {
        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
      }
    });
    p.addErrorListener(new DiagnosticErrorListener());
    try {
      p.oboFile();
    } catch (IllegalStateException e) {
      throw new Exception("Problem parsing \"" + HEAD_HPO + "\"", e);
    }
  }

}
