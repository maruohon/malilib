package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListUtils
{
    /**
     * Filters entries from the first list by the given Predicate and adds them to the second list.
     * If <b>removeMatched</b> is true, then the matched entries are also removed from the first list.
     * <br><br>
     * Note: If removeMatched is true, then the first list will be cleared and re-filled!
     */
    public static <T> void extractEntriesToSecondList(List<T> listFrom,
                                                      List<T> listTo,
                                                      Predicate<T> filter,
                                                      boolean removeMatched)
    {
        ArrayList<T> newList = new ArrayList<>();

        for (T entry : listFrom)
        {
            boolean matches = filter.test(entry);

            if (matches)
            {
                listTo.add(entry);
            }
            else if (removeMatched)
            {
                newList.add(entry);
            }
        }

        if (removeMatched)
        {
            listFrom.clear();
            listFrom.addAll(newList);
        }
    }

    /**
     * Returns either the next or the previous entry in the list, depending on the reverse argument.
     */
    public static <T> T getNextEntry(List<T> list, T currentValue, boolean reverse)
    {
        return getNextEntry(list, currentValue, reverse, (v) -> true);
    }

    /**
     * Returns either the next or the previous entry in the list, depending on the reverse argument,
     * that passes the test in predicate.
     */
    public static <T> T getNextEntry(List<T> list, T currentValue, boolean reverse, Predicate<T> predicate)
    {
        final int size = list.size();

        if (size > 1)
        {
            int newIndex = list.indexOf(currentValue);

            if (newIndex != -1)
            {
                final int maxIndex = size - 1;
                final int increment = (reverse ? -1 : 1);

                for (int i = 0; i < size; ++i)
                {
                    newIndex += increment;

                    if (newIndex >= size)
                    {
                        newIndex = 0;
                    }
                    else if (newIndex < 0)
                    {
                        newIndex = maxIndex;
                    }

                    T tmp = list.get(newIndex);

                    if (predicate.test(tmp))
                    {
                        return tmp;
                    }
                }
            }
        }

        return currentValue;
    }
}
