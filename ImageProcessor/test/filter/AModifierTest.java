package filter;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the abstract class {@link AModifierTest}. This class creates an abstract method
 * to test subclasses that implement {@link IModifier} to ensure that they are returning an object
 * that is an {@link IModifier}.
 */
public abstract class AModifierTest {
  /**
   * This method was created to return objects that implement IModifier.
   *
   * @return IModifier object
   */
  public abstract IModifier objectCreator();

  @Test
  public void testObjectCreatedIsAnIModifier() {
    assertTrue(objectCreator() instanceof IModifier);
  }
}