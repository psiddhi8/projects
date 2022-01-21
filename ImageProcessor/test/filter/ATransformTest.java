package filter;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the abstract class {@link ATransform} and extends {@link AModifierTest}.
 * This class creates an abstract method to test classes that implement {@link ATransform} to ensure
 * that they are returning an object that is an {@link ATransform}.
 */
public abstract class ATransformTest extends AModifierTest {
  /**
   * This method was created to return objects that extend ATransform.
   *
   * @return ATransform object
   */
  public abstract ATransform objectTransformCreator();

  @Test
  public void testObjectCreatedIsAnATransform() {
    assertTrue(objectTransformCreator() instanceof ATransform);
  }
}