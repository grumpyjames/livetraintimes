package net.digihippo.ltt;

import java.io.Serializable;
import java.util.Date;

public abstract class Either<A,B> implements Serializable {
    public abstract <T> T either(final Function<A, T> onA, final Function<B, T> onB);
    public abstract void consume(final Consumer<A> onA, final Consumer<B> onB);

    private Either() {}

    static <A, B> Either<A, B> left(final A a) {
        return new Left<>(a);
    }

    static <A, B> Either<A, B> right(final B b) {
        return new Right<>(b);
    }

    boolean isRight()
    {
        return either(new Function<A, Boolean>()
        {
            @Override
            public Boolean apply(A a)
            {
                return false;
            }
        }, new Function<B, Boolean>()
        {
            @Override
            public Boolean apply(B b)
            {
                return true;
            }
        });
    }

    B unwrap()
    {
        return either(new Function<A, B>()
        {
            @Override
            public B apply(A a)
            {
                throw new IllegalStateException();
            }
        }, new Function<B, B>()
        {
            @Override
            public B apply(B b)
            {
                return b;
            }
        });
    }

    private static final class Left<A, B> extends Either<A, B> {
        private final A a;

        private Left(A a) {
            this.a = a;
        }

        @Override
        public <T> T either(Function<A, T> onA, Function<B, T> onB) {
            return onA.apply(a);
        }

        @Override
        public void consume(Consumer<A> onA, Consumer<B> onB) {
            onA.consume(a);
        }
    }

    private static final class Right<A, B> extends Either<A, B> {
        private final B b;

        private Right(B b) {
            this.b = b;
        }

        @Override
        public <T> T either(Function<A, T> onA, Function<B, T> onB) {
            return onB.apply(b);
        }

        @Override
        public void consume(Consumer<A> onA, Consumer<B> onB) {
            onB.consume(b);
        }
    }
}
