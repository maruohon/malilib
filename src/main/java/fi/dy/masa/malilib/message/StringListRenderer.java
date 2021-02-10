package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.render.TextRenderer;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class StringListRenderer extends BaseWidget
{
    protected final List<String> originalStrings = new ArrayList<>();
    protected final List<String> processedLinesClamped = new ArrayList<>();
    protected final List<String> processedLinesFull = new ArrayList<>();
    protected LineClamper lineClamper;
    protected boolean centerTextHorizontally;
    protected boolean hasClampedContent;
    protected boolean useTextShadow = true;
    protected int textColorNormal = 0xFFC0C0C0;
    protected int textColorHover = 0xFFE0E0E0;
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

    public StringListRenderer setCenterTextHorizontally(boolean centerTextHorizontally)
    {
        this.centerTextHorizontally = centerTextHorizontally;
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

    public void clearText()
    {
        this.originalStrings.clear();
        this.processedLinesClamped.clear();
        this.processedLinesFull.clear();
        this.hasClampedContent = false;
        this.totalTextWidth = 0;
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
            this.totalHeight += this.processedLinesFull.size() > 0 ? this.fontHeight + 1 : this.fontHeight;
            this.totalTextWidth = Math.max(this.totalTextWidth, this.textRenderer.getStringWidth(line));
            this.processedLinesFull.add(line);

            if (this.hasMaxWidth)
            {
                clampedLine = this.lineClamper.clampLineToWidth(line, this.maxWidth);
                this.hasClampedContent |= clampedLine.equals(line) == false;
            }

            if (this.hasMaxHeight == false || this.totalHeight <= this.maxHeight)
            {
                this.processedLinesClamped.add(clampedLine);
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
        List<String> lines = new ArrayList<>(this.originalStrings);

        this.clearText();
        this.originalStrings.addAll(lines); 

        for (String translated : lines)
        {
            this.splitAndAddLine(translated);
        }
    }

    public void renderAt(int x, int y, float z, boolean hovered)
    {
        int fontHeight = this.fontHeight;
        int usedHeight = 0;
        boolean first = true;
        TextRenderer renderer = this.getTextRenderer(this.useTextShadow, this.centerTextHorizontally);
        List<String> lines = hovered ? this.processedLinesFull : this.processedLinesClamped;

        for (String text : lines)
        {
            if (hovered == false && this.hasMaxHeight &&
                usedHeight + (first ? fontHeight : fontHeight + 1) > this.maxHeight)
            {
                break;
            }

            renderer.renderText(x, y, z, hovered ? this.textColorHover : this.textColorNormal, text);
            y += fontHeight + 1;
            usedHeight += fontHeight + 1;
            first = false;
        }
    }

    public interface LineClamper
    {
        String clampLineToWidth(String line, int maxWidth);
    }
}
