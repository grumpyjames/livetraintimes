package net.digihippo.ltt;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class StationIndex<T> {
    private final Map<String, Set<T>> structure;
    private final Function<T, String> toString;

    public StationIndex(
            Map<String, Set<T>> structure,
            Function<T, String> toString) {
        this.structure = structure;
        this.toString = toString;
    }

    public Iterable<T> search(final String expression)
    {
        if (expression.length() < 3) return Collections.emptyList();
        final String lowerCaseExpr = expression.toLowerCase();
        byte[] bytes = lowerCaseExpr.getBytes();

        byte indexOne = (byte) (bytes[0] - 97);
        byte indexTwo = (byte) (bytes[1] - 97);
        byte indexThree = (byte) (bytes[2] - 97);
        if (outsideAtoZ(indexOne) || outsideAtoZ(indexTwo) || outsideAtoZ(indexThree))
        {
            return Collections.emptyList();
        }

        Set<T> set =
                structure.get(new String(new byte[]{indexOne, indexTwo, indexThree}));
        if (set == null)
        {
            return Collections.emptyList();
        }
        return Iterables.filter(set, new Predicate<T>() {
            @Override
            public boolean apply(T s) {
                return toString.apply(s).toLowerCase().contains(lowerCaseExpr);
            }
        });
    }

    public static <T> StationIndex<T> parse(
            final Iterable<T> expressions,
            final Function<T, String> toString,
            final Function<T, String> ... canonicalPrefixes) throws UnsupportedEncodingException {
        final Builder<T> builder = new Builder<T>(toString);
        final byte[] prefix = new byte[3];
        for (T expression : expressions) {
            for (Function<T, String> canonicalPrefix : canonicalPrefixes) {
                builder.prefixFound(
                        canonicalPrefix.apply(expression).toLowerCase().getBytes("US-ASCII"),
                        expression);
            }
            try {
                int index = 0;
                for (byte b : toString.apply(expression).toLowerCase().getBytes("US-ASCII")) {
                    byte moved = (byte)(b - 97);
                    if (outsideAtoZ(moved)) {
                        if (index > 2) {
                            builder.prefixFound(prefix, expression);
                        }
                        index = 0;
                    } else if (index < 3) {
                        prefix[index] = moved;
                        index++;
                    }
                }
                if (index > 2) {
                    builder.prefixFound(prefix, expression);
                }
            } catch (Exception e)
            {
                throw new RuntimeException(toString.apply(expression), e);
            }
        }

        return builder.build();
    }

    private static class Builder<T>
    {
        private final Map<String, Set<T>> structure = new HashMap<String, Set<T>>();
        private final Function<T, String> toString;

        public Builder(Function<T, String> toString) {
            this.toString = toString;
        }

        public void prefixFound(byte[] prefix, T expression) {
            String prefixStr = new String(prefix);
            @SuppressWarnings("unchecked")
            Set<T> set =
                    structure.get(prefixStr);
            if (set == null) {
                HashSet<T> hashSet = new HashSet<T>();
                hashSet.add(expression);
                structure.put(prefixStr, hashSet);
            } else {
                set.add(expression);
            }
        }

        public StationIndex<T> build() {
            return new StationIndex<T>(
                    structure,
                    toString);
        }
    }

    private static boolean outsideAtoZ(byte moved) {
        return moved < 0 || 25 < moved;
    }

}
