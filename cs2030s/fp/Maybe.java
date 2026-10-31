package cs2030s.fp;

import java.util.NoSuchElementException;

/**
 * Represent a box with possibly missing value.
 * 
 * @author XXX
 * @version 2026-2027 Sem 1
 */
public abstract sealed class Maybe<T> permits Maybe.Some, Maybe.None {
  private static final Maybe<?> NONE = new None();

  public static <T> Maybe<T> none() {
    @SuppressWarnings("unchecked")
    Maybe<T> res = (Maybe<T>) NONE;
    return res;
  }

  public static <T> Maybe<T> some(T val) {
    return new Some<T>(val);
  }

  public static <T> Maybe<T> of(T val) {
    if (val == null) {
      return none();
    } else {
      return some(val);
    }
  }

  protected abstract T get();

  public abstract Maybe<T> filter(BooleanCondition<? super T> cond);

  public abstract <R> Maybe<R> map(Transformer<? super T, ? extends R> func);

  public abstract <R> Maybe<R> flatMap(Transformer<? super T, ? extends Maybe<? extends R>> func);

  public abstract T orElse(T val);

  public abstract T orElseGet(Producer<? extends T> prod);

  public abstract void ifPresent(Consumer<? super T> cons);

  protected static final class Some<T> extends Maybe<T> {
    private T val;
    
    public Some(T val) {
      this.val = val;
    }

    @Override
    public String toString() {
      return "[" + this.val + "]";
    }

    @Override
    protected T get() {
      return this.val;
    }

    @Override
    public Maybe<T> filter(BooleanCondition<? super T> cond) {
      if (this.val == null) {
        return Maybe.<T>some(null);
      } else if (!cond.test(this.val)) {
        return Maybe.<T>none();
      } else {
        return this;
      }
    }

    @Override
    public <R> Maybe<R> map(Transformer<? super T, ? extends R> func) {
      return Maybe.<R>some(func.transform(this.val));
    }

    @Override
    public <R> Maybe<R> flatMap(Transformer<? super T, ? extends Maybe<? extends R>> func) {
      @SuppressWarnings("unchecked")
      Maybe<R> res = (Maybe<R>) func.transform(this.val);
      return res;
    }

    @Override
    public T orElse(T val) {
      return this.val;
    }

    @Override
    public T orElseGet(Producer<? extends T> prod) {
      return this.val;
    }

    @Override
    public void ifPresent(Consumer<? super T> cons) {
      cons.consume(this.val);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof Some<?>) {
        Some<?> some = (Some<?>) obj;
        if (this.val == null) {
          return some.val == null;
        }
        return this.val.equals(some.val);
      }
      return false;
    }
  }

  protected static final class None extends Maybe<Object> {
    @Override
    public String toString() {
      return "[]";
    }

    @Override
    protected Object get() {
      throw new NoSuchElementException();
    }

    @Override
    public Maybe<Object> filter(BooleanCondition<? super Object> cond) {
      return Maybe.<Object>none();
    }

    @Override
    public <R> Maybe<R> map(Transformer<? super Object, ? extends R> func) {
      return Maybe.<R>none();
    }

    @Override
    public <R> Maybe<R> flatMap(Transformer<? super Object, ? extends Maybe<? extends R>> func) {
      return Maybe.<R>none();
    }

    @Override
    public Object orElse(Object val) {
      return val;
    }

    @Override
    public Object orElseGet(Producer<? extends Object> prod) {
      return prod.produce();
    }

    @Override
    public void ifPresent(Consumer<? super Object> cons) {
      // do nothing
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof None;
    }
  }
}