package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.render.TextRenderer;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class StringListRenderer extends BaseWidget
{
    protected final List<String> originalStrings = new ArrayList<>();
    protected final List<Line> processedLinesClamped = new ArrayList<>();
    protected final List<Line> processedLinesFull = new ArrayList<>();
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected LineClamper lineClamper;
    protected boolean hasClampedContent;
    protected boolean useTextShadow = true;
    protected int textColorNormal = 0xFFC0C0C0;
    protected int textColorHover = 0xFFE0E0E0;
    protected int clampedTextWidth;
    protected int totalTextWidth;
    protected int clampedHeight;
    protected int totalHeight;

    public StringListRenderer()
    {
        super(0, 0, 0, 0);

        this.lineClamper = this::clampLineToWidth;
    }

    public boolean hasClampedContent()
    {
        return this.hasClampedContent;
    }

    public int getTotalTextWidth()
    {
        return this.totalTextWidth;
    }

    public int getTotalHeight()
    {
        return this.totalHeight;
    }

    public int getClampedHeight()
    {
        return this.clampedHeight;
    }
    
    public StringListRenderer setNormalTextColor(int color)
    {
        this.textColorNormal = color;
        return this;
    }

    public StringListRenderer setHoverTextColor(int color)
    {
        this.textColorHover = color;
        return this;
    }

    public StringListRenderer setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public StringListRenderer setUseTextShadow(boolean useShadow)
    {
        this.useTextShadow = useShadow;
        return this;
    }

    public StringListRenderer setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.hasMaxWidth = maxWidth > 0;
        return this;
    }

    public StringListRenderer setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight;
        this.hasMaxHeight = maxHeight > 0;
        return this;
    }

    public StringListRenderer setLineClamper(LineClamper clamper)
    {
        this.lineClamper = clamper;
        return this;
    }

    /**
     * Clears the process/split/clamped strings and the computed total width and height
     * AND also the original strings.
     */
    public void clearText()
    {
        this.originalStrings.clear();
        this.clearProcessedText();
    }

    /**
     * Clears the process/split/clamped strings and the computed total width and height.
     */
    public void clearProcessedText()
    {
        this.processedLinesClamped.clear();
        this.processedLinesFull.clear();
        this.hasClampedContent = false;
        this.totalTextWidth = 0;
        this.clampedTextWidth = 0;
        this.clampedHeight = 0;
        this.totalHeight = 0;
    }

    public void setText(String translationKey, Object... args)
    {
        this.clearText();
        this.addLine(translationKey, args);
    }

    public void setText(List<String> lines)
    {
        this.clearText();

        for (String line : lines)
        {
            this.addLine(line);
        }
    }

    public String clampLineToWidth(String line, int maxWidth)
    {
        return StringUtils.clampTextToRenderLength(line, maxWidth, LeftRight.RIGHT, "...");
    }

    public void addLine(String translationKey, Object... args)
    {
        String translated = StringUtils.translate(translationKey, args);
        this.originalStrings.add(translated);
        this.splitAndAddLine(translated);
    }

    protected void splitAndAddLine(String translated)
    {
        String[] splitLines = translated.split("\\n");

        for (String line : splitLines)
        {
            String clampedLine = line;
            int lineWidth = this.textRenderer.getStringWidth(line);
            this.totalHeight += this.processedLinesFull.size() > 0 ? this.fontHeight + 1 : this.fontHeight;
            this.totalTextWidth = Math.max(this.totalTextWidth, lineWidth);
            this.processedLinesFull.add(new Line(line, lineWidth));

            if (this.hasMaxWidth)
            {
                clampedLine = this.lineClamper.clampLineToWidth(line, this.maxWidth);
                boolean gotClamped = clampedLine.equals(line) == false;

                if (gotClamped)
                {
                    lineWidth = this.textRenderer.getStringWidth(clampedLine);
                    this.hasClampedContent = true;
                }
            }

            if (this.hasMaxHeight == false || this.totalHeight <= this.maxHeight)
            {
                this.processedLinesClamped.add(new Line(clampedLine, lineWidth));
                this.clampedHeight = this.totalHeight;
            }
        }

        if (this.hasMaxHeight)
        {
            this.hasClampedContent |= this.totalHeight > this.maxHeight;
        }
    }

    public void reAddLines()
    {
        this.clearProcessedText();

        for (String translated : this.originalStrings)
        {
            this.splitAndAddLine(translated);
        }
    }

    public void renderAt(int x, int y, float z, boolean hovered)
    {
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        boolean first = true;
        TextRenderer renderer = this.getTextRenderer(this.useTextShadow, center);
        List<Line> lines = hovered ? this.processedLinesFull : this.processedLinesClamped;
        int maxTextWidth = hovered ? this.totalTextWidth : this.clampedTextWidth;
        int color = hovered ? this.textColorHover : this.textColorNormal;
        int fontHeight = this.fontHeight;
        int usedHeight = 0;

        for (Line line : lines)
        {
            if (hovered == false && this.hasMaxHeight &&
                usedHeight + (first ? fontHeight : fontHeight + 1) > this.maxHeight)
            {
                break;
            }

            int lineX = x;

            if (rightAlign)
            {
                lineX = x + maxTextWidth - line.renderWidth;
            }
            else if (center)
            {
                lineX = x + maxTextWidth / 2;
            }

            renderer.renderText(lineX, y, z, color, line.text);
            y += fontHeight + 1;
            usedHeight += fontHeight + 1;
            first = false;
        }
    }

    public interface LineClamper
    {
        String clampLineToWidth(String line, int maxWidth);
    }

    public static class Line
    {
        public final String text;
        public final int renderWidth;

        public Line(String text, int renderWidth)
        {
            this.text = text;
            this.renderWidth = renderWidth;
        }
    }
}
