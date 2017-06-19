package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.DbXRefContext;

public class Antlr4OboParserTestDbXRef extends Antlr4OboParserTestBase {

  @Test
  public void testNameNoDescriptionNoModifier() {
    final String text = "HP:author";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefContext ctx = parser.dbXRef();
    final DbXref dbXRef = (DbXref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXRef.getName());
    assertNull(dbXRef.getDescription());
    assertNull(dbXRef.getTrailingModifier());
  }

  @Test
  public void testNameDescriptionNoModifier() {
    final String text = "HP:author \"Author \\\"description\"";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefContext ctx = parser.dbXRef();
    final DbXref dbXRef = (DbXref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXRef.getName());
    assertEquals("Author \"description", dbXRef.getDescription());
    assertNull(dbXRef.getTrailingModifier());
  }

  @Test
  public void testNameNoDescriptionModifier() {
    final String text = "HP:author {key1=value1,key2=value2}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefContext ctx = parser.dbXRef();
    final DbXref dbXRef = (DbXref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXRef.getName());
    assertEquals(null, dbXRef.getDescription());
    assertNotNull(dbXRef.getTrailingModifier());
  }

  @Test
  public void testNameDescriptionModifier() {
    final String text = "HP:author \"Author \\\"description\" {key1=value1,key2=value2}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefContext ctx = parser.dbXRef();
    final DbXref dbXRef = (DbXref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXRef.getName());
    assertEquals("Author \"description", dbXRef.getDescription());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key1, value=value1], KeyValue [key=key2, "
            + "value=value2]]]",
        dbXRef.getTrailingModifier().toString());
  }

}
