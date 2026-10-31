package cs2030s.fp;

public class Lazy<T> {
  private Producer<? extends T> producer;
  private Maybe<T> value;

  private Lazy(Producer<? extends T> producer, Maybe<T> value) {
    this.producer = producer;
    this.value = value;
  }

  public static <T> Lazy<T> of(T val) {
    return new Lazy<T>(() -> val, Maybe.some(val));
  }

  public static <T> Lazy<T> of(Producer<? extends T> prod) {
    return new Lazy<T>(prod, Maybe.none());
  }

  public T get() {
    this.value = Maybe.some(this.value.orElseGet(this.producer));
    return this.value.orElse(null); // safe as this is `Some`
  }

  public <R> Lazy<R> map(Transformer<? super T, ? extends R> func) {
    return new Lazy<R>(() -> func.transform(this.get()), Maybe.none());
  }

  public <R> Lazy<R> flatMap(Transformer<? super T, ? extends Lazy<? extends R>> func) {
    return new Lazy<R>(() -> func.transform(this.get()).get(), Maybe.none());
  }

  public Lazy<Boolean> filter(BooleanCondition<? super T> pred) {
    return new Lazy<Boolean>(() -> pred.test(this.get()), Maybe.none());
  }

  public <R, S> Lazy<S> combine(Lazy<? extends R> lazy, Combiner<? super T, ? super R, ? extends S> comb) {
    return new Lazy<S>(() -> comb.combine(this.get(), lazy.get()), Maybe.none());
  }

  @Override
  public String toString() {
    return this.value.map(t -> String.valueOf(t)).orElse("?");
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Lazy<?>)) {
      return false;
    }
    Lazy<?> lz = (Lazy<?>) obj;
    if (lz.get() == null) {
      return this.get() == null;
    }
    return lz.get().equals(this.get());
  }
}
