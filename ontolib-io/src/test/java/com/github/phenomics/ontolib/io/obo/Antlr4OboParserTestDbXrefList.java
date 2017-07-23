package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.DbXrefListContext;
import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.DbXrefList;

public class Antlr4OboParserTestDbXrefList extends Antlr4OboParserTestBase {

  @Test
  public void testNameNoDescriptionEmptyList() {
    final String text = "[]";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefListContext ctx = parser.dbXrefList();
    final DbXrefList dbXrefList = (DbXrefList) getOuterListener().getValue(ctx);

    assertEquals(0, dbXrefList.getDbXrefs().size());
    assertEquals("DbXrefList [dbXrefs=[]]", dbXrefList.toString());
  }

  @Test
  public void testNameNoDescriptionOneElementList() {
    final String text = "[name \"description\" {key=value}]";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefListContext ctx = parser.dbXrefList();
    final DbXrefList dbXrefList = (DbXrefList) getOuterListener().getValue(ctx);

    assertEquals(1, dbXrefList.getDbXrefs().size());
    assertEquals(
        "DbXrefList [dbXrefs=[DbXref [name=name, description=description, "
            + "trailingModifier=TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]]]]",
        dbXrefList.toString());
  }

  @Test
  public void testNameNoDescriptionTwoElementList() {
    final String text = "[name \"description\" {key=value}, name2 \"description2\" {key2=value2}]";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXrefListContext ctx = parser.dbXrefList();
    final DbXrefList dbXrefList = (DbXrefList) getOuterListener().getValue(ctx);

    assertEquals(2, dbXrefList.getDbXrefs().size());
    assertEquals("DbXrefList [dbXrefs=[DbXref [name=name, description=description, "
        + "trailingModifier=TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]], "
        + "DbXref [name=name2, description=description2, trailingModifier=TrailingModifier "
        + "[keyValue=[KeyValue [key=key2, value=value2]]]]]]", dbXrefList.toString());
  }

}
