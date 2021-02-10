package fi.dy.masa.malilib.gui.widget;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;

public class RadioButtonWidget<T extends Enum<T>> extends InteractableWidget
{
    protected final List<T> options;
    protected final Function<T, String> displayStringFunction;
    protected final int textWidth;
    protected MultiIcon iconSelected = DefaultIcons.RADIO_BUTTON_SELECTED;
    protected MultiIcon iconUnselected = DefaultIcons.RADIO_BUTTON_UNSELECTED;
    protected int entryHeight;
    @Nullable protected T selectedEntry;
    @Nullable protected SelectionListener<RadioButtonWidget<T>> listener;

    public RadioButtonWidget(int x, int y, List<T> options, Function<T, String> displayStringFunction)
    {
        this(x, y, options, displayStringFunction, null);
    }

    public RadioButtonWidget(int x, int y, List<T> options, Function<T, String> displayStringFunction, @Nullable String hoverInfoKey)
    {
        super(x, y, 10, 10);

        this.options = options;
        this.displayStringFunction = displayStringFunction;

        int width = 0;

        for (T val : options)
        {
            width = Math.max(width, this.getStringWidth(displayStringFunction.apply(val)));
        }

        this.textWidth = width;

        if (hoverInfoKey != null)
        {
            this.addHoverString(hoverInfoKey);
        }
    }

    public void setIcons(MultiIcon iconSelected, MultiIcon iconUnselected)
    {
        this.iconSelected = iconSelected;
        this.iconUnselected = iconUnselected;
        this.updateSizes();
    }

    public void setSelectionListener(SelectionListener<RadioButtonWidget<T>> listener)
    {
        this.listener = listener;
    }

    protected void updateSizes()
    {
        MultiIcon icon = this.iconUnselected;
        int iconWidth = icon != null ? icon.getWidth() + 3 : 0;
        int iconHeight = icon != null ? icon.getHeight() : this.fontHeight + 1;

        this.entryHeight = Math.max((this.fontHeight + 1), iconHeight);

        this.setWidth(this.textWidth + iconWidth);
        this.setHeight(this.entryHeight * this.options.size());
    }

    @Nullable
    public T getSelection()
    {
        return this.selectedEntry;
    }

    public void setSelection(T entry, boolean notifyListener)
    {
        this.selectedEntry = entry;

        if (notifyListener && this.listener != null)
        {
            this.listener.onSelectionChange(this);
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        int entryIndex = (mouseY - this.getY()) / this.entryHeight;

        if (entryIndex >= 0 && entryIndex < this.options.size())
        {
            this.setSelection(this.options.get(entryIndex), true);
        }

        return true;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        for (T entry : this.options)
        {
            boolean entrySelected = this.selectedEntry == entry;
            MultiIcon icon = entrySelected ? this.iconSelected : this.iconUnselected;
            int iconWidth = 0;

            if (icon != null)
            {
                iconWidth = icon.getWidth() + 3;
                int iconHeight = icon.getHeight();
                int iconY = y + (this.entryHeight - iconHeight) / 2;
                boolean entryHovered = hovered && GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), y, this.getWidth(), this.entryHeight);
                icon.renderAt(x, iconY, z, false, entryHovered);
            }

            int textY = y + 1 + (this.entryHeight - this.fontHeight) / 2;
            int textColor = entrySelected ? 0xFFFFFFFF : 0xB0B0B0B0;

            String displayString = this.displayStringFunction.apply(entry);
            this.drawStringWithShadow(x + iconWidth, textY, z, textColor, displayString);

            y += this.entryHeight;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
    }
}
