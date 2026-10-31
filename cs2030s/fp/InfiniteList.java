package cs2030s.fp;

import java.util.ArrayList;
import java.util.List;

public class InfiniteList<T> {
  private static final InfiniteList<?> SENTINEL = new Sentinel();

  private final Lazy<Maybe<T>> head;
  private final Lazy<InfiniteList<T>> tail;

  private InfiniteList() {
    this.head = null;
    this.tail = null;
  }

  public static <T> InfiniteList<T> generate(Producer<T> producer) {
    return new InfiniteList<>(
          Lazy.of(() -> Maybe.some(producer.produce())),
          Lazy.of(() -> InfiniteList.generate(producer))
        );
  }

  public static <T> InfiniteList<T> iterate(T seed, Transformer<T, T> next) {
    return new InfiniteList<>(
          seed,
          () -> InfiniteList.iterate(next.transform(seed), next)
        );
  }

  private InfiniteList(T head, Producer<InfiniteList<T>> tail) {
    this.head = Lazy.of(Maybe.some(head));
    this.tail = Lazy.of(tail);
  }

  private InfiniteList(Lazy<Maybe<T>> head, Lazy<InfiniteList<T>> tail) {
    this.head = head;
    this.tail = tail;
  }

  public T head() {
    return this.head.get().orElseGet(() -> this.tail.get().head());
  }

  public InfiniteList<T> tail() {
    return this.head.get()
                    .map(maybe -> this.tail.get())
                    .orElseGet(() -> this.tail.get().tail());
  }

  public <R> InfiniteList<R> map(Transformer<? super T, ? extends R> mapper) {
    return new InfiniteList<>(
          this.head.map(maybe -> maybe.map(mapper)),
          this.tail.map(inf -> inf.map(mapper))
        );
  }

  public InfiniteList<T> filter(BooleanCondition<? super T> predicate) {
    return new InfiniteList<>(
          this.head.map(maybe -> maybe.filter(predicate)),
          this.tail.map(inf -> inf.filter(predicate))
        );
  }

  public static <T> InfiniteList<T> sentinel() {
    @SuppressWarnings("unchecked")
    InfiniteList<T> res = (InfiniteList<T>) InfiniteList.SENTINEL;
    return res;
  }

  public InfiniteList<T> limit(long n) {
    return Maybe.of(n).filter(num -> num > 0)
                .map(num -> new InfiniteList<>(
                      this.head,
                      this.tail.map(inf -> inf.limit(
                            this.head.get().map(m -> n - 1).orElse(n))
                          )
                    ))
                .orElseGet(() -> InfiniteList.sentinel());
  }

  public InfiniteList<T> takeWhile(BooleanCondition<? super T> predicate) {
    Lazy<Boolean> pass = Lazy.of(() -> this.head.get().map(elem -> predicate.test(elem)).orElse(true));
    return new InfiniteList<>(
          Lazy.of(() -> this.head.get().filter(elem -> pass.get())),
          Lazy.of(() -> this.head.get()
                            .filter(elem -> !pass.get())
                            .map(elem -> InfiniteList.<T>sentinel())
                            .orElseGet(() -> this.tail.get().takeWhile(predicate)))
        );
  }

  public boolean isSentinel() {
    return false;
  }

  public <U> U reduce(U identity, Combiner<U, ? super T, U> accumulator) {
    U res = identity;
    InfiniteList<T> list = this;
    while (!list.isSentinel()) {
      U tmp = res;
      res = list.head.get()
                .map(elem -> accumulator.combine(tmp, elem))
                .orElse(res);
      list = list.tail.get();
    }
    return res;
  }

  public long count() {
    return this.reduce(0L, (acc, elem) -> acc + 1L);
  }

  public List<T> toList() {
    return this.head.get()
               .map(elem -> {
                    List<T> tmp = new ArrayList<>();
                    tmp.add(elem);
                    tmp.addAll(
                        this.head.get()
                            .map(el -> this.tail.get().toList())
                            .orElse(List.<T>of()));
                    return tmp;
                  })
               .orElseGet(() -> {
                    List<T> tmp = new ArrayList<>();
                    tmp.addAll(this.tail.get().toList());
                    return tmp;
                  });
  }

  protected InfiniteList<T> append(InfiniteList<T> list) {
    return new InfiniteList<>(
          this.head,
          this.tail.map(inf -> inf.append(list))
        );
  }

  public <U> InfiniteList<U> flatMap(Transformer<? super T, ? extends InfiniteList<? extends U>> transformer) {
    return new InfiniteList<>(
          Lazy.of(() -> Maybe.none()),
          Lazy.of(() -> this.head.get()
                            .map(elem -> transformer.transform(elem).<U>map(el -> el))
                            .orElseGet(() -> InfiniteList.<U>sentinel())
                            .append(new InfiniteList<>(
                                  Lazy.of(() -> Maybe.none()),
                                  Lazy.of(() -> this.tail.get().flatMap(transformer))
                                )))
        );
  }

  @Override
  public String toString() {
    return "[" + this.head + " " + this.tail + "]";
  }

  private static class Sentinel extends InfiniteList<Object> {

    @Override
    public Object head() {
      throw new java.util.NoSuchElementException();
    }

    @Override
    public InfiniteList<Object> tail() {
      throw new java.util.NoSuchElementException();
    }

    @Override
    public <R> InfiniteList<R> map(Transformer<? super Object, ? extends R> mapper) {
      return InfiniteList.sentinel();
    }

    @Override
    public <R> InfiniteList<R> flatMap(Transformer<? super Object, ? extends InfiniteList<? extends R>> mapper) {
      return InfiniteList.sentinel();
    }

    @Override
    public InfiniteList<Object> filter(BooleanCondition<? super Object> predicate) {
      return InfiniteList.sentinel();
    }

    @Override
    public InfiniteList<Object> limit(long n) {
      return InfiniteList.sentinel();
    }

    @Override
    public List<Object> toList() {
      return List.<Object>of();
    }

    @Override
    public InfiniteList<Object> takeWhile(BooleanCondition<? super Object> predicate) {
      return InfiniteList.sentinel();
    }

    @Override
    protected InfiniteList<Object> append(InfiniteList<Object> list) {
      return list;
    }

    @Override
    public boolean isSentinel() {
      return true;
    }

    @Override
    public String toString() {
      return "-";
    }
  }
}
