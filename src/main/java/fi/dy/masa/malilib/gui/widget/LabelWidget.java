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
    protected final List<String> labels = new ArrayList<>();
    protected boolean centerTextHorizontally;
    protected boolean useTextShadow = true;
    protected boolean visible = true;
    protected int totalTextWidth;
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

        this.updateWidth();
        this.updateHeight();
    }

    public int getTotalWidth()
    {
        return this.totalWidth;
    }

    public LabelWidget addLine(String translationKey, Object... args)
    {
        String line = StringUtils.translate(translationKey, args);
        this.totalTextWidth = Math.max(this.totalTextWidth, this.getStringWidth(line) + 4);
        this.labels.add(line);
        this.updateWidth();
        this.updateHeight();
        return this;
    }

    public LabelWidget setText(String translationKey, Object... args)
    {
        this.labels.clear();
        return this.addLine(translationKey, args);
    }

    public LabelWidget setText(List<String> lines)
    {
        this.labels.clear();
        this.totalTextWidth = 0;

        for (String line : lines)
        {
            line = StringUtils.translate(line);
            this.totalTextWidth = Math.max(this.totalTextWidth, this.getStringWidth(line) + 4);
            this.labels.add(line);
        }

        return this;
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

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            this.totalWidth = this.totalTextWidth;

            if (this.backgroundEnabled)
            {
                this.totalWidth += this.borderWidth * 2;
            }

            this.totalWidth += this.paddingX * 2;

            int width = this.totalWidth;

            if (this.hasMaxWidth)
            {
                width = Math.min(width, this.maxWidth);
            }

            this.setWidth(width);
        }
    }

    @Override
    public void updateHeight()
    {
        if (this.automaticHeight)
        {
            int height = (this.fontHeight + 1) * this.labels.size() - 2;

            if (this.backgroundEnabled)
            {
                height += this.borderWidth * 2 + this.paddingY * 2;
            }
            else
            {
                height += this.paddingY * 2;
            }

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
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.visible)
        {
            super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

            x += this.paddingX + this.borderWidth;
            y += this.paddingY + this.borderWidth;
            int fontHeight = this.fontHeight;
            TextRenderer renderer = this.getTextRenderer(this.useTextShadow, this.centerTextHorizontally);

            for (String text : this.labels)
            {
                if (this.hasMaxWidth && hovered == false)
                {
                    text = StringUtils.clampTextToRenderLength(text, this.maxWidth - 2, LeftRight.RIGHT, " ...");
                }

                renderer.renderText(x, y, z, this.textColor, text);

                y += fontHeight + 1;
            }

            RenderUtils.color(1f, 1f, 1f, 1f);
        }
    }
}
