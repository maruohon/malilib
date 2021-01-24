package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.TextRenderer;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class LabelWidget extends BackgroundWidget
{
    protected final List<String> originalStrings = new ArrayList<>();
    protected final List<String> processedLinesNoHover = new ArrayList<>();
    protected final List<String> processedLinesHover = new ArrayList<>();
    protected boolean centerTextHorizontally;
    protected boolean hasOverflowingContent;
    protected boolean useBackgroundForHoverOverflow = true;
    protected boolean useTextShadow = true;
    protected boolean visible = true;
    protected int totalTextWidth;
    protected int totalHeight;
    protected int totalWidth;
    protected int textColor;

    public LabelWidget(int x, int y, int textColor, String... text)
    {
        this(x, y, -1, -1, textColor, Arrays.asList(text));
    }

    public LabelWidget(int x, int y, int textColor, List<String> lines)
    {
        this(x, y, -1, -1, textColor, lines);
    }

    public LabelWidget(int x, int y, int width, int height, int textColor, String... text)
    {
        this(x, y, width, height, textColor, Arrays.asList(text));
    }

    public LabelWidget(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        super(x, y, width, height);

        this.textColor = textColor;
        this.setText(lines);
    }

    public int getTotalWidth()
    {
        return this.totalWidth;
    }

    public int getTotalHeight()
    {
        return this.totalHeight;
    }

    protected void clearText()
    {
        this.originalStrings.clear();
        this.processedLinesNoHover.clear();
        this.processedLinesHover.clear();
        this.hasOverflowingContent = false;
        this.totalTextWidth = 0;
    }

    public LabelWidget setText(String translationKey, Object... args)
    {
        this.clearText();
        return this.addLine(translationKey, args);
    }

    public LabelWidget setText(List<String> lines)
    {
        this.clearText();

        for (String line : lines)
        {
            this.addLineNoUpdate(line);
        }

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    public LabelWidget addLine(String translationKey, Object... args)
    {
        this.addLineNoUpdate(translationKey, args);
        this.updateWidth();
        this.updateHeight();
        return this;
    }

    protected void addLineNoUpdate(String translationKey, Object... args)
    {
        String translated = StringUtils.translate(translationKey, args);
        this.originalStrings.add(translated);

        String[] splitLines = translated.split("\\n");

        for (String line : splitLines)
        {
            this.totalTextWidth = Math.max(this.totalTextWidth, this.getStringWidth(line));
            this.processedLinesHover.add(line);
            String clampedLine = line;

            if (this.automaticWidth == false || this.hasMaxWidth)
            {
                clampedLine = StringUtils.clampTextToRenderLength(line, this.maxWidth - 2, LeftRight.RIGHT, "...");
                this.hasOverflowingContent |= clampedLine.equals(line) == false;
            }

            this.processedLinesNoHover.add(clampedLine);
        }

        int bw = this.getActiveBorderWidth();
        int w = (this.fontHeight + 1) * this.processedLinesNoHover.size() + this.paddingTop + this.paddingBottom + bw;

        if (this.maxHeight > 0 && w > this.maxHeight)
        {
            this.hasOverflowingContent = true;
        }
    }

    public LabelWidget setVisible(boolean visible)
    {
        this.visible = visible;
        return this;
    }

    public LabelWidget setCenterTextHorizontally(boolean centerTextHorizontally)
    {
        this.centerTextHorizontally = centerTextHorizontally;
        return this;
    }

    public LabelWidget setUseTextShadow(boolean useShadow)
    {
        this.useTextShadow = useShadow;
        return this;
    }

    public LabelWidget setTextColor(int color)
    {
        this.textColor = color;
        return this;
    }

    public LabelWidget setUseBackgroundForHoverOverflow(boolean useBackground)
    {
        this.useBackgroundForHoverOverflow = useBackground;
        return this;
    }

    @Override
    public void updateWidth()
    {
        this.totalWidth = this.totalTextWidth + this.paddingLeft + this.paddingRight;
        this.totalHeight += this.getActiveBorderWidth() * 2;

        int width = this.totalWidth;

        if (this.hasMaxWidth)
        {
            width = Math.min(width, this.maxWidth);
        }

        if (this.automaticWidth)
        {
            this.setWidth(width);
        }
    }

    @Override
    public void updateHeight()
    {
        this.totalHeight = (this.fontHeight + 1) * this.processedLinesNoHover.size() + this.paddingTop + this.paddingBottom;
        this.totalHeight += this.getActiveBorderWidth() * 2;

        int height = this.totalHeight;

        if (this.hasMaxHeight)
        {
            height = Math.min(height, this.maxHeight);
        }

        if (this.automaticHeight)
        {
            this.setHeight(height);
        }
    }

    @Override
    protected int getBackgroundWidth(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.hasMaxWidth && isActiveGui && hovered)
        {
            return this.totalWidth;
        }

        return super.getBackgroundWidth(mouseX, mouseY, isActiveGui, hovered);
    }

    @Override
    protected int getBackgroundHeight(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.hasMaxHeight && isActiveGui && hovered)
        {
            return this.totalHeight;
        }

        return super.getBackgroundHeight(mouseX, mouseY, isActiveGui, hovered);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.visible)
        {
            if (hovered && this.backgroundEnabled == false && this.useBackgroundForHoverOverflow && this.hasOverflowingContent)
            {
                z += 20;
                int width = this.totalWidth;
                int height = this.totalHeight;
                this.renderBackground(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered, false);
                this.renderBorder(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered, false);
            }
            else
            {
                super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);
            }

            int bw = this.getActiveBorderWidth();
            int fontHeight = this.fontHeight;
            int usedHeight = this.paddingTop + this.paddingBottom + bw * 2;
            x += this.paddingLeft + bw;
            y += this.paddingTop + bw;
            TextRenderer renderer = this.getTextRenderer(this.useTextShadow, this.centerTextHorizontally);
            List<String> lines = hovered ? this.processedLinesHover : this.processedLinesNoHover;

            for (String text : lines)
            {
                usedHeight += fontHeight + 1;

                if (hovered == false && usedHeight > this.maxHeight && (this.automaticHeight == false || this.hasMaxHeight))
                {
                    break;
                }

                renderer.renderText(x, y, z, this.textColor, text);
                y += fontHeight + 1;
            }

            RenderUtils.color(1f, 1f, 1f, 1f);
        }
    }
}
