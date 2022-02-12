package fi.dy.masa.malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class CheckBoxWidget extends InteractableWidget
{
    protected final MultiIcon iconUnchecked;
    protected final MultiIcon iconChecked;
    @Nullable protected Consumer<Boolean> listener;
    protected BooleanSupplier booleanSupplier = () -> false;
    protected Consumer<Boolean> booleanConsumer = (v) -> {};
    protected int textColorChecked = 0xFFFFFFFF;
    protected int textColorUnchecked = 0xB0B0B0B0;

    public CheckBoxWidget(MultiIcon iconUnchecked, MultiIcon iconChecked, @Nullable String translationKey)
    {
        super(0, 0);

        this.text = translationKey != null ? StyledTextLine.translate(translationKey) : null;
        this.iconUnchecked = iconUnchecked;
        this.iconChecked = iconChecked;
        this.textOffset.setYOffset(-1);

        this.setBooleanStorage(new BooleanConfig("", false));

        int textWidth = this.text != null ? this.text.renderWidth : 0;
        int ih = iconChecked.getHeight();
        this.setWidth(iconUnchecked.getWidth() + (textWidth > 0 ? textWidth + 3 : 0));
        this.setHeight(textWidth > 0 ? Math.max(this.getFontHeight(), ih) : ih);

        this.updateState();
    }

    public CheckBoxWidget(@Nullable String translationKey, @Nullable String hoverInfoKey)
    {
        this(DefaultIcons.CHECKMARK_OFF, DefaultIcons.CHECKMARK_ON, translationKey, hoverInfoKey);
    }

    public CheckBoxWidget(MultiIcon iconUnchecked, MultiIcon iconChecked,
                          @Nullable String translationKey, @Nullable String hoverInfoKey)
    {
        this(iconUnchecked, iconChecked, translationKey);

        if (hoverInfoKey != null)
        {
            this.translateAndAddHoverStrings(hoverInfoKey);
        }
    }

    public CheckBoxWidget setTextColorChecked(int color)
    {
        this.textColorChecked = color;
        this.updateState();
        return this;
    }

    public CheckBoxWidget setTextColorUnchecked(int color)
    {
        this.textColorUnchecked = color;
        this.updateState();
        return this;
    }

    public void setBooleanStorage(BooleanStorage storage)
    {
        this.setBooleanStorage(storage::getBooleanValue, storage::setBooleanValue);
    }

    public void setBooleanStorage(BooleanSupplier booleanSupplier, Consumer<Boolean> booleanConsumer)
    {
        this.booleanSupplier = booleanSupplier;
        this.booleanConsumer = booleanConsumer;
        this.updateState();
    }

    public void setListener(@Nullable Consumer<Boolean> listener)
    {
        this.listener = listener;
    }

    public boolean isSelected()
    {
        return this.booleanSupplier.getAsBoolean();
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
        this.booleanConsumer.accept(selected);
        this.updateState();

        if (notifyListener && this.listener != null)
        {
            this.listener.accept(selected);
        }
    }

    protected void updateState()
    {
        boolean selected = this.isSelected();
        Icon icon = selected ? this.iconChecked : this.iconUnchecked;
        int textXOffset = icon.getWidth() + 3;

        this.getTextSettings().setTextColor(selected ? this.textColorChecked : this.textColorUnchecked);
        this.getTextOffset().setXOffset(textXOffset);
        this.setIcon(icon);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.setSelected(! this.isSelected());
        return true;
    }
}
