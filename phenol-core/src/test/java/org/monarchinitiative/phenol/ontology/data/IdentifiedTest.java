package org.monarchinitiative.phenol.ontology.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IdentifiedTest {

  /**
   *
   * Two-pronged test. First, test that {@link Identified#id()} is serialized as an "id" string field.
   * Second, test that a list of {@link TermId}s is serialized as a list of string values (NOT JSON objects).
   */
  @Test
  public void testSerialization() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    Identified id = new SimpleIdentified(
      TermId.of("BLA:1234567"),
      List.of(TermId.of("BLA:999"), TermId.of("BLA:888")),
      "Jimmy",
      15,
      true);


    String actual = mapper.writeValueAsString(id);
    assertThat(actual, equalTo("{\"id\":\"BLA:1234567\",\"altTermIds\":[\"BLA:999\",\"BLA:888\"],\"name\":\"Jimmy\",\"age\":15,\"alive\":true}"));
  }

  public static class SimpleIdentified implements Identified {

    private final TermId id;
    private final List<TermId> altTermIds;
    private final String name;
    private final int age;
    private final boolean alive;

    private SimpleIdentified(TermId id, List<TermId> altTermIds, String name, int age, boolean alive) {
      this.id = id;
      this.altTermIds = altTermIds;
      this.name = name;
      this.age = age;
      this.alive = alive;
    }


    @Override
    public TermId id() {
      return id;
    }

    public List<TermId> getAltTermIds() {
      return altTermIds;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    public boolean isAlive() {
      return alive;
    }
  }

}
