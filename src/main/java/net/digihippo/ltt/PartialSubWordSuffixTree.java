package net.digihippo.ltt;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class PartialSubWordSuffixTree {
    private final Map<String, Set<String>> structure;

    public static void main(String[] args) throws UnsupportedEncodingException {
        PartialSubWordSuffixTree tree = PartialSubWordSuffixTree.parse(
                Iterables.transform(Stations.allStations(), new Function<Station, String>() {
                    @Override
                    public String apply(Station station) {
                        return station.fullName();
                    }
                }));

        System.out.println(tree.search("Panc"));

        System.out.println(tree.search("mu"));

        System.out.println(tree.search("Gat"));
        System.out.println(tree.search("Lon"));
        System.out.println(tree.search("Pic"));
    }

    public PartialSubWordSuffixTree(Map<String, Set<String>> structure) {
        this.structure = structure;
    }

    public Iterable<String> search(final String expression)
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

        Set<String> set =
                structure.get(new String(new byte[]{indexOne, indexTwo, indexThree}));
        if (set == null)
        {
            return Collections.emptyList();
        }
        return Iterables.filter(set, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s.toLowerCase().contains(lowerCaseExpr);
            }
        });
    }

    public static PartialSubWordSuffixTree parse(
            final Iterable<String> expressions) throws UnsupportedEncodingException {
        final Builder builder = new Builder();
        final byte[] prefix = new byte[3];
        for (String expression : expressions) {
            try {
                int index = 0;
                for (byte b : expression.toLowerCase().getBytes("US-ASCII")) {
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
                throw new RuntimeException(expression, e);
            }
        }

        return builder.build();
    }

    private static class Builder
    {
        final Map<String, Set<String>> structure = new HashMap<String, Set<String>>();
        public void prefixFound(byte[] prefix, String expression) {
            String prefixStr = new String(prefix);
            @SuppressWarnings("unchecked")
            Set<String> set =
                    structure.get(prefixStr);
            if (set == null) {
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(expression);
                structure.put(prefixStr, hashSet);
            } else {
                set.add(expression);
            }
        }

        public PartialSubWordSuffixTree build() {
            return new PartialSubWordSuffixTree(structure);
        }
    }

    private static boolean outsideAtoZ(byte moved) {
        return moved < 0 || 25 < moved;
    }

}
