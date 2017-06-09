package de.charite.compbio.ontolib.io.obo;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOLexer;

/**
 * Extends the generated Antlr4OBOLexer to bail out at the first lexing error.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OBOLexer extends Antlr4OBOLexer {

  public OBOLexer(CharStream input) {
    super(input);
  }

  @Override
  public void recover(LexerNoViableAltException e) {
    throw new RuntimeException(e); // bail out
  }

}
