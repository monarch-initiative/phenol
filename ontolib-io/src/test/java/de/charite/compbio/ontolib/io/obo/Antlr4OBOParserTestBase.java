package de.charite.compbio.ontolib.io.obo;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParserBaseListener;

/**
 * Test class base for Antlr4 OBO parsers.
 *
 * @author mholtgre
 */
public class Antlr4OBOParserTestBase {

  /** The parser base listener to use. */
  private final Antlr4OBOParserListenerImpl listener = new Antlr4OBOParserListenerImpl();
  
  /**
   * Build and return {@link Antlr4OBOParser} for a given <code>text</code>.
   * 
   * @param text String with the text to parse.
   * @param mode Name of the mode to use.
   * @return {@link Antlr4OBOParser}, readily setup for parsing the OBO file.
   */
  protected Antlr4OBOParser buildParser(String text, String mode) {
    ANTLRInputStream inputStream = new ANTLRInputStream(text);
    OBOLexer l = new OBOLexer(inputStream);
    
    for (int i = 0; i < l.getModeNames().length; ++i) {
      if (mode.equals(l.getModeNames()[i])) {
        l.mode(i);
      }
    }

    Antlr4OBOParser p = new Antlr4OBOParser(new CommonTokenStream(l));

    p.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
          int charPositionInLine, String msg, RecognitionException e) {
        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
      }
    });

    p.addErrorListener(new DiagnosticErrorListener());

    p.addParseListener(listener);
    
    return p;
  }
  
  protected Antlr4OBOParser buildParser(String text) {
    return buildParser(text, "DEFAULT_MODE");
  }

  /**
   * @return The listener, e.g., stores temporary values.
   */
  public Antlr4OBOParserListenerImpl getListener() {
    return listener;
  }

}
