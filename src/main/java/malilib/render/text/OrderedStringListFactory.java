package malilib.render.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;

import malilib.util.StringUtils;

public class OrderedStringListFactory
{
    public static final int DEFAULT_PRIORITY = 100;
    protected static final String AUTOMATIC_KEY_PREFIX = "_auto_";

    protected final HashMap<String, Pair<Integer, Function<List<StyledTextLine>, List<StyledTextLine>>>> providers = new HashMap<>();
    protected final List<Function<List<StyledTextLine>, List<StyledTextLine>>> sortedProviders = new ArrayList<>();
    protected ImmutableList<StyledTextLine> styledLines = ImmutableList.of();
    protected boolean dirty;
    protected boolean dynamic;
    protected int maxTextRenderWidth;

    public OrderedStringListFactory(int maxTextRenderWidth)
    {
        this.maxTextRenderWidth = maxTextRenderWidth;
    }

    /**
     * Sets the maximum render width the text should be wrapped to
     */
    public void setMaxTextRenderWidth(int maxWidth)
    {
        this.maxTextRenderWidth = maxWidth;
    }

    /**
     * Marks the contents as "dynamic", which means that the lines will be fetched
     * from the providers every time the {@link #getStyledLines()} method is called.
     * Normally (when false), the lines are only fetched once every time after the
     * {@link #markDirty()} method is called.
     */
    public void setDynamic(boolean dynamic)
    {
        this.dynamic = dynamic;
    }

    /**
     * Returns the current built list of styled text lines.
     * Calls {@link #updateList()} to rebuild the list from the current line providers,
     * if the contents are marked as dirty or dynamic.
     */
    public ImmutableList<StyledTextLine> getStyledLines()
    {
        if (this.dirty || this.dynamic)
        {
            this.updateList();
        }

        return this.styledLines;
    }

    public boolean hasNoProviders()
    {
        return this.sortedProviders.isEmpty();
    }

    public boolean hasNoStrings()
    {
        return this.dynamic == false && this.dirty == false && this.styledLines.isEmpty();
    }

    public void translateAndAddStrings(List<String> translationKeys)
    {
        List<String> translated = new ArrayList<>();

        for (String key : translationKeys)
        {
            translated.add(StringUtils.translate(key));
        }

        this.addStrings(translated);
    }

    /**
     * Adds a string by creating a string list provider of the translation of
     * the given translationKey and args. Uses the translationKey also as the key
     * for the string list provider.
     */
    public void translateAndAddString(int priority, String translationKey, Object... args)
    {
        List<String> list = Collections.singletonList(StringUtils.translate(translationKey, args));
        this.setStringListProvider(translationKey, () -> list, priority);
    }

    /**
     * Adds the provided lines, by creating a provider with an automatically generated key.
     * The lines should be already translated/localized.
     */
    public void addStrings(String... lines)
    {
        this.addStrings(Arrays.asList(lines));
    }

    /**
     * Adds the provided lines, by appending them to any previously added non-keyed lines.
     * The lines should be already translated/localized.
     */
    public void addStrings(List<String> linesIn)
    {
        this.addTextLines(StyledTextLine.parseList(linesIn));
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The lines should be already translated/localized.
     */
    public void setStringListProvider(String key, Supplier<List<String>> supplier)
    {
        this.setStringListProvider(key, supplier, DEFAULT_PRIORITY);
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The priority is the sort order of all the line suppliers,
     * they are sorted by their numerical priority (so smaller priority value comes first).
     * The lines should be already translated/localized.
     */
    public void setStringListProvider(String key, Supplier<List<String>> supplierIn, int priority)
    {
        Function<List<StyledTextLine>, List<StyledTextLine>> provider =
                (oldLines) -> StyledTextLine.parseList(supplierIn.get());

        this.providers.put(key, Pair.of(priority, provider));
        this.updateSortedProviders();
        this.markDirty();
    }

    /**
     * Adds the provided text lines, by appending them to any previously added non-keyed text lines.
     */
    public void addTextLines(StyledTextLine... textLines)
    {
        this.addTextLines(Arrays.asList(textLines));
    }

    /**
     * Adds the provided text lines, by appending them to any previously added non-keyed text lines.
     */
    public void addTextLines(List<StyledTextLine> textLines)
    {
        final List<StyledTextLine> lines = new ArrayList<>(textLines);

        if (this.providers.containsKey(AUTOMATIC_KEY_PREFIX))
        {
            List<StyledTextLine> oldLines = this.providers.get(AUTOMATIC_KEY_PREFIX).getValue().apply(Collections.emptyList());
            lines.addAll(0, oldLines);
        }

        this.setTextLineProvider(AUTOMATIC_KEY_PREFIX, (old) -> lines, DEFAULT_PRIORITY);
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     * The lines should be already translated/localized.
     */
    public void setTextLines(String key, StyledTextLine... lines)
    {
        this.setTextLines(key, Arrays.asList(lines));
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     * The lines should be already translated/localized.
     */
    public void setTextLines(String key, List<StyledTextLine> lines)
    {
        this.setTextLineProvider(key, () -> lines, DEFAULT_PRIORITY);
    }

    /**
     * Adds the provided text line supplier, by using the provided key.
     * The key can be used to remove just this provider later on.
     */
    public void setTextLineProvider(String key, Supplier<List<StyledTextLine>> supplier)
    {
        this.setTextLineProvider(key, supplier, DEFAULT_PRIORITY);
    }

    /**
     * Adds the provided text line supplier, by using the provided key.
     * The key can be used to remove just this provider later on.
     * The priority is the sort order of all the text line providers.
     * They are sorted by their numerical priority (so smaller priority value comes first).
     */
    public void setTextLineProvider(String key, Supplier<List<StyledTextLine>> supplier, int priority)
    {
        this.setTextLineProvider(key, (lines) -> supplier.get(), priority);
    }

    /**
     * Adds the provided text line supplier, by using the provided key.
     * The key can be used to remove just this provider later on.
     * The priority is the sort order of all the providers.
     * They are sorted by their numerical priority (so smaller priority value comes first).
     * The Function gets in the current list of text lines at the moment when the function is executed.
     * This allows the function to do some conditional checks before adding its own text lines,
     * for example based on the number of existing lines. As an example not adding a preceding blank
     * line if the list is currently empty.
     */
    public void setTextLineProvider(String key, Function<List<StyledTextLine>, List<StyledTextLine>> supplier, int priority)
    {
        this.providers.put(key, Pair.of(priority, supplier));
        this.updateSortedProviders();
        this.markDirty();
    }

    /**
     * Removes the line provider by the given key
     */
    public void removeTextLineProvider(String key)
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
        this.styledLines = ImmutableList.of();
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
        ArrayList<StyledTextLine> allLines = new ArrayList<>();

        for (Function<List<StyledTextLine>, List<StyledTextLine>> stringProvider : this.sortedProviders)
        {
            List<StyledTextLine> lines = stringProvider.apply(allLines);
            allLines.addAll(lines);
        }

        if (this.maxTextRenderWidth > 16)
        {
            this.styledLines = StyledTextUtils.wrapStyledTextToMaxWidth(allLines, this.maxTextRenderWidth);
        }
        else
        {
            this.styledLines = ImmutableList.copyOf(allLines);
        }

        this.dirty = false;
    }
}
