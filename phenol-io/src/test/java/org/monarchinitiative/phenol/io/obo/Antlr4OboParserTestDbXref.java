package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.DbXrefContext;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.Dbxref;

public class Antlr4OboParserTestDbXref extends Antlr4OboParserTestBase {

  @Test
  public void testNameNoDescriptionNoModifier() {
    final String text = "HP:author";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefContext ctx = parser.dbXref();
    final Dbxref dbXref = (Dbxref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXref.getName());
    assertNull(dbXref.getDescription());
    assertNull(dbXref.getTrailingModifiers());
  }

  @Test
  public void testNameDescriptionNoModifier() {
    final String text = "HP:author \"Author \\\"description\"";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefContext ctx = parser.dbXref();
    final Dbxref dbXref = (Dbxref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXref.getName());
    assertEquals("Author \"description", dbXref.getDescription());
    assertNull(dbXref.getTrailingModifiers());
  }

  @Test
  public void testNameNoDescriptionModifier() {
    final String text = "HP:author {key1=value1,key2=value2}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefContext ctx = parser.dbXref();
    final Dbxref dbXref = (Dbxref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXref.getName());
    assertEquals(null, dbXref.getDescription());
    assertNotNull(dbXref.getTrailingModifiers());
  }

  @Test
  public void testNameDescriptionModifier() {
    final String text = "HP:author \"Author \\\"description\" {key1=value1,key2=value2}";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefContext ctx = parser.dbXref();
    final Dbxref dbXref = (Dbxref) getOuterListener().getValue(ctx);

    assertEquals("HP:author", dbXref.getName());
    assertEquals("Author \"description", dbXref.getDescription());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key1, value=value1], KeyValue [key=key2, "
            + "value=value2]]]",
        dbXref.getTrailingModifiers().toString());
  }
}