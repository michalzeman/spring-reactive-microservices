package com.mz.reactivedemo.common.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Try<T> {

  static <T> Try<T> of(SupplierThrowable<T> f) {
    try {
      Objects.requireNonNull(f);
      return new Success<>(f.get());
    } catch (Throwable error) {
      return new Failure<>(error);
    }
  }

  static <T> Try<T> error(Throwable error) {
    return new Failure<>(error);
  }

  boolean isSuccess();

  boolean isFailure();

  Optional<T> toOptional();

  <R> Try<R> map(FunctionThrowable<T, R> f);

  <R> Try<R> flatMap(FunctionThrowable<T, Try<R>> f);

  Try<T> onFailure(Consumer<Throwable> f);

  Try<T> onSuccess(Consumer<T> f);

  T getOrElse(Supplier<T> f);

  T get();

  @FunctionalInterface
  interface FunctionThrowable<T, R> extends Function<T, R> {

    @Override
    default R apply(T t) {
      try {
        return this.applyWithThrow(t);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }

    R applyWithThrow(T t) throws Throwable;

  }

  @FunctionalInterface
  interface SupplierThrowable<T> extends Supplier<T> {

    default T get() {
      try {
        return this.getThrowable();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }

    T getThrowable() throws Throwable;

  }

  class Success<T> implements Try<T> {

    private final T result;

    private Success(T result) {
      this.result = result;
    }

    @Override
    public T get() {
      return this.result;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public boolean isFailure() {
      return false;
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.of(result);
    }

    @Override
    public <R> Try<R> map(FunctionThrowable<T, R> f) {
      try {
        Objects.requireNonNull(result);
        R resultMap = f.apply(result);
        Objects.requireNonNull(resultMap);
        return new Success<>(resultMap);
      } catch (Throwable error) {
        return new Failure<>(error);
      }
    }

    @Override
    public <R> Try<R> flatMap(FunctionThrowable<T, Try<R>> f) {
      try {
        Objects.requireNonNull(f);
        return f.apply(this.result);
      } catch (Throwable error) {
        return new Failure<>(error);
      }
    }

    @Override
    public Try<T> onFailure(Consumer<Throwable> f) {
      return this;
    }

    @Override
    public Try<T> onSuccess(Consumer<T> f) {
      Objects.requireNonNull(f);
      f.accept(result);
      return this;
    }

    @Override
    public T getOrElse(Supplier<T> f) {
      return result;
    }
  }

  class Failure<T> implements Try<T> {

    private final Throwable error;

    private Failure(Throwable error) {
      this.error = error;
    }

    public Throwable error() {
      return this.error;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isFailure() {
      return true;
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.empty();
    }

    @Override
    public <R> Try<R> map(FunctionThrowable<T, R> f) {
      return (Try<R>) this;
    }

    @Override
    public <R> Try<R> flatMap(FunctionThrowable<T, Try<R>> f) {
      return (Try<R>) this;
    }

    @Override
    public Try<T> onFailure(Consumer<Throwable> f) {
      Objects.requireNonNull(f);
      f.accept(error);
      return this;
    }

    @Override
    public Try<T> onSuccess(Consumer<T> f) {
      return this;
    }

    @Override
    public T getOrElse(Supplier<T> f) {
      return f.get();
    }

    @Override
    public T get() {
      throw new NoSuchElementException(error.getMessage());
    }
  }
}
