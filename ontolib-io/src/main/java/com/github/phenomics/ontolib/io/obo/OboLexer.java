package com.github.phenomics.ontolib.io.obo;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;

/**
 * Extends the generated Antlr4OboLexer to bail out at the first lexing error.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OboLexer extends Antlr4OboLexer {

  public OboLexer(CharStream input) {
    super(input);
  }

  @Override
  public void recover(LexerNoViableAltException e) {
    throw new RuntimeException(e); // bail out
  }

}
