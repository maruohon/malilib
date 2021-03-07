package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderSettings;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class StringListRenderer extends BaseWidget
{
    protected final List<StyledTextLine> originalTextLines = new ArrayList<>();
    protected final List<StyledTextLine> processedLinesClamped = new ArrayList<>();
    protected final List<StyledTextLine> processedLinesFull = new ArrayList<>();
    protected final TextRenderSettings textSettingsNormal = new TextRenderSettings();
    protected final TextRenderSettings textSettingsHover = new TextRenderSettings();
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected LineClamper lineClamper;
    protected boolean hasClampedContent;
    protected int lineHeight;
    protected int clampedTextWidth;
    protected int totalTextWidth;
    protected int clampedHeight;
    protected int totalHeight;

    public StringListRenderer()
    {
        super(0, 0, 0, 0);

        this.lineHeight = TextRenderer.INSTANCE.getFontHeight() + 2;
        this.textSettingsNormal.setTextColor(0xFFC0C0C0).setUseFontShadow(true);
        this.textSettingsHover.setTextColor(0xFFE0E0E0).setUseFontShadow(true);
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

    public void setNormalTextSettings(TextRenderSettings settings)
    {
        this.textSettingsNormal.setFrom(settings);
    }

    public void setHoveredTextSettings(TextRenderSettings settings)
    {
        this.textSettingsHover.setFrom(settings);
    }

    public StringListRenderer setLineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;
        return this;
    }

    public StringListRenderer setNormalTextColor(int color)
    {
        this.textSettingsNormal.setTextColor(color);
        return this;
    }

    public StringListRenderer setHoverTextColor(int color)
    {
        this.textSettingsHover.setTextColor(color);
        return this;
    }

    public StringListRenderer setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public StringListRenderer setUseTextShadow(boolean useShadow)
    {
        this.textSettingsNormal.setUseFontShadow(useShadow);
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

    public String clampLineToWidth(String line, int maxWidth)
    {
        return StringUtils.clampTextToRenderLength(line, maxWidth, LeftRight.RIGHT, "...");
    }

    protected void clampAndAddTextLine(StyledTextLine line)
    {
        String fullLineText = line.displayText;
        StyledTextLine clampedLine = line;
        int lineWidth = line.renderWidth;
        int lineHeight = this.fontHeight + 1;
        this.totalHeight += this.processedLinesFull.size() > 0 ? lineHeight + 1 : lineHeight;
        this.totalTextWidth = Math.max(this.totalTextWidth, lineWidth);
        this.processedLinesFull.add(line);

        if (this.hasMaxWidth && lineWidth > this.maxWidth)
        {
            // TODO this needs style preserving clamping
            String clampedLineText = this.lineClamper.clampLineToWidth(fullLineText, this.maxWidth);
            boolean gotClamped = clampedLineText.equals(fullLineText) == false;

            if (gotClamped)
            {
                clampedLine = StyledTextLine.of(clampedLineText);
                lineWidth = clampedLine.renderWidth;
                this.hasClampedContent = true;
            }
        }

        this.clampedTextWidth = Math.max(this.clampedTextWidth, lineWidth);

        if (this.hasMaxHeight == false || this.totalHeight <= this.maxHeight)
        {
            this.processedLinesClamped.add(clampedLine);
            this.clampedHeight = this.totalHeight;
        }

        if (this.hasMaxHeight)
        {
            this.hasClampedContent |= this.totalHeight > this.maxHeight;
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
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        boolean shadow = this.textSettingsNormal.useFontShadow;
        boolean renderBackground = hovered ? this.textSettingsHover.useBackground : this.textSettingsNormal.useBackground;
        int maxTextWidth = hovered ? this.totalTextWidth : this.clampedTextWidth;
        int color = hovered ? this.textSettingsHover.textColor : this.textSettingsNormal.textColor;
        int usedHeight = TextRenderer.INSTANCE.getFontHeight();
        List<StyledTextLine> lines = hovered ? this.processedLinesFull : this.processedLinesClamped;

        if (renderBackground)
        {
            this.renderTextBackgrounds(x, y, z, lines, hovered);
        }

        TextRenderer.INSTANCE.startBuffers();

        for (StyledTextLine line : lines)
        {
            if (hovered == false && this.hasMaxHeight && usedHeight > this.maxHeight)
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

            TextRenderer.INSTANCE.renderLineToBuffer(lineX, y, z, color, shadow, line);
            y += this.lineHeight;
            usedHeight += this.lineHeight;
        }

        TextRenderer.INSTANCE.renderBuffers();
    }

    protected void renderTextBackgrounds(int x, int y, float z, List<StyledTextLine> lines, boolean hovered)
    {
        RenderUtils.setupBlend();
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        int maxTextWidth = hovered ? this.totalTextWidth : this.clampedTextWidth;
        int lineHeight = this.fontHeight + 2;
        int bgColor = hovered ? this.textSettingsHover.backgroundColor : this.textSettingsNormal.backgroundColor;
        int lineY = y - 2;

        for (StyledTextLine line : lines)
        {
            int lineX = x - 2;

            if (rightAlign)
            {
                lineX = x + maxTextWidth - line.renderWidth - 2;
            }
            else if (center)
            {
                lineX = x + maxTextWidth / 2 - line.renderWidth / 2 - 2;
            }

            RenderUtils.renderRectangleBatched(lineX, lineY, z, line.renderWidth + 3, lineHeight, bgColor, buffer);
            lineY += lineHeight;
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
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
