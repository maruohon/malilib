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
}
