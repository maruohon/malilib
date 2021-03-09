package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class CheckBoxWidget extends InteractableWidget
{
    protected final StyledTextLine displayText;
    protected final MultiIcon widgetUnchecked;
    protected final MultiIcon widgetChecked;
    protected int textColorChecked = 0xFFFFFFFF;
    protected int textColorUnchecked = 0xB0B0B0B0;
    protected boolean checked;
    @Nullable protected SelectionListener<CheckBoxWidget> listener;

    public CheckBoxWidget(int x, int y, MultiIcon iconUnchecked, MultiIcon iconChecked, String text)
    {
        super(x, y, 0, 0);

        this.displayText = StyledTextLine.of(text);
        this.widgetUnchecked = iconUnchecked;
        this.widgetChecked = iconChecked;

        int textWidth = this.displayText.renderWidth;
        this.setWidth(iconUnchecked.getWidth() + (textWidth > 0 ? textWidth + 3 : 0));
        this.setHeight(Math.max(this.fontHeight, iconChecked.getHeight()));
    }

    public CheckBoxWidget(int x, int y, MultiIcon iconUnchecked, MultiIcon iconChecked, String text, @Nullable String hoverInfo)
    {
        this(x, y, iconUnchecked, iconChecked, text);

        this.addHoverStrings(hoverInfo);
    }

    public void setListener(@Nullable SelectionListener<CheckBoxWidget> listener)
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

    public CheckBoxWidget setTextColorChecked(int color)
    {
        this.textColorChecked = color;
        return this;
    }

    public CheckBoxWidget setTextColorUnchecked(int color)
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
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.setChecked(! this.checked);
        return true;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        MultiIcon icon = this.checked ? this.widgetChecked : this.widgetUnchecked;
        int textColor = this.checked ? this.textColorChecked : this.textColorUnchecked;

        icon.renderAt(x, y, z, false, false);
        this.renderTextLine(x + icon.getWidth() + 3, y + this.getCenteredTextOffsetY(), z, textColor, true, this.displayText);
    }
}
