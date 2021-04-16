package fi.dy.masa.malilib.gui.widget;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class CheckBoxWidget extends InteractableWidget
{
    protected final MultiIcon widgetUnchecked;
    protected final MultiIcon widgetChecked;
    @Nullable protected final StyledTextLine displayText;
    @Nullable protected Consumer<Boolean> listener;
    protected BooleanStorage storage;
    protected int textColorChecked = 0xFFFFFFFF;
    protected int textColorUnchecked = 0xB0B0B0B0;

    public CheckBoxWidget(int x, int y, MultiIcon iconUnchecked, MultiIcon iconChecked, @Nullable String text)
    {
        super(x, y, 0, 0);

        this.displayText = text != null ? StyledTextLine.of(text) : null;
        this.widgetUnchecked = iconUnchecked;
        this.widgetChecked = iconChecked;
        this.storage = new BooleanConfig("", false);

        int textWidth = this.displayText != null ? this.displayText.renderWidth : 0;
        int ih = iconChecked.getHeight();
        this.setWidth(iconUnchecked.getWidth() + (textWidth > 0 ? textWidth + 3 : 0));
        this.setHeight(textWidth > 0 ? Math.max(this.fontHeight, ih) : ih);
    }

    public CheckBoxWidget(int x, int y, MultiIcon iconUnchecked, MultiIcon iconChecked, @Nullable String text, String hoverInfoKey)
    {
        this(x, y, iconUnchecked, iconChecked, text);

        this.translateAndAddHoverStrings(hoverInfoKey);
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

    public void setBooleanStorage(BooleanStorage storage)
    {
        this.storage = storage;
    }

    public void setListener(@Nullable Consumer<Boolean> listener)
    {
        this.listener = listener;
    }

    public boolean isSelected()
    {
        return this.storage.getBooleanValue();
    }

    public void setSelected(boolean selected)
    {
        this.setSelected(selected, true);
    }

    /**
     * Set the current selected value/state
     * @param notifyListener If true, then the change listener (if set) will be notified.
     * If false, then the listener will not be notified
     */
    public void setSelected(boolean selected, boolean notifyListener)
    {
        this.storage.setBooleanValue(selected);

        if (notifyListener && this.listener != null)
        {
            this.listener.accept(selected);
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.setSelected(! this.isSelected());
        return true;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        boolean selected = this.isSelected();
        MultiIcon icon = selected ? this.widgetChecked : this.widgetUnchecked;
        int textColor = selected ? this.textColorChecked : this.textColorUnchecked;

        icon.renderAt(x, y, z, false, false);

        if (this.displayText != null)
        {
            this.renderTextLine(x + icon.getWidth() + 3, y + this.getCenteredTextOffsetY(), z, textColor, true, this.displayText);
        }
    }
}
