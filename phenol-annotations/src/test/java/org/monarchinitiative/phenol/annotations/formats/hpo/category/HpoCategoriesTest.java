package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HpoCategoriesTest {

  @Test
  void testCategories(){
    assertEquals(HpoCategories.preset().length, 27);
  }

}
