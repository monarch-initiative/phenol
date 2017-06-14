package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TrailingModifierContext;

public class Antlr4OBOParserTestTrailingModifier extends Antlr4OBOParserTestBase {

  @Test
  public void testEmptyList() {
    final String text = "{}";
    final Antlr4OBOParser parser = buildParser(text, "valueMode");
    final TrailingModifierContext ctx = parser.trailingModifier();
    final TrailingModifier tm = (TrailingModifier) getOuterListener().getValue(ctx);

    assertEquals(0, tm.getKeyValue().size());
    assertEquals("TrailingModifier [keyValue=[]]", tm.toString());
  }

  @Test
  public void testOneElementList() {
    final String text = "{k=v}";
    final Antlr4OBOParser parser = buildParser(text, "valueMode");
    final TrailingModifierContext ctx = parser.trailingModifier();
    final TrailingModifier tm = (TrailingModifier) getOuterListener().getValue(ctx);

    assertEquals(1, tm.getKeyValue().size());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=k, value=v]]]", tm.toString());
  }

  @Test
  public void testTwoElementsList() {
    final String text = "{k=v,l=w}";
    final Antlr4OBOParser parser = buildParser(text, "valueMode");
    final TrailingModifierContext ctx = parser.trailingModifier();
    final TrailingModifier tm = (TrailingModifier) getOuterListener().getValue(ctx);

    assertEquals(2, tm.getKeyValue().size());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=k, value=v], KeyValue [key=l, value=w]]]",
        tm.toString());
  }


}
