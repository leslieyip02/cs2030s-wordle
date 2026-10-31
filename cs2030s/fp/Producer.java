package cs2030s.fp;

/**
 * Represent a function that produce a value.
 * 
 * @version 2026-2027 Sem 1
 * @param <T> The type of the value produced.
 */
@FunctionalInterface
public interface Producer<T> {
  /**
   * The functional method to produce a value.
   *
   * @return The value produced.
   */
  T produce();
}
