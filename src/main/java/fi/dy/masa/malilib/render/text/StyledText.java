package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;

public class StyledText
{
    public final ImmutableList<StyledTextLine> lines;

    public StyledText(ImmutableList<StyledTextLine> lines)
    {
        this.lines = lines;
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

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        protected final List<StyledTextLine> lines = new ArrayList<>();
        protected final List<StyledTextSegment> segmentsForCurrentLine = new ArrayList<>();
        protected final TextStyle.Builder styleBuilder = TextStyle.builder();
        protected StringBuilder stringForCurrentSegment = new StringBuilder();

        public void appendString(String str)
        {
            this.stringForCurrentSegment.append(str);
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
            this.commitCurrentSegment();
            this.lines.add(new StyledTextLine(ImmutableList.copyOf(this.segmentsForCurrentLine)));
            this.segmentsForCurrentLine.clear();
        }

        protected void commitCurrentSegment()
        {
            this.commitCurrentSegmentUsingStyle(this.styleBuilder.build());
        }

        protected void commitCurrentSegmentUsingStyle(TextStyle style)
        {
            if (this.stringForCurrentSegment.length() > 0)
            {
                String str = this.stringForCurrentSegment.toString();
                this.segmentsForCurrentLine.add(new StyledTextSegment(str, style));
                this.stringForCurrentSegment = new StringBuilder();
            }
        }

        public StyledText build()
        {
            this.commitCurrentSegment();
            this.lines.add(new StyledTextLine(ImmutableList.copyOf(this.segmentsForCurrentLine)));
            return new StyledText(ImmutableList.copyOf(this.lines));
        }
    }

    public static StyledText of(String str)
    {
        return StyledTextParser.parseString(str);
    }
}