package net.digihippo.ltt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StationIndex<T>
{
    private final Map<String, Set<T>> structure;
    private final Function<T, String> toString;
    private final Function<T, String>[] canonicalPrefixes;

    private StationIndex(
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

        final List<T> result = new ArrayList<>();
        for (T s : set)
        {
            boolean withinToString = toString.apply(s).toLowerCase().contains(lowerCaseExpr);
            if (!withinToString)
            {
                for (Function<T, String> canonicalPrefix : canonicalPrefixes)
                {
                    if (canonicalPrefix.apply(s).toLowerCase().equals(lowerCaseExpr))
                    {
                        result.add(s);
                        break;
                    }
                }
            }
            else
            {
                result.add(s);
            }
        }
        return result;
    }

    @SafeVarargs
    public static <T> StationIndex<T> parse(
        final Iterable<T> expressions,
        final Function<T, String> toString,
        final Function<T, String>... canonicalPrefixes)
    {
        final Builder<T> builder = new Builder<>(toString, canonicalPrefixes);
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
        private final Map<String, Set<T>> structure = new HashMap<>();
        private final Function<T, String> toString;
        private final Function<T, String>[] canonicalPrefixes;

        Builder(
            Function<T, String> toString,
            Function<T, String>[] canonicalPrefixes)
        {
            this.toString = toString;
            this.canonicalPrefixes = canonicalPrefixes;
        }

        void prefixFound(char[] prefix, T expression)
        {
            String prefixStr = new String(prefix);
            Set<T> set =
                structure.get(prefixStr);
            if (set == null)
            {
                HashSet<T> hashSet = new HashSet<>();
                hashSet.add(expression);
                structure.put(prefixStr, hashSet);
            }
            else
            {
                set.add(expression);
            }
        }

        StationIndex<T> build()
        {
            return new StationIndex<>(
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
