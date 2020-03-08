package org.monarchinitiative.phenol.io.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

class ObjHexStringConverterTest {

  private int i = 0;
  private TermId termId = TermId.of("MONDO", "001");

  @Test
  void object2hex() throws Exception {
    Assertions.assertDoesNotThrow(() -> ObjHexStringConverter.object2hex((Integer)i));
    Assertions.assertDoesNotThrow(() -> ObjHexStringConverter.object2hex(termId));
  }

  @Test
  void hex2obj() throws Exception {
    String intHex = ObjHexStringConverter.object2hex(i);
    Object objInt = ObjHexStringConverter.hex2obj(intHex);
    Assertions.assertEquals(i, (Integer) objInt);

    String objHex = ObjHexStringConverter.object2hex(termId);
    Object objTermId = ObjHexStringConverter.hex2obj(objHex);
    Assertions.assertEquals(termId, objTermId);

  }

}
