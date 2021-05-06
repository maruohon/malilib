package fi.dy.masa.malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class CheckBoxWidget extends InteractableWidget
{
    protected final MultiIcon iconUnchecked;
    protected final MultiIcon iconChecked;
    protected BooleanSupplier booleanSupplier;
    protected Consumer<Boolean> booleanConsumer;
    @Nullable protected Consumer<Boolean> listener;
    protected int textColorChecked = 0xFFFFFFFF;
    protected int textColorUnchecked = 0xB0B0B0B0;

    public CheckBoxWidget(int x, int y, MultiIcon iconUnchecked, MultiIcon iconChecked,
                          @Nullable String translationKey)
    {
        super(x, y, 0, 0);

        this.text = translationKey != null ? StyledTextLine.translate(translationKey) : null;
        this.iconUnchecked = iconUnchecked;
        this.iconChecked = iconChecked;
        this.textOffsetY = -1;

        this.setBooleanStorage(new BooleanConfig("", false));

        int textWidth = this.text != null ? this.text.renderWidth : 0;
        int ih = iconChecked.getHeight();
        this.setWidth(iconUnchecked.getWidth() + (textWidth > 0 ? textWidth + 3 : 0));
        this.setHeight(textWidth > 0 ? Math.max(this.fontHeight, ih) : ih);
    }

    public CheckBoxWidget(int x, int y, @Nullable String translationKey, @Nullable String hoverInfoKey)
    {
        this(x, y, DefaultIcons.CHECKMARK_OFF, DefaultIcons.CHECKMARK_ON, translationKey, hoverInfoKey);
    }

    public CheckBoxWidget(int x, int y, MultiIcon iconUnchecked, MultiIcon iconChecked,
                          @Nullable String translationKey, @Nullable String hoverInfoKey)
    {
        this(x, y, iconUnchecked, iconChecked, translationKey);

        if (hoverInfoKey != null)
        {
            this.translateAndAddHoverStrings(hoverInfoKey);
        }
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
        this.setBooleanStorage(storage::getBooleanValue, storage::setBooleanValue);
    }

    public void setBooleanStorage(BooleanSupplier booleanSupplier, Consumer<Boolean> booleanConsumer)
    {
        this.booleanSupplier = booleanSupplier;
        this.booleanConsumer = booleanConsumer;
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        boolean selected = this.isSelected();
        MultiIcon icon = selected ? this.iconChecked : this.iconUnchecked;

        this.defaultNormalTextColor = selected ? this.textColorChecked : this.textColorUnchecked;
        this.textOffsetX = icon.getWidth() + 3;

        super.renderAt(x, y, z, ctx);

        icon.renderAt(x, y, z, false, false);
    }
}
