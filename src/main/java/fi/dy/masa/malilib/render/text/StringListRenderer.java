package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class StringListRenderer extends BaseWidget
{
    protected final List<StyledTextLine> originalTextLines = new ArrayList<>();
    protected final List<StyledTextLine> processedLinesClamped = new ArrayList<>();
    protected final MultiLineTextRenderSettings textSettingsHover = new MultiLineTextRenderSettings();
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected LineClamper lineClamper;
    protected boolean hasClampedContent;
    protected int totalTextWidth;
    protected int totalTextHeight;
    protected int clampedTextWidth;
    protected int clampedHeight;

    public StringListRenderer()
    {
        super(0, 0, 0, 0);

        this.textSettings.setTextColor(0xFFC0C0C0);
        this.textSettings.setTextShadowEnabled(true);
        this.textSettingsHover.setTextColor(0xFFE0E0E0);
        this.textSettingsHover.setTextShadowEnabled(true);
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
        int bgWidth = this.textSettings.getBackgroundEnabled() ? this.padding.getHorizontalTotal() : 0;
        return this.clampedTextWidth + bgWidth;
    }

    public int getTotalRenderWidth()
    {
        int bgWidth = this.textSettings.getBackgroundEnabled() ? this.padding.getHorizontalTotal() : 0;
        return this.totalTextWidth + bgWidth;
    }

    public int getTotalTextHeight()
    {
        return this.totalTextHeight;
    }

    public int getTotalRenderHeight()
    {
        int bgHeight = this.textSettings.getBackgroundEnabled() ? this.padding.getVerticalTotal() : 0;
        return this.totalTextHeight + bgHeight;
    }

    public int getClampedHeight()
    {
        return this.clampedHeight;
    }

    public int getTotalLineCount()
    {
        return this.originalTextLines.size();
    }

    public boolean isEmpty()
    {
        return this.originalTextLines.isEmpty();
    }

    public HorizontalAlignment getHorizontalAlignment()
    {
        return this.horizontalAlignment;
    }

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);
        this.reAddLines();
    }

    public void setNormalTextSettingsFrom(TextRenderSettings settings)
    {
        this.textSettings.setFrom(settings);
    }

    public void setHoveredTextSettingsFrom(TextRenderSettings settings)
    {
        this.textSettingsHover.setFrom(settings);
    }

    public MultiLineTextRenderSettings getNormalTextSettings()
    {
        return this.textSettings;
    }

    public MultiLineTextRenderSettings getHoverTextSettings()
    {
        return this.textSettingsHover;
    }

    public StringListRenderer setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public StringListRenderer setLineClamper(LineClamper clamper)
    {
        this.lineClamper = clamper;
        return this;
    }

    /**
     * Clears the processed/split/clamped strings and the computed total width and height
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
        this.hasClampedContent = false;
        this.totalTextWidth = 0;
        this.clampedTextWidth = 0;
        this.clampedHeight = 0;
        this.totalTextHeight = 0;
    }

    public void setText(String translationKey, Object... args)
    {
        this.setStyledText(StyledText.translate(translationKey, args));
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
        return StyledTextUtils.clampStyledTextToMaxWidth(line, maxWidth, LeftRight.RIGHT, " ...");
    }

    protected void clampAndAddTextLine(StyledTextLine line)
    {
        StyledTextLine clampedLine = line;
        int lineWidth = line.renderWidth;
        int lineHeight = this.getLineHeight();
        this.totalTextHeight += this.processedLinesClamped.size() > 0 ? lineHeight : TextRenderer.INSTANCE.getLineHeight();
        this.totalTextWidth = Math.max(this.totalTextWidth, lineWidth);

        if (this.hasMaxWidth() && lineWidth > this.maxWidth)
        {
            clampedLine = this.lineClamper.clampLineToWidth(line, this.maxWidth);
            lineWidth = clampedLine.renderWidth;
            this.hasClampedContent = true;
        }

        this.clampedTextWidth = Math.max(this.clampedTextWidth, lineWidth);

        if (this.hasMaxHeight() == false || this.totalTextHeight <= this.maxHeight)
        {
            this.processedLinesClamped.add(clampedLine);
            this.clampedHeight = this.totalTextHeight;
        }

        if (this.hasMaxHeight())
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

    public void renderAt(int x, int y, float z, boolean hovered, ScreenContext ctx)
    {
        MultiLineTextRenderSettings settings = hovered ? this.textSettingsHover : this.textSettings;
        List<StyledTextLine> lines = hovered ? this.originalTextLines : this.processedLinesClamped;
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        boolean shadow = settings.getTextShadowEnabled();
        boolean renderBackground = settings.getBackgroundEnabled();
        boolean oddEvenBackground = settings.getOddEvenBackgroundEnabled();
        boolean checkHeight = hovered == false && this.hasMaxHeight();
        int color = settings.getTextColor();
        int bgColorNormal = settings.getBackgroundColor();
        int bgColorOdd = oddEvenBackground ? settings.getOddRowBackgroundColor() : bgColorNormal;
        int usedHeight = TextRenderer.INSTANCE.getLineHeight();
        int width = hovered ? this.getTotalRenderWidth() : this.getClampedRenderWidth();
        int leftPadding = this.padding.getLeft();
        int rightPadding = this.padding.getRight();
        int topPadding = this.padding.getTop();
        int bottomPadding = this.padding.getBottom();
        int horizontalPadding = leftPadding + rightPadding;
        int textLineX = x + leftPadding;
        int textLineY = y + topPadding;
        int backgroundX = x;
        int backgroundY = y;
        int fontHeight = this.getFontHeight();
        int lineHeight = this.getLineHeight();
        int size = lines.size();
        BufferBuilder buffer = null;

        if (renderBackground)
        {
            buffer = RenderUtils.startBuffer(GL11.GL_QUADS, VertexFormats.POSITION_COLOR, false);
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

            if (renderBackground)
            {
                int backgroundWidth = line.renderWidth + horizontalPadding;

                if (settings.evenWidthBackgroundEnabled)
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

                int bgColor = (i & 0x1) != 0 ? bgColorOdd : bgColorNormal;
                int bgHeight = fontHeight + topPadding + bottomPadding;
                ShapeRenderUtils.renderRectangle(backgroundX, backgroundY, z, backgroundWidth, bgHeight, bgColor, buffer);
                backgroundY += lineHeight;
            }

            TextRenderer.INSTANCE.renderLineToBuffer(textLineX, textLineY, z + 0.0125f, color, shadow, line);
            textLineY += lineHeight;
            usedHeight += lineHeight;
        }

        if (renderBackground)
        {
            RenderUtils.drawBuffer();
        }

        TextRenderer.INSTANCE.renderBuffers();
    }

    public interface LineClamper
    {
        StyledTextLine clampLineToWidth(StyledTextLine line, int maxWidth);
    }
}
