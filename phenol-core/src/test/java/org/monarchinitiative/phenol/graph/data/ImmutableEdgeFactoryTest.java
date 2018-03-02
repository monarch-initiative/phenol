package org.monarchinitiative.phenol.graph.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ImmutableEdgeFactoryTest {

  private ImmutableEdge.Factory<Integer> factory;

  @Before
  public void setUp() {
    factory = new ImmutableEdge.Factory<Integer>();
  }

  @Test
  public void testConstructOne() {
    ImmutableEdge<Integer> e = factory.construct(1, 2);
    assertEquals("ImmutableEdge [source=1, dest=2, id=1]", e.toString());
    assertEquals(2, factory.getNextEdgeId());
  }

  @Test
  public void testConstructTwo() {
    ImmutableEdge<Integer> e1 = factory.construct(1, 2);
    assertEquals("ImmutableEdge [source=1, dest=2, id=1]", e1.toString());
    ImmutableEdge<Integer> e2 = factory.construct(2, 3);
    assertEquals("ImmutableEdge [source=2, dest=3, id=2]", e2.toString());
    assertEquals(3, factory.getNextEdgeId());
  }

}
