package malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;

public class StyledTextBuilder
{
    protected final List<StyledTextLine> lines = new ArrayList<>();
    protected final List<StyledTextSegment> segmentsForCurrentLine = new ArrayList<>();
    protected final TextStyle.Builder styleBuilder = TextStyle.builder();
    protected StringBuilder displayStringForCurrentSegment = new StringBuilder();
    protected StringBuilder originalTextStringForCurrentSegment = new StringBuilder();

    public StyledTextBuilder()
    {
    }

    public StyledTextBuilder(TextStyle startingStyle)
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
            this.commitCurrentSegmentUsingStyle(styleBefore, false);
        }
    }

    public void addLineBeak()
    {
        this.commitCurrentLine(false);
        this.segmentsForCurrentLine.clear();
    }

    protected void commitCurrentSegment(boolean force)
    {
        this.commitCurrentSegmentUsingStyle(this.styleBuilder.build(), force);
    }

    protected void commitCurrentSegmentUsingStyle(TextStyle style, boolean force)
    {
        if (force || this.displayStringForCurrentSegment.length() > 0)
        {
            String displayString = this.displayStringForCurrentSegment.toString();
            String originalString = this.originalTextStringForCurrentSegment.toString();

            StyledTextUtils.generatePerFontTextureSegmentsFor(displayString, originalString, style,
                                                              this.segmentsForCurrentLine::add,
                                                              TextRenderer.INSTANCE::getGlyphFor);

            this.displayStringForCurrentSegment = new StringBuilder();
            this.originalTextStringForCurrentSegment = new StringBuilder();
        }
    }

    protected void commitCurrentLine(boolean force)
    {
        this.commitCurrentSegment(force);
        this.lines.add(new StyledTextLine(ImmutableList.copyOf(this.segmentsForCurrentLine)));
    }

    public ImmutableList<StyledTextLine> build()
    {
        this.commitCurrentLine(true); // force commit empty strings with only style
        return ImmutableList.copyOf(this.lines);
    }
}
