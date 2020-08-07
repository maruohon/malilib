package fi.dy.masa.malilib.gui.widget;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.RenderUtils;

public class RadioButtonWidget<T extends Enum<T>> extends BaseWidget
{
    protected final List<T> options;
    protected final Function<T, String> displayStringFunction;
    protected final int textWidth;
    protected fi.dy.masa.malilib.gui.icon.IconProvider<IconType> iconProvider;
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
        this.addHoverString(hoverInfoKey);
        this.setIconProvider(new IconProvider());
    }

    public void setIconProvider(fi.dy.masa.malilib.gui.icon.IconProvider<IconType> provider)
    {
        this.iconProvider = provider;
        this.updateSizes();
    }

    public void setSelectionListener(SelectionListener<RadioButtonWidget<T>> listener)
    {
        this.listener = listener;
    }

    protected void updateSizes()
    {
        Icon icon = this.iconProvider.getIconFor(IconType.UNSELECTED);
        int iconWidth = icon != null ? icon.getWidth() : this.iconProvider.getExpectedWidth();
        int iconHeight = icon != null ? icon.getHeight() : this.fontHeight + 1;

        this.entryHeight = Math.max((this.fontHeight + 1), iconHeight);

        this.setWidth(this.textWidth + iconWidth + 3);
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
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        int entryIndex = (mouseY - this.getY()) / this.entryHeight;

        if (entryIndex >= 0 && entryIndex < this.options.size())
        {
            this.setSelection(this.options.get(entryIndex), true);
        }

        return true;
    }

    protected IconType getIconTypeFor(int mouseX, int mouseY, T entry, int y, boolean selected)
    {
        boolean hovered = GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), y, this.getWidth(), this.entryHeight);

        if (selected)
        {
            return hovered ? IconType.SELECTED_HOVER : IconType.SELECTED;
        }
        else
        {
            return hovered ? IconType.UNSELECTED_HOVER : IconType.UNSELECTED;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int x = this.getX();
        int y = this.getY();

        for (T entry : this.options)
        {
            boolean entrySelected = this.selectedEntry == entry;
            Icon icon = this.iconProvider.getIconFor(this.getIconTypeFor(mouseX, mouseY, entry, y, entrySelected));
            int iconWidth = 0;

            if (icon != null)
            {
                iconWidth = icon.getWidth() + 3;
                int iconHeight = icon.getHeight();
                int iconY = y + (this.entryHeight - iconHeight) / 2;
                icon.renderAt(x, iconY, this.getZLevel(), false, false);
            }

            int textY = y + 1 + (this.entryHeight - this.fontHeight) / 2;
            int textColor = entrySelected ? 0xFFFFFFFF : 0xB0B0B0B0;

            String displayString = this.displayStringFunction.apply(entry);
            this.drawStringWithShadow(x + iconWidth, textY, textColor, displayString);

            y += this.entryHeight;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    public enum IconType
    {
        UNSELECTED,
        SELECTED,
        UNSELECTED_HOVER,
        SELECTED_HOVER;
    }

    public static class IconProvider implements fi.dy.masa.malilib.gui.icon.IconProvider<IconType>
    {
        @Override
        public int getExpectedWidth()
        {
            return 8;
        }

        @Override
        public Icon getIconFor(IconType type)
        {
            switch (type)
            {
                case UNSELECTED:        return BaseIcon.RADIO_BUTTON_UNSELECTED_NORMAL;
                case UNSELECTED_HOVER:  return BaseIcon.RADIO_BUTTON_UNSELECTED_HOVER;
                case SELECTED:          return BaseIcon.RADIO_BUTTON_SELECTED_NORMAL;
                case SELECTED_HOVER:    return BaseIcon.RADIO_BUTTON_SELECTED_HOVER;
            }

            return null;
        }
    }
}
