package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetCheckBox extends WidgetBase
{
    protected final String displayText;
    protected final IGuiIcon widgetUnchecked;
    protected final IGuiIcon widgetChecked;
    protected int textColorChecked = 0xFFFFFFFF;
    protected int textColorUnchecked = 0xB0B0B0B0;
    protected boolean checked;
    @Nullable protected ISelectionListener<WidgetCheckBox> listener;

    public WidgetCheckBox(int x, int y, IGuiIcon iconUnchecked, IGuiIcon iconChecked, String text)
    {
        super(x, y, 0, 0);

        this.displayText = text;
        this.widgetUnchecked = iconUnchecked;
        this.widgetChecked = iconChecked;

        int sw = this.getStringWidth(text);
        this.setWidth(iconUnchecked.getWidth() + (sw > 0 ? sw + 3 : 0));
        this.setHeight(Math.max(this.fontHeight, iconChecked.getHeight()));
    }

    public WidgetCheckBox(int x, int y, IGuiIcon iconUnchecked, IGuiIcon iconChecked, String text, @Nullable String hoverInfo)
    {
        this(x, y, iconUnchecked, iconChecked, text);

        this.addHoverString(hoverInfo);
    }

    public void setListener(@Nullable ISelectionListener<WidgetCheckBox> listener)
    {
        this.listener = listener;
    }

    public boolean isChecked()
    {
        return this.checked;
    }

    public void setChecked(boolean checked)
    {
        this.setChecked(checked, true);
    }

    public WidgetCheckBox setTextColorChecked(int color)
    {
        this.textColorChecked = color;
        return this;
    }

    public WidgetCheckBox setTextColorUnchecked(int color)
    {
        this.textColorUnchecked = color;
        return this;
    }

    /**
     * Set the current checked value
     * @param checked
     * @param notifyListener If true, then the change listener (if set) will be notified.
     * If false, then the listener will not be notified
     */
    public void setChecked(boolean checked, boolean notifyListener)
    {
        this.checked = checked;

        if (notifyListener && this.listener != null)
        {
            this.listener.onSelectionChange(this);
        }
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.setChecked(! this.checked);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        IGuiIcon icon = this.checked ? this.widgetChecked : this.widgetUnchecked;

        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(icon.getTexture());

        int x = this.getX();
        int y = this.getY();
        int iw = icon.getWidth();
        int textColor = this.checked ? this.textColorChecked : this.textColorUnchecked;

        icon.renderAt(x, y, this.getZLevel(), false, false);

        this.drawStringWithShadow(x + iw + 3, y + this.getCenteredTextOffsetY(), textColor, this.displayText);

        RenderUtils.color(1f, 1f, 1f, 1f);
    }
}
