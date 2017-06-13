package de.charite.compbio.ontolib.io.obo;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.DbXRefContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TrailingModifierContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TrailingModifierKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParserBaseListener;

/**
 * Master <code>ParseTreeListener</code> to use for OBO parsing.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Antlr4OBOParserListenerImpl extends Antlr4OBOParserBaseListener {

  /** {@link @Logger} instance to use for logging. */
  private static final Logger LOGGER = LoggerFactory.getLogger(Antlr4OBOParserListenerImpl.class);

  /** maps nodes to Objects with Map<ParseTree,Object> */
  ParseTreeProperty<Object> values = new ParseTreeProperty<>();

  public void setValue(ParseTree node, Object value) {
    values.put(node, value);
  }

  public Object getValue(ParseTree node) {
    return values.get(node);
  }

  /**
   * Exit <code>dbXRef</code> rule, construct {@link DBXRef} object.
   */
  @Override
  public void exitDbXRef(DbXRefContext ctx) {
    final String name = ctx.Word().getText();
    final String description = (ctx.QuotedString() == null) ? null : ctx.QuotedString().getText();
    final TrailingModifier trailingModifier = (TrailingModifier) getValue(ctx.trailingModifier());

    setValue(ctx, new DBXRef(name, description, trailingModifier));
  }

  /**
   * Exit <code>trailingModifier</code> rule, construct {@link TrailingModifier} object.
   */
  @Override
  public void exitTrailingModifier(TrailingModifierContext ctx) {
    final TrailingModifier tm = new TrailingModifier();
    for (TrailingModifierKeyValueContext kvCtx : ctx.trailingModifierKeyValue()) {
      tm.addKeyValue(kvCtx.Word(0).getText().trim(), kvCtx.Word(1).getText().trim());
    }

    setValue(ctx, tm);
  }

}
