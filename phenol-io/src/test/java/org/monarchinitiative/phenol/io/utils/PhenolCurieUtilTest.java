package org.monarchinitiative.phenol.io.utils;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adapted from CurieUtilBuilderTest, remiving superfluous tests.
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 * @author Peter Robinson <peter.robinson@jax.org>
 */
class PhenolCurieUtilTest {


  @Test
  void defaultCurieUtil() {
    PhenolCurieUtil instance = PhenolCurieUtil.defaultCurieUtil();
    assertEquals(Optional.of("HP:0000001"), instance.getCurie("http://purl.obolibrary.org/obo/HP_0000001"));
  }

  @Test
  void defaultCurieUtilWithDefaultsAndUserDefinedResource() {
    PhenolCurieUtil defaultCurieUtil = PhenolCurieUtil.defaultCurieUtil();
    assertFalse(defaultCurieUtil.containsKey("WIBBLE"));

    PhenolCurieUtil instance = PhenolCurieUtil.withDefaultsAnd(ImmutableMap.of("WIBBLE", "http://purl.obolibrary.org/obo/WIBBLE_"));
    assertTrue(instance.containsKey("WIBBLE"));
    assertEquals(Optional.of("WIBBLE:0000001"), instance.getCurie("http://purl.obolibrary.org/obo/WIBBLE_0000001"));
   // The following items are not needed in phenol
    // assertEquals("http://purl.obolibrary.org/obo/WIBBLE_", instance.getExpansion("WIBBLE"));
    //assertEquals(Optional.of("http://purl.obolibrary.org/obo/WIBBLE_0000001"), instance.getIri("WIBBLE:0000001"));
  }

}
