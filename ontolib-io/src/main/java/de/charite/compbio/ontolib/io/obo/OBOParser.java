package de.charite.compbio.ontolib.io.obo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;

/**
 * Driver code for parsing OBO files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class OBOParser {

  /** The {@link Logger} object to use for logging. */
  private static final Logger LOGGER = LoggerFactory.getLogger(OBOParser.class);

  /** Flag whether debugging is enabled or not. */
  private boolean debug;

  /** Default constructor, disables debugging. */
  public OBOParser() {
    this(false);
  }

  /**
   * Constructor.
   *
   * @param debug Whether or not to enable debugging.
   */
  public OBOParser(boolean debug) {
    this.debug = debug;
  }

  /**
   * Parse OBO file in one go.
   * 
   * @param file Path to OBO file.
   * @return Completely parsed OBO file.
   *
   * @throws IOException In case of problems with file I/O. F
   */
  public OBOFile parseFile(File file) throws IOException {
    final HelperListener helper = new HelperListener();
    parseFile(file, helper);
    return new OBOFile(helper.getHeader(), helper.getStanzas());
  }

  /**
   * Parse OBO file, report complete parsing of header and stanzas via <code>listener</code>.
   * 
   * @param file Path to OBO file.
   * @param listener Listener for parsing.
   *
   * @throws IOException In case of problems with file I/O.
   */
  public void parseFile(File file, OBOParseResultListener listener) throws IOException {
    LOGGER.info("Parsing OBO file {}...", new Object[] {file.getAbsolutePath()});
    parseInputStream(CharStreams.fromFileName(file.getAbsolutePath()), listener);
    LOGGER.info("Done parsing OBO file.");
  }

  /**
   * Parse OBO file in one go.
   * 
   * @param file Path to OBO file.
   * @return Completely parsed OBO file.
   *
   * @throws IOException In case of problems with file I/O. F
   */
  public OBOFile parseString(String oboString) {
    final HelperListener helper = new HelperListener();
    parseString(oboString, helper);
    return new OBOFile(helper.getHeader(), helper.getStanzas());
  }

  /**
   * Parse OBO file, report complete parsing of header and stanzas via <code>listener</code>.
   * 
   * @param file Path to OBO file.
   * @param listener Listener for parsing.
   *
   * @throws IOException In case of problems with file I/O.
   */
  public void parseString(String oboString, OBOParseResultListener listener) {
    parseInputStream(CharStreams.fromString(oboString), listener);
  }

  private void parseInputStream(CharStream inputStream, OBOParseResultListener listener) {
    final OBOLexer l = new OBOLexer(inputStream);
    final Antlr4OBOParser p = new Antlr4OBOParser(new CommonTokenStream(l));

    p.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
          int charPositionInLine, String msg, RecognitionException e) {
        throw new IllegalStateException("Failed to parse at line " + line + " due to " + msg, e);
      }
    });

    if (debug) {
      p.addErrorListener(new DiagnosticErrorListener());
    }

    p.addParseListener(new OBOParserListener(listener));

    p.oboFile();
  }

  /**
   * Helper class for collecting parse result; header and stanzas.
   */
  private final class HelperListener implements OBOParseResultListener {

    private Header header = null;

    private List<Stanza> stanzas = Lists.newArrayList();

    @Override
    public void parsedHeader(Header header) {
      this.header = header;
    }

    @Override
    public void parsedStanza(Stanza stanza) {
      stanzas.add(stanza);
    }

    @Override
    public void parsedFile() {
      // nop
    }

    /**
     * @return Resulting header
     */
    public Header getHeader() {
      return header;
    }

    /**
     * @return The resulting stanyas.
     */
    public List<Stanza> getStanzas() {
      return stanzas;
    }

  }

}
