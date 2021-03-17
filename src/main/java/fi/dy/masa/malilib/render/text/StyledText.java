package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class StyledText
{
    protected static final Cache<CacheKey, StyledText> TEXT_CACHE = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(1000).expireAfterAccess(15 * 60, TimeUnit.SECONDS).build();

    public final ImmutableList<StyledTextLine> lines;

    public StyledText(ImmutableList<StyledTextLine> lines)
    {
        this.lines = lines;
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
        TEXT_CACHE.invalidateAll();
    }

    public static StyledText translatedOf(String translationKey, Object... args)
    {
        return of(StringUtils.translate(translationKey, args));
    }

    public static StyledText of(String str)
    {
        try
        {
            CacheKey key = new CacheKey(str, null);
            return TEXT_CACHE.get(key, () -> StyledTextParser.parseString(str));
        }
        catch (ExecutionException e)
        {
            MaLiLib.LOGGER.warn("Exception while retrieving StyledText from cache", e);
            return StyledTextParser.parseString(str);
        }
    }

    public static StyledText of(String str, TextStyle startingStyle)
    {
        try
        {
            CacheKey key = new CacheKey(str, startingStyle);
            return TEXT_CACHE.get(key, () -> StyledTextParser.parseStringWithStartingStyle(str, startingStyle));
        }
        catch (ExecutionException e)
        {
            MaLiLib.LOGGER.warn("Exception while retrieving StyledText from cache", e);
            return StyledTextParser.parseStringWithStartingStyle(str, startingStyle);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder builder(TextStyle startingStyle)
    {
        return new Builder(startingStyle);
    }

    public static class Builder
    {
        protected final List<StyledTextLine> lines = new ArrayList<>();
        protected final List<StyledTextSegment> segmentsForCurrentLine = new ArrayList<>();
        protected final TextStyle.Builder styleBuilder = TextStyle.builder();
        protected StringBuilder displayStringForCurrentSegment = new StringBuilder();
        protected StringBuilder originalTextStringForCurrentSegment = new StringBuilder();

        Builder()
        {
        }

        Builder(TextStyle startingStyle)
        {
            this.styleBuilder.fromStyle(startingStyle);
        }

        public void appendDisplayString(String str)
        {
            this.displayStringForCurrentSegment.append(str);
        }

        public void appendOriginalTextString(String str)
        {
            this.originalTextStringForCurrentSegment.append(str);
        }

        public void applyStyleChange(Consumer<TextStyle.Builder> styleModifier)
        {
            TextStyle styleBefore = this.styleBuilder.build();
            styleModifier.accept(this.styleBuilder);

            if (this.styleBuilder.equalsStyle(styleBefore) == false)
            {
                this.commitCurrentSegmentUsingStyle(styleBefore);
            }
        }

        public void addLineBeak()
        {
            this.commitCurrentLine();
            this.segmentsForCurrentLine.clear();
        }

        protected void commitCurrentSegment()
        {
            this.commitCurrentSegmentUsingStyle(this.styleBuilder.build());
        }

        protected void commitCurrentSegmentUsingStyle(TextStyle style)
        {
            if (this.displayStringForCurrentSegment.length() > 0)
            {
                String displayString = this.displayStringForCurrentSegment.toString();
                String originalString = this.originalTextStringForCurrentSegment.toString();

                TextRendererUtils.generatePerFontTextureSegmentsFor(displayString, originalString, style,
                                                                    this.segmentsForCurrentLine::add,
                                                                    TextRenderer.INSTANCE::getGlyphFor);

                this.displayStringForCurrentSegment = new StringBuilder();
                this.originalTextStringForCurrentSegment = new StringBuilder();
            }
        }

        protected void commitCurrentLine()
        {
            this.commitCurrentSegment();
            this.lines.add(new StyledTextLine(ImmutableList.copyOf(this.segmentsForCurrentLine)));
        }

        public StyledText build()
        {
            this.commitCurrentLine();
            return new StyledText(ImmutableList.copyOf(this.lines));
        }
    }

    private static class CacheKey
    {
        public final String text;
        @Nullable public final TextStyle startingStyle;

        public CacheKey(String text, @Nullable TextStyle startingStyle)
        {
            this.text = text;
            this.startingStyle = startingStyle;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || this.getClass() != o.getClass()) { return false; }

            CacheKey cacheKey = (CacheKey) o;

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
