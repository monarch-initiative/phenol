package org.monarchinitiative.phenol.graph.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ImmutableEdgeTest {

  private ImmutableEdge<Integer> edge;

  @Before
  public void setUp() {
    edge = ImmutableEdge.construct(1, 2, 1);
  }

  @Test
  public void testAccessors() {
    assertEquals(1, edge.getSource().intValue());
    assertEquals(2, edge.getDest().intValue());
    assertEquals(1, edge.getId());
  }

  @Test
  public void testToString() {
    assertEquals("ImmutableEdge [source=1, dest=2, id=1]", edge.toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testClone() {
    ImmutableEdge<Integer> e2 = (ImmutableEdge<Integer>) edge.clone();
    assertNotSame(edge, e2);
    assertEquals(edge, e2);
    assertEquals("ImmutableEdge [source=1, dest=2, id=1]", e2.toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testEquals() {
    assertTrue(edge.equals(edge));

    ImmutableEdge<Integer> e2 = (ImmutableEdge<Integer>) edge.clone();
    assertTrue(edge.equals(e2));

    ImmutableEdge<Integer> e3 = ImmutableEdge.construct(3, 2, 1);
    assertFalse(edge.equals(e3));

    ImmutableEdge<Integer> e4 = ImmutableEdge.construct(1, 3, 1);
    assertFalse(edge.equals(e4));

    ImmutableEdge<Integer> e5 = ImmutableEdge.construct(1, 2, 3);
    assertFalse(edge.equals(e5));
  }

}
