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
    protected final int textWidth;
    protected boolean checked;
    @Nullable protected ISelectionListener<WidgetCheckBox> listener;

    public WidgetCheckBox(int x, int y, IGuiIcon iconUnchecked, IGuiIcon iconChecked, String text)
    {
        super(x, y, 40, 20);

        this.displayText = text;
        this.width = iconUnchecked.getWidth() + 3 + this.getStringWidth(text);
        this.height = Math.max(this.fontHeight, iconChecked.getHeight());
        this.textWidth = this.getStringWidth(text);
        this.widgetUnchecked = iconUnchecked;
        this.widgetChecked = iconChecked;
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
    public void render(int mouseX, int mouseY, boolean selected)
    {
        IGuiIcon icon = this.checked ? this.widgetChecked : this.widgetUnchecked;

        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(icon.getTexture());
        icon.renderAt(this.x, this.y, this.zLevel, false, false);

        int iw = icon.getWidth();
        int y = this.y + 1 + (this.height - this.fontHeight) / 2;
        int textColor = this.checked ? 0xFFFFFFFF : 0xB0B0B0B0;

        this.drawStringWithShadow(this.x + iw + 3, y, textColor, this.displayText);
    }
}
