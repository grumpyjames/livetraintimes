package net.digihippo.ltt;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class StationIndex<T>
{
    private final Map<String, Set<T>> structure;
    private final Function<T, String> toString;
    private final Function<T, String>[] canonicalPrefixes;

    public StationIndex(
        Map<String, Set<T>> structure,
        Function<T, String> toString,
        Function<T, String>[] canonicalPrefixes)
    {
        this.structure = structure;
        this.toString = toString;
        this.canonicalPrefixes = canonicalPrefixes;
    }

    public Iterable<T> search(final String expression)
    {
        if (expression.length() < 3) return Collections.emptyList();
        final String lowerCaseExpr = expression.toLowerCase();

        Set<T> set =
            structure.get(expression.toLowerCase().substring(0, 3));
        if (set == null)
        {
            return Collections.emptyList();
        }
        return Iterables.filter(set, new Predicate<T>()
        {
            @Override
            public boolean apply(T s)
            {
                boolean withinToString = toString.apply(s).toLowerCase().contains(lowerCaseExpr);
                if (!withinToString)
                {
                    for (Function<T, String> canonicalPrefix : canonicalPrefixes)
                    {
                        if (canonicalPrefix.apply(s).equals(lowerCaseExpr))
                        {
                            return true;
                        }
                    }
                }
                return withinToString;
            }
        });
    }

    public static <T> StationIndex<T> parse(
        final Iterable<T> expressions,
        final Function<T, String> toString,
        final Function<T, String>... canonicalPrefixes) throws UnsupportedEncodingException
    {
        final Builder<T> builder = new Builder<T>(toString, canonicalPrefixes);
        final char[] prefix = new char[3];
        for (T expression : expressions)
        {
            for (Function<T, String> canonicalPrefix : canonicalPrefixes)
            {
                builder.prefixFound(
                    canonicalPrefix.apply(expression).toLowerCase().toCharArray(),
                    expression);
            }
            try
            {
                int index = 0;

                String expressionStr = toString.apply(expression).toLowerCase();
                for (int i = 0; i < expressionStr.length(); i++)
                {
                    char c = expressionStr.charAt(i);
                    if (outsideAtoZ(c))
                    {
                        if (index > 2)
                        {
                            builder.prefixFound(prefix, expression);
                        }
                        // special case to allow searching for words like 'St' or 'St.'
                        if (index == 2 && c == ' ' || c == '.')
                        {
                            prefix[index] = c;
                            builder.prefixFound(prefix, expression);
                        }
                        index = 0;
                    }
                    else if (index < 3)
                    {
                        prefix[index] = c;
                        index++;
                    }
                }
                if (index > 2)
                {
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
        private final Function<T, String>[] canonicalPrefixes;

        public Builder(
            Function<T, String> toString,
            Function<T, String>[] canonicalPrefixes)
        {
            this.toString = toString;
            this.canonicalPrefixes = canonicalPrefixes;
        }

        public void prefixFound(char[] prefix, T expression)
        {
            String prefixStr = new String(prefix);
            @SuppressWarnings("unchecked")
            Set<T> set =
                structure.get(prefixStr);
            if (set == null)
            {
                HashSet<T> hashSet = new HashSet<T>();
                hashSet.add(expression);
                structure.put(prefixStr, hashSet);
            }
            else
            {
                set.add(expression);
            }
        }

        public StationIndex<T> build()
        {
            return new StationIndex<T>(
                structure,
                toString,
                canonicalPrefixes);
        }
    }

    private static boolean outsideAtoZ(char c)
    {
        return c < 'a' || 'z' < c;
    }

}
