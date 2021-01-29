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
import fi.dy.masa.malilib.util.StringUtils;

public class OrderedStringListFactory
{
    protected static final String AUTOMATIC_KEY_PREFIX = "_auto_";

    protected final HashMap<String, Pair<Integer, Function<List<String>, List<String>>>> providers = new HashMap<>();
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

    /**
     * Adds the provided lines, by creating a provider with an automatically generated key
     * @param lines
     */
    public void addLines(String... lines)
    {
        this.addLines(Arrays.asList(lines));
    }

    /**
     * Adds the provided lines, by appending them to any previously added non-keyed lines
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
     */
    public void setLines(String key, String... lines)
    {
        this.setStringListProvider(key, () -> Arrays.asList(lines));
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     */
    public void setLines(String key, List<String> lines)
    {
        this.setStringListProvider(key, () -> lines);
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
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
     */
    public void setStringListProvider(String key, Function<List<String>, List<String>> supplier, int priority)
    {
        this.providers.put(key, Pair.of(priority, supplier));
        this.markDirty();
    }

    /**
     * Removes the line supplier by the given key
     */
    public void removeStringListProvider(String key)
    {
        this.providers.remove(key);
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

    /**
     * Rebuilds the list of strings from the current line providers
     */
    public void updateList()
    {
        ArrayList<Pair<Integer, Function<List<String>, List<String>>>> providers = new ArrayList<>(this.providers.values());
        providers.sort(java.util.Map.Entry.comparingByKey());

        ArrayList<String> allLines = new ArrayList<>();

        for (Pair<Integer, Function<List<String>, List<String>>> pair : providers)
        {
            List<String> rawLines = pair.getRight().apply(allLines);

            for (String line : rawLines)
            {
                String[] parts = StringUtils.translate(line).split("\\n");
                allLines.addAll(Arrays.asList(parts));
            }
        }

        this.lines = ImmutableList.copyOf(allLines);
        this.dirty = false;
    }

    /*
    protected String generateUnusedKey()
    {
        int size = this.providers.size();
        int suffix = size;
        String key = AUTOMATIC_KEY_PREFIX + suffix;

        for (int i = 0; i < size; ++i)
        {
            if (this.providers.containsKey(key) == false)
            {
                return key;
            }

            ++suffix;
            key = AUTOMATIC_KEY_PREFIX + suffix;
        }

        return key;
    }
    */
}
