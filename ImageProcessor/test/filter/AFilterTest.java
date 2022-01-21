package filter;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the abstract class {@link AFilter} and extends {@link AModifierTest}.
 * This class creates an abstract method to test classes that implement {@link AFilter} to ensure
 * that they are returning an object that is an {@link AFilter}.
 */
public abstract class AFilterTest extends AModifierTest {
  /**
   * This method was created to return objects that extend AFilter.
   *
   * @return AFilter object
   */
  public abstract AFilter objectFilterCreator();

  @Test
  public void testObjectCreatedIsAnAFilter() {
    assertTrue(objectFilterCreator() instanceof AFilter);
  }
}