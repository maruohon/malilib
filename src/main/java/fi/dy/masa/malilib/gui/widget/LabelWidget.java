package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.render.TextRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class LabelWidget extends BackgroundWidget
{
    protected final List<String> labels = new ArrayList<>();
    protected boolean centerTextHorizontally;
    protected boolean useTextShadow = true;
    protected boolean visible = true;
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

        for (String line : lines)
        {
            this.labels.add(StringUtils.translate(line));
        }

        this.updateWidth();
        this.updateHeight();
    }

    public LabelWidget addLine(String translationKey, Object... args)
    {
        this.labels.add(StringUtils.translate(translationKey, args));
        this.updateWidth();
        this.updateHeight();
        return this;
    }

    public LabelWidget setText(String translationKey, Object... args)
    {
        this.labels.clear();
        return this.addLine(translationKey, args);
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
            int width = 0;

            for (String line : this.labels)
            {
                width = Math.max(width, this.getStringWidth(line));
            }

            if (this.backgroundEnabled)
            {
                width += this.borderWidth * 2 + this.paddingX * 2;
            }
            else
            {
                width += this.paddingX * 2;
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
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.visible)
        {
            super.render(mouseX, mouseY, isActiveGui, hovered);

            int x = this.getX() + this.paddingX + this.borderWidth;
            int y = this.getY() + this.paddingY + this.borderWidth;
            int fontHeight = this.fontHeight;
            TextRenderer renderer = this.getTextRenderer(this.useTextShadow, this.centerTextHorizontally);

            for (String text : this.labels)
            {
                renderer.renderText(x, y, this.textColor, text);
                y += fontHeight + 1;
            }

            RenderUtils.color(1f, 1f, 1f, 1f);
        }
    }
}
