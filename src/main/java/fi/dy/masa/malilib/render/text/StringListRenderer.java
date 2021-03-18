package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
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

        this.textSettingsNormal.setTextColor(0xFFC0C0C0).setUseTextShadow(true);
        this.textSettingsHover.setTextColor(0xFFE0E0E0).setUseTextShadow(true);
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

    public int getTotalRenderWidth()
    {
        int bgOffset = this.textSettingsNormal.useBackground ? 3 : 0;
        return this.totalTextWidth + bgOffset;
    }

    public int getTotalTextHeight()
    {
        return this.totalTextHeight;
    }

    public int getTotalRenderHeight()
    {
        int bgOffset = this.textSettingsNormal.useBackground ? 1 : 0;
        return this.totalTextHeight + bgOffset;
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

    public StringListRenderer setVerticalAlignment(VerticalAlignment verticalAlignment)
    {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public StringListRenderer setUseTextShadow(boolean useShadow)
    {
        this.textSettingsNormal.setUseTextShadow(useShadow);
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

    /**
     * Render the text aligned to the given position.<br>
     * The alignment behavior depends on the set horizontal and vertical alignment values.<br>
     * With HorizontalAlignment.LEFT and VerticalAlignment.TOP, the x and y coordinates are
     * the top left corner of the string list.<br>
     * With HorizontalAlignment.RIGHT, the x coordinate is the right edge.<br>
     * With HorizontalAlignment.CENTER, the x coordinate is the middle of the string list. etc.
     */
    public void renderAt(int x, int y, float z, boolean hovered)
    {
        List<StyledTextLine> lines = hovered ? this.processedLinesFull : this.processedLinesClamped;
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        boolean shadow = hovered ? this.textSettingsHover.useTextShadow : this.textSettingsNormal.useTextShadow;
        boolean renderBackground = hovered ? this.textSettingsHover.useBackground : this.textSettingsNormal.useBackground;
        boolean checkHeight = hovered == false && this.hasMaxHeight;
        int color = hovered ? this.textSettingsHover.textColor : this.textSettingsNormal.textColor;
        int usedHeight = TextRenderer.INSTANCE.getFontHeight();
        int yOffset = this.verticalAlignment.getYStartOffset(this.getTotalRenderHeight());
        int textLineX = x + 2;
        int textLineY = y + yOffset + 1;

        if (renderBackground)
        {
            int bgColor = hovered ? this.textSettingsHover.backgroundColor : this.textSettingsNormal.backgroundColor;
            this.renderTextBackgrounds(x, y + yOffset, z, lines, checkHeight, bgColor);
        }

        TextRenderer.INSTANCE.startBuffers();

        for (StyledTextLine line : lines)
        {
            if (checkHeight && usedHeight > this.maxHeight)
            {
                break;
            }

            if (rightAlign)
            {
                textLineX = x - line.renderWidth;
            }
            else if (center)
            {
                textLineX = x - line.renderWidth / 2 + 1;
            }

            TextRenderer.INSTANCE.renderLineToBuffer(textLineX, textLineY, z, color, shadow, line);
            textLineY += this.lineHeight;
            usedHeight += this.lineHeight;
        }

        TextRenderer.INSTANCE.renderBuffers();
    }

    protected void renderTextBackgrounds(int x, int y, float z, List<StyledTextLine> lines, boolean checkHeight, int bgColor)
    {
        boolean rightAlign = this.horizontalAlignment == HorizontalAlignment.RIGHT;
        boolean center = this.horizontalAlignment == HorizontalAlignment.CENTER;
        int usedHeight = TextRenderer.INSTANCE.getFontHeight();
        int lineX = x;

        RenderUtils.setupBlend();
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        for (StyledTextLine line : lines)
        {
            if (checkHeight && usedHeight > this.maxHeight)
            {
                break;
            }

            if (rightAlign)
            {
                lineX = x - line.renderWidth - 2;
            }
            else if (center)
            {
                lineX = x - line.renderWidth / 2 - 1;
            }

            ShapeRenderUtils.renderRectangle(lineX, y, z, line.renderWidth + 3, this.lineHeight, bgColor, buffer);
            y += this.lineHeight;
            usedHeight += this.lineHeight;
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    public interface LineClamper
    {
        StyledTextLine clampLineToWidth(StyledTextLine line, int maxWidth);
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
