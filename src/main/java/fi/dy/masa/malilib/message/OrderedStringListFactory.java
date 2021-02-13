package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.collect.ImmutableList;

public class OrderedStringListFactory
{
    protected static final String AUTOMATIC_KEY_PREFIX = "_auto_";

    protected final HashMap<String, Pair<Integer, Function<List<String>, List<String>>>> providers = new HashMap<>();
    protected final List<Function<List<String>, List<String>>> sortedProviders = new ArrayList<>();
    protected ImmutableList<String> lines = ImmutableList.of();
    protected boolean dirty;

    /**
     * Returns the current built list of strings.
     * Call {@link #updateList()} to rebuild the list from the current line providers.
     * @return
     */
    public ImmutableList<String> getLines()
    {
        if (this.dirty)
        {
            this.updateList();
        }

        return this.lines;
    }

    public boolean isEmpty()
    {
        return this.sortedProviders.isEmpty();
    }

    /**
     * Adds the provided lines, by creating a provider with an automatically generated key.
     * The lines should be already translated/localized.
     * @param lines
     */
    public void addLines(String... lines)
    {
        this.addLines(Arrays.asList(lines));
    }

    /**
     * Adds the provided lines, by appending them to any previously added non-keyed lines.
     * The lines should be already translated/localized.
     * @param linesIn
     */
    public void addLines(List<String> linesIn)
    {
        if (this.providers.containsKey(AUTOMATIC_KEY_PREFIX))
        {
            List<String> newLines = new ArrayList<>(this.providers.get(AUTOMATIC_KEY_PREFIX).getValue().apply(Collections.emptyList()));
            newLines.addAll(linesIn);
            linesIn = newLines;
        }

        final List<String> lines = linesIn;

        this.setStringListProvider(AUTOMATIC_KEY_PREFIX, () -> lines);
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     * The lines should be already translated/localized.
     */
    public void setLines(String key, String... lines)
    {
        this.setStringListProvider(key, () -> Arrays.asList(lines));
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     * The lines should be already translated/localized.
     */
    public void setLines(String key, List<String> lines)
    {
        this.setStringListProvider(key, () -> lines);
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The lines should be already translated/localized.
     */
    public void setStringListProvider(String key, Supplier<List<String>> supplier)
    {
        this.setStringListProvider(key, supplier, 100);
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The priority is the sort order of all the line suppliers,
     * they are sorted by their numerical priority (so smaller priority value comes first).
     * The lines should be already translated/localized.
     */
    public void setStringListProvider(String key, Supplier<List<String>> supplier, int priority)
    {
        this.setStringListProvider(key, (lines) -> supplier.get(), priority);
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The priority is the sort order of all the line suppliers,
     * they are sorted by their numerical priority (so smaller priority value comes first).
     * The Function gets in the current list of strings at the moment when the function is executed.
     * This allows the function to do some conditional checks before adding its own lines,
     * for example based on the number of existing lines. As an example not adding a preceding blank
     * line if the list is currently empty.
     * The lines should be already translated/localized.
     */
    public void setStringListProvider(String key, Function<List<String>, List<String>> supplier, int priority)
    {
        this.providers.put(key, Pair.of(priority, supplier));
        this.updateSortedProviders();
        this.markDirty();
    }

    /**
     * Removes the line supplier by the given key
     */
    public void removeStringListProvider(String key)
    {
        this.providers.remove(key);
        this.updateSortedProviders();
        this.markDirty();
    }

    /**
     * Removes the line supplier that was added using the simple key-less adder methods.
     */
    public void removeKeyless()
    {
        this.providers.remove(AUTOMATIC_KEY_PREFIX);
        this.markDirty();
    }

    /**
     * Removes all line suppliers
     */
    public void removeAll()
    {
        this.providers.clear();
        this.lines = ImmutableList.of();
    }

    /**
     * Marks the providers dirty, to cause a re-build of the string list when next requested
     */
    public void markDirty()
    {
        this.dirty = true;
    }

    protected void updateSortedProviders()
    {
        this.sortedProviders.clear();
        this.providers.values().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach((p) -> this.sortedProviders.add(p.getValue()));
    }

    /**
     * Rebuilds the list of strings from the current line providers
     */
    public void updateList()
    {
        ArrayList<String> allLines = new ArrayList<>();

        for (Function<List<String>, List<String>> stringProvider : this.sortedProviders)
        {
            List<String> lines = stringProvider.apply(allLines);
            allLines.addAll(lines);
        }

        this.lines = ImmutableList.copyOf(allLines);
        this.dirty = false;
    }
}
