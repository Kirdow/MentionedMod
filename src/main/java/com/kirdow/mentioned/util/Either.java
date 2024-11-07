package com.kirdow.mentioned.util;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L, R> {
    private Either() {}

    public abstract <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);
    public abstract void on(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer);
    public abstract void ifLeft(Consumer<? super L> leftConsumer);
    public abstract void ifRight(Consumer<? super R> rightConsumer);
    public abstract boolean isLeft(Ref<? super L> out);
    public abstract boolean isRight(Ref<? super R> out);

    public static <L, R> Either<L, R> ofLeft(L left) {
        return new Left<>(left);
    }

    public static <L, R> Either<L, R> ofRight(R right) {
        return new Right<>(right);
    }

    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
            return leftMapper.apply(value);
        }

        @Override
        public void on(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer) {
            leftConsumer.accept(value);
        }

        @Override
        public void ifLeft(Consumer<? super L> leftConsumer) {
            leftConsumer.accept(value);
        }

        @Override
        public void ifRight(Consumer<? super R> rightConsumer) {}

        @Override
        public boolean isLeft(Ref<? super L> out) {
            if (out != null) {
                out.value = value;
            }

            return true;
        }

        @Override
        public boolean isRight(Ref<? super R> out) {
            return false;
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
            return rightMapper.apply(value);
        }

        @Override
        public void on(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer) {
            rightConsumer.accept(value);
        }

        @Override
        public void ifLeft(Consumer<? super L> leftConsumer) {}

        @Override
        public void ifRight(Consumer<? super R> rightConsumer) {
            rightConsumer.accept(value);
        }

        @Override
        public boolean isLeft(Ref<? super L> out) {
            return false;
        }

        @Override
        public boolean isRight(Ref<? super R> out) {
            if (out != null) {
                out.value = value;
            }

            return true;
        }
    }
}