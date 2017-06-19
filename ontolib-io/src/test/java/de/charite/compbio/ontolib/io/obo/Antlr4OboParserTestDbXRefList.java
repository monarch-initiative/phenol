package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.DbXRefListContext;

public class Antlr4OboParserTestDbXRefList extends Antlr4OboParserTestBase {

  @Test
  public void testNameNoDescriptionEmptyList() {
    final String text = "[]";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefListContext ctx = parser.dbXRefList();
    final DbXrefList dbXRefList = (DbXrefList) getOuterListener().getValue(ctx);

    assertEquals(0, dbXRefList.getDbXrefs().size());
    assertEquals("DBXRefList [dbXRefs=[]]", dbXRefList.toString());
  }

  @Test
  public void testNameNoDescriptionOneElementList() {
    final String text = "[name \"description\" {key=value}]";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefListContext ctx = parser.dbXRefList();
    final DbXrefList dbXRefList = (DbXrefList) getOuterListener().getValue(ctx);

    assertEquals(1, dbXRefList.getDbXrefs().size());
    assertEquals(
        "DBXRefList [dbXRefs=[DBXRef [name=name, description=description, "
            + "trailingModifier=TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]]]]",
        dbXRefList.toString());
  }

  @Test
  public void testNameNoDescriptionTwoElementList() {
    final String text = "[name \"description\" {key=value}, name2 \"description2\" {key2=value2}]";
    final Antlr4OboParser parser = buildParser(text, "valueMode");
    final DbXRefListContext ctx = parser.dbXRefList();
    final DbXrefList dbXRefList = (DbXrefList) getOuterListener().getValue(ctx);

    assertEquals(2, dbXRefList.getDbXrefs().size());
    assertEquals("DBXRefList [dbXRefs=[DBXRef [name=name, description=description, "
        + "trailingModifier=TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]], "
        + "DBXRef [name=name2, description=description2, trailingModifier=TrailingModifier "
        + "[keyValue=[KeyValue [key=key2, value=value2]]]]]]", dbXRefList.toString());
  }

}
