package cs2030s.fp;

/**
 * Represent a function that consumes a value.
 * 
 * @version 2026-2027 Sem 1
 * @param <T> The type of the value consumed.
 */
@FunctionalInterface
public interface Consumer<T> {
  /**
   * The functional method to consume a value.
   *
   * @param t The value consumed.
   */
  void consume(T t);
}
