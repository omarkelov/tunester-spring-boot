package com.whatever.tunester.util;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class ListUtils {
    public static <T> List<T> getPreviousAndNextItems(List<T> items, IntPredicate intPredicate) {
        if (items.isEmpty()) {
            return new ArrayList<>() {{
                add(null);
                add(null);
            }};
        }

        OptionalInt itemIdxOptional = IntStream
            .range(0, items.size())
            .filter(intPredicate)
            .findFirst();

        int itemIdx = itemIdxOptional.isPresent()
            ? itemIdxOptional.getAsInt()
            : new Random().nextInt(0, items.size());

        T previousItem = itemIdx > 0
            ? items.get(itemIdx - 1)
            : items.get(items.size() - 1);

        T nextItem = itemIdx < items.size() - 1
            ? items.get(itemIdx + 1)
            : items.get(0);

        return new ArrayList<>() {{
            add(previousItem);
            add(nextItem);
        }};
    }
}
