package fi.dy.masa.malilib.gui.widget;

import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.message.StringListRenderer;
import fi.dy.masa.malilib.render.RenderUtils;

public class LabelWidget extends BackgroundWidget
{
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected boolean useBackgroundForHoverOverflow = true;
    protected boolean visible = true;
    protected int totalHeight;
    protected int totalWidth;

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

        this.stringListRenderer.setNormalTextColor(textColor);
        this.stringListRenderer.setHoverTextColor(textColor);
        this.setText(lines);
        this.updateStringRendererSize();
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
        this.stringListRenderer.clearText();
    }

    public LabelWidget setText(String translationKey, Object... args)
    {
        this.stringListRenderer.setText(translationKey, args);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setText(List<String> lines)
    {
        this.stringListRenderer.setText(lines);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget addLine(String translationKey, Object... args)
    {
        this.stringListRenderer.addLine(translationKey, args);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setVisible(boolean visible)
    {
        this.visible = visible;
        return this;
    }

    public LabelWidget setCenterTextHorizontally(boolean centerTextHorizontally)
    {
        this.stringListRenderer.setCenterTextHorizontally(centerTextHorizontally);
        return this;
    }

    public LabelWidget setUseTextShadow(boolean useShadow)
    {
        this.stringListRenderer.setUseTextShadow(useShadow);
        return this;
    }

    public LabelWidget setNormalTextColor(int color)
    {
        this.stringListRenderer.setNormalTextColor(color);
        return this;
    }

    public LabelWidget setHoverTextColor(int color)
    {
        this.stringListRenderer.setHoverTextColor(color);
        return this;
    }

    public LabelWidget setUseBackgroundForHoverOverflow(boolean useBackground)
    {
        this.useBackgroundForHoverOverflow = useBackground;
        return this;
    }

    protected void updateLabelWidgetSize()
    {
        this.updateWidth();
        this.updateHeight();
        this.updateStringRendererSize();
    }

    @Override
    protected void onSizeChanged()
    {
        this.updateStringRendererSize();
    }

    protected void updateStringRendererSize()
    {
        int width = this.hasMaxWidth ? this.maxWidth : this.getWidth();
        int height = this.hasMaxHeight ? this.maxHeight : this.getHeight();
        int bw = this.getActiveBorderWidth() * 2;

        this.stringListRenderer.setMaxWidth(width - this.padding.getLeft() - this.padding.getRight() - bw);
        this.stringListRenderer.setMaxHeight(height - this.padding.getTop() - this.padding.getBottom() - bw);
        this.stringListRenderer.reAddLines();
    }

    @Override
    public void updateWidth()
    {
        this.totalWidth = this.stringListRenderer.getTotalTextWidth() + this.padding.getLeft() + this.padding.getRight();
        this.totalWidth += this.getActiveBorderWidth() * 2;

        if (this.automaticWidth)
        {
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
        this.totalHeight = this.stringListRenderer.getTotalHeight() + this.padding.getTop() + this.padding.getBottom();
        this.totalHeight += this.getActiveBorderWidth() * 2;

        if (this.automaticHeight)
        {
            int height = this.totalHeight;

            if (this.hasMaxHeight)
            {
                height = Math.min(height, this.maxHeight);
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
            if (hovered && this.backgroundEnabled == false && this.useBackgroundForHoverOverflow && this.stringListRenderer.hasClampedContent())
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
            x += this.padding.getLeft() + bw;
            y += this.padding.getTop() + bw;

            this.stringListRenderer.renderAt(x, y, z, hovered);

            RenderUtils.color(1f, 1f, 1f, 1f);
        }
    }
}
