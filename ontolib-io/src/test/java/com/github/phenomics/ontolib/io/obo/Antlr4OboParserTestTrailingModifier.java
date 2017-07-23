package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.TrailingModifier;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TrailingModifierContext;

public class Antlr4OboParserTestTrailingModifier extends Antlr4OboParserTestBase {

  @Test
  public void testEmptyList() {
    final String text = "{}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final TrailingModifierContext ctx = parser.trailingModifier();
    final TrailingModifier tm = (TrailingModifier) getOuterListener().getValue(ctx);

    assertEquals(0, tm.getKeyValue().size());
    assertEquals("TrailingModifier [keyValue=[]]", tm.toString());
  }

  @Test
  public void testOneElementList() {
    final String text = "{k=v}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final TrailingModifierContext ctx = parser.trailingModifier();
    final TrailingModifier tm = (TrailingModifier) getOuterListener().getValue(ctx);

    assertEquals(1, tm.getKeyValue().size());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=k, value=v]]]", tm.toString());
  }

  @Test
  public void testTwoElementsList() {
    final String text = "{k=v,l=w}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final TrailingModifierContext ctx = parser.trailingModifier();
    final TrailingModifier tm = (TrailingModifier) getOuterListener().getValue(ctx);

    assertEquals(2, tm.getKeyValue().size());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=k, value=v], KeyValue [key=l, value=w]]]",
        tm.toString());
  }


}
