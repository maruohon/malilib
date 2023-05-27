package malilib.render.text;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLib;
import malilib.util.StringUtils;

public class StyledText
{
    protected static final Cache<StyledTextCacheKey, StyledText> TEXT_CACHE = CacheBuilder.newBuilder().concurrencyLevel(1).initialCapacity(4000).maximumSize(4000).expireAfterAccess(10 * 60, TimeUnit.SECONDS).build();

    public final ImmutableList<StyledTextLine> lines;
    private int renderWidth = -1;

    public StyledText(ImmutableList<StyledTextLine> lines)
    {
        this.lines = lines;
    }

    public ImmutableList<StyledTextLine> getLines()
    {
        return this.lines;
    }

    public int getRenderWidth()
    {
        if (this.renderWidth < 0)
        {
            int width = 0;

            for (StyledTextLine line : this.lines)
            {
                width = Math.max(width, line.renderWidth);
            }

            this.renderWidth = width;
        }

        return this.renderWidth;
    }

    public StyledText append(List<StyledTextLine> lines)
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        builder.addAll(this.lines);
        builder.addAll(lines);

        return new StyledText(builder.build());
    }

    public StyledText append(int blankLinesBefore, List<StyledTextLine> lines, int blankLinesAfter)
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        builder.addAll(this.lines);
        for (int i = 0; i < blankLinesBefore; ++i) { builder.add(StyledTextLine.EMPTY); }
        builder.addAll(lines);
        for (int i = 0; i < blankLinesAfter; ++i) { builder.add(StyledTextLine.EMPTY); }

        return new StyledText(builder.build());
    }

    /**
     * @return a copy of the text without a style (or rather with the default style)
     */
    public StyledText withoutStyle()
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        for (StyledTextLine line : this.lines)
        {
            builder.add(line.withoutStyle());
        }

        return new StyledText((builder.build()));
    }

    @Override
    public String toString()
    {
        return "StyledText{" + this.lines.toString() + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        StyledText that = (StyledText) o;

        return this.lines.equals(that.lines);
    }

    @Override
    public int hashCode()
    {
        return this.lines.hashCode();
    }

    public static void clearCache()
    {
        StyledTextLine.TEXT_CACHE.invalidateAll();
        TEXT_CACHE.invalidateAll();
    }

    public static StyledText ofLines(ImmutableList<StyledTextLine> lines)
    {
        return new StyledText(lines);
    }

    public static StyledText parseList(List<String> strings)
    {
        return new StyledText(StyledTextLine.parseList(strings));
    }

    public static StyledText translate(String translationKey, Object... args)
    {
        return parse(StringUtils.translate(translationKey, args));
    }

    public static StyledText parse(String str)
    {
        return parse(str, Optional.empty());
    }

    public static StyledText parse(String str, TextStyle startingStyle)
    {
        return parse(str, Optional.of(startingStyle));
    }

    protected static StyledText parse(String str, Optional<TextStyle> startingStyle)
    {
        try
        {
            TextStyle style = startingStyle.isPresent() ? startingStyle.get() : null;
            StyledTextCacheKey key = new StyledTextCacheKey(str, style);
            return TEXT_CACHE.get(key, () -> new StyledText(StyledTextLine.parseLines(str, startingStyle)));
        }
        catch (ExecutionException e)
        {
            MaLiLib.LOGGER.warn("Exception while retrieving StyledText from cache", e);
            return new StyledText(StyledTextLine.parseLines(str, startingStyle));
        }
    }

    public static StyledTextBuilder builder()
    {
        return new StyledTextBuilder();
    }

    public static StyledTextBuilder builder(TextStyle startingStyle)
    {
        return new StyledTextBuilder(startingStyle);
    }

    public static class StyledTextCacheKey
    {
        public final String text;
        @Nullable public final TextStyle startingStyle;

        public StyledTextCacheKey(String text, @Nullable TextStyle startingStyle)
        {
            this.text = text;
            this.startingStyle = startingStyle;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || this.getClass() != o.getClass()) { return false; }

            StyledTextCacheKey cacheKey = (StyledTextCacheKey) o;

            if (!this.text.equals(cacheKey.text)) { return false; }
            return Objects.equals(this.startingStyle, cacheKey.startingStyle);
        }

        @Override
        public int hashCode()
        {
            int result = this.text.hashCode();
            result = 31 * result + (this.startingStyle != null ? this.startingStyle.hashCode() : 0);
            return result;
        }
    }
}
