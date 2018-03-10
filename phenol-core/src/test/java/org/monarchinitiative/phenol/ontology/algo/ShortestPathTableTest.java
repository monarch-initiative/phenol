package org.monarchinitiative.phenol.ontology.algo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

public class ShortestPathTableTest extends VegetableOntologyTestBase {

  ShortestPathTable table;

  @Before
  public void setUp() {
    super.setUp();
    table = new ShortestPathTable(ontology);
  }

  @Test
  public void testGetDistance() {
    assertEquals(1, table.getDistance(idRootVegetable, idVegetable));
    assertEquals(-1, table.getDistance(idVegetable, idRootVegetable));

    assertEquals(1, table.getDistance(idCarrot, idRootVegetable));
    assertEquals(-1, table.getDistance(idRootVegetable, idCarrot));

    assertEquals(-1, table.getDistance(idBeet, idBlueCarrot));
    assertEquals(-1, table.getDistance(idBlueCarrot, idBeet));

    assertEquals(2, table.getDistance(idBlueCarrot, idRootVegetable));
    assertEquals(-1, table.getDistance(idRootVegetable, idBlueCarrot));

    assertEquals(1, table.getDistance(idPumpkin, idRootVegetable));
    assertEquals(-1, table.getDistance(idRootVegetable, idPumpkin));
  }

  @Test
  public void testGetDistanceSymmetric() {
    assertEquals(1, table.getDistanceSymmetric(idRootVegetable, idVegetable));
    assertEquals(1, table.getDistanceSymmetric(idVegetable, idRootVegetable));

    assertEquals(1, table.getDistanceSymmetric(idCarrot, idRootVegetable));
    assertEquals(1, table.getDistanceSymmetric(idRootVegetable, idCarrot));

    assertEquals(-1, table.getDistanceSymmetric(idBeet, idBlueCarrot));
    assertEquals(-1, table.getDistanceSymmetric(idBlueCarrot, idBeet));

    assertEquals(2, table.getDistanceSymmetric(idBlueCarrot, idRootVegetable));
    assertEquals(2, table.getDistanceSymmetric(idRootVegetable, idBlueCarrot));

    assertEquals(1, table.getDistanceSymmetric(idPumpkin, idRootVegetable));
    assertEquals(1, table.getDistanceSymmetric(idRootVegetable, idPumpkin));
  }
}
