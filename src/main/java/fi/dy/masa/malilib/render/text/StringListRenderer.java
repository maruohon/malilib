package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.position.VerticalAlignment;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class StringListRenderer extends BaseWidget
{
    protected final List<StyledTextLine> originalTextLines = new ArrayList<>();
    protected final List<StyledTextLine> processedLinesClamped = new ArrayList<>();
    protected final List<StyledTextLine> processedLinesFull = new ArrayList<>();
    protected final TextRenderSettings textSettingsNormal = new TextRenderSettings();
    protected final TextRenderSettings textSettingsHover = new TextRenderSettings();
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
    protected LineClamper lineClamper;
    protected boolean hasClampedContent;
    protected int totalTextWidth;
    protected int totalTextHeight;
    protected int clampedTextWidth;
    protected int clampedHeight;

    public StringListRenderer()
    {
        super(0, 0, 0, 0);

        this.textSettingsNormal.setTextColor(0xFFC0C0C0);
        this.textSettingsNormal.setUseTextShadow(true);
        this.textSettingsHover.setTextColor(0xFFE0E0E0);
        this.textSettingsHover.setUseTextShadow(true);
        this.padding.setAll(1, 2, 0, 2);
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

    public int getClampedRenderWidth()
    {
        int bgWidth = this.textSettingsNormal.useBackground ? this.padding.getHorizontalTotal() : 0;
        return this.clampedTextWidth + bgWidth;
    }

    public int getTotalRenderWidth()
    {
        int bgWidth = this.textSettingsNormal.useBackground ? this.padding.getHorizontalTotal() : 0;
        return this.totalTextWidth + bgWidth;
    }

    public int getTotalTextHeight()
    {
        return this.totalTextHeight;
    }

    public int getTotalRenderHeight()
    {
        int bgHeight = this.textSettingsNormal.useBackground ? this.padding.getVerticalTotal() : 0;
        return this.totalTextHeight + bgHeight;
    }

    public int getClampedHeight()
    {
        return this.clampedHeight;
    }

    public int getTotalLineCount()
    {
        return this.processedLinesFull.size();
    }

    public boolean isEmpty()
    {
        return this.processedLinesFull.isEmpty();
    }

    public void setNormalTextSettingsFrom(TextRenderSettings settings)
    {
        this.textSettingsNormal.setFrom(settings);
    }

    public void setHoveredTextSettingsFrom(TextRenderSettings settings)
    {
        this.textSettingsHover.setFrom(settings);
    }

    public TextRenderSettings getNormalTextSettings()
    {
        return this.textSettingsNormal;
    }

    public TextRenderSettings getHoverTextSettings()
    {
        return this.textSettingsHover;
    }

    public StringListRenderer setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public StringListRenderer setVerticalAlignment(VerticalAlignment verticalAlignment)
    {
        this.verticalAlignment = verticalAlignment;
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
        this.originalTextLines.clear();
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
        this.totalTextHeight = 0;
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

    public void addLine(String translationKey, Object... args)
    {
        String translated = StringUtils.translate(translationKey, args);
        this.parseAndAddLine(translated);
    }

    protected void parseAndAddLine(String translated)
    {
        this.addStyledText(StyledText.of(translated));
    }

    public void setStyledText(StyledText text)
    {
        this.clearText();
        this.setStyledTextLines(text.lines);
    }

    public void addStyledText(StyledText text)
    {
        for (StyledTextLine line : text.lines)
        {
            this.addStyledTextLine(line);
        }
    }

    public void setStyledTextLines(List<StyledTextLine> lines)
    {
        this.clearText();

        for (StyledTextLine line : lines)
        {
            this.addStyledTextLine(line);
        }
    }

    public void addStyledTextLine(StyledTextLine line)
    {
        this.originalTextLines.add(line);
        this.clampAndAddTextLine(line);
    }

    public StyledTextLine clampLineToWidth(StyledTextLine line, int maxWidth)
    {
        return StyledTextUtils.clampStyledTextToMaxWidth(line, this.maxWidth, LeftRight.RIGHT, " ...");
    }

    protected void clampAndAddTextLine(StyledTextLine line)
    {
        StyledTextLine clampedLine = line;
        int lineWidth = line.renderWidth;
        this.totalTextHeight += this.processedLinesFull.size() > 0 ? this.lineHeight : TextRenderer.INSTANCE.getFontHeight();
        this.totalTextWidth = Math.max(this.totalTextWidth, lineWidth);
        this.processedLinesFull.add(line);

        if (this.hasMaxWidth && lineWidth > this.maxWidth)
        {
            clampedLine = this.lineClamper.clampLineToWidth(line, this.maxWidth);
            lineWidth = clampedLine.renderWidth;
            this.hasClampedContent = true;
        }

        this.clampedTextWidth = Math.max(this.clampedTextWidth, lineWidth);

        if (this.hasMaxHeight == false || this.totalTextHeight <= this.maxHeight)
        {
            this.processedLinesClamped.add(clampedLine);
            this.clampedHeight = this.totalTextHeight;
        }

        if (this.hasMaxHeight)
        {
            this.hasClampedContent |= this.totalTextHeight > this.maxHeight;
        }
    }

    public void reAddLines()
    {
        this.clearProcessedText();

        for (StyledTextLine line : this.originalTextLines)
        {
            this.clampAndAddTextLine(line);
        }
    }

    public void renderAt(int x, int y, float z, boolean hovered)
    {
        TextRenderSettings settings = hovered ? this.textSettingsHover : this.textSettingsNormal;
        List<StyledTextLine> lines = hovered ? this.processedLinesFull : this.processedLinesClamped;
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        boolean shadow = settings.useTextShadow;
        boolean renderBackground = settings.useBackground;
        boolean oddEvenBackground = settings.useOddEvenBackground;
        boolean checkHeight = hovered == false && this.hasMaxHeight;
        int color = settings.textColor;
        int bgColorNormal = settings.backgroundColor;
        int bgColorOdd = oddEvenBackground ? settings.backgroundColorOdd : bgColorNormal;
        int usedHeight = TextRenderer.INSTANCE.getFontHeight();
        int width = hovered ? this.getTotalRenderWidth() : this.getClampedRenderWidth();
        int leftPadding = this.padding.getLeft();
        int rightPadding = this.padding.getRight();
        int horizontalPadding = leftPadding + rightPadding;
        int textLineX = x + leftPadding;
        int textLineY = y + this.padding.getTop() + 1;
        int backgroundX = x;
        int backgroundY = y;
        int lineHeight = this.lineHeight;
        int size = lines.size();
        BufferBuilder buffer = null;

        if (renderBackground)
        {
            buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);
        }

        TextRenderer.INSTANCE.startBuffers();

        for (int i = 0; i < size; ++i)
        {
            StyledTextLine line = lines.get(i);

            if (checkHeight && usedHeight > this.maxHeight)
            {
                break;
            }

            if (rightAlign)
            {
                textLineX = x + width - line.renderWidth - rightPadding;
            }
            else if (center)
            {
                textLineX = x + width / 2 - line.renderWidth / 2;
            }

            int bgHeight = lineHeight;

            if (renderBackground)
            {
                int backgroundWidth = line.renderWidth + horizontalPadding;

                if (settings.useEvenWidthBackground)
                {
                    backgroundWidth = width;
                }
                else if (rightAlign)
                {
                    backgroundX = x + width - backgroundWidth;
                }
                else if (center)
                {
                    backgroundX = x + width / 2 - backgroundWidth / 2;
                }

                if (i == 0)
                {
                    bgHeight += this.padding.getTop();
                }

                if (i == size - 1)
                {
                    bgHeight += this.padding.getBottom();
                }

                int bgColor = (i & 0x1) != 0 ? bgColorOdd : bgColorNormal;
                ShapeRenderUtils.renderRectangle(backgroundX, backgroundY, z, backgroundWidth, bgHeight, bgColor, buffer);
                backgroundY += bgHeight;
            }

            TextRenderer.INSTANCE.renderLineToBuffer(textLineX, textLineY, z + 0.0125f, color, shadow, line);
            textLineY += lineHeight;
            usedHeight += bgHeight;
        }

        if (renderBackground)
        {
            RenderUtils.drawBuffer();
        }

        TextRenderer.INSTANCE.renderBuffers();
    }

    /*
    protected void renderTextBackgrounds(int x, int y, float z, List<StyledTextLine> lines, boolean checkHeight, int bgColor)
    {
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        int usedHeight = TextRenderer.INSTANCE.getFontHeight();
        int lineX = x;

        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        for (StyledTextLine line : lines)
        {
            if (checkHeight && usedHeight > this.maxHeight)
            {
                break;
            }

            int lineWidth = line.renderWidth;

            if (rightAlign)
            {
                lineX = x - lineWidth - 2;
            }
            else if (center)
            {
                lineX = x - lineWidth / 2 - 1;
            }

            ShapeRenderUtils.renderRectangle(lineX, y, z, lineWidth + 3, this.lineHeight, bgColor, buffer);
            y += this.lineHeight;
            usedHeight += this.lineHeight;
        }

        RenderUtils.drawBuffer();
    }
    */

    public interface LineClamper
    {
        StyledTextLine clampLineToWidth(StyledTextLine line, int maxWidth);
    }
}
