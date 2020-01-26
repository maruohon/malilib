package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.IIconProvider;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetRadioButton<T extends Enum<T>> extends WidgetBase
{
    protected final List<T> options;
    protected final Function<T, String> displayStringFunction;
    protected final int textWidth;
    protected IIconProvider<IconType> iconProvider;
    protected int entryHeight;
    @Nullable protected T selectedEntry;
    @Nullable protected ISelectionListener<WidgetRadioButton<T>> listener;

    public WidgetRadioButton(int x, int y, List<T> options, Function<T, String> displayStringFunction)
    {
        this(x, y, options, displayStringFunction, null);
    }

    public WidgetRadioButton(int x, int y, List<T> options, Function<T, String> displayStringFunction, @Nullable String hoverInfoKey)
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
        this.setHoverStrings(hoverInfoKey);
        this.setIconProvider(new IconProvider());
    }

    public void setIconProvider(IIconProvider<IconType> provider)
    {
        this.iconProvider = provider;
        this.updateSizes();
    }

    public void setSelectionListener(ISelectionListener<WidgetRadioButton<T>> listener)
    {
        this.listener = listener;
    }

    protected void updateSizes()
    {
        IGuiIcon icon = this.iconProvider.getIconFor(IconType.UNSELECTED);
        int width = icon != null ? icon.getWidth() : this.iconProvider.getExpectedWidth();
        int height = icon != null ? icon.getHeight() : this.fontHeight + 1;

        this.width = this.textWidth + width + 3;
        this.entryHeight = Math.max((this.fontHeight + 1), height);
        this.height = this.entryHeight * this.options.size();
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
        int entryIndex = (mouseY - this.y) / this.entryHeight;

        if (entryIndex >= 0 && entryIndex < this.options.size())
        {
            this.setSelection(this.options.get(entryIndex), true);
        }

        return true;
    }

    protected IconType getIconTypeFor(int mouseX, int mouseY, T entry, int y, boolean selected)
    {
        boolean hovered = GuiBase.isMouseOver(mouseX, mouseY, this.x, y, this.width, this.entryHeight);

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
    public void render(int mouseX, int mouseY, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int y = this.y;

        for (T entry : this.options)
        {
            boolean entrySelected = this.selectedEntry == entry;
            IGuiIcon icon = this.iconProvider.getIconFor(this.getIconTypeFor(mouseX, mouseY, entry, y, entrySelected));
            int iconWidth = 0;

            if (icon != null)
            {
                iconWidth = icon.getWidth() + 3;
                int iconHeight = icon.getHeight();
                int iconY = y + (this.entryHeight - iconHeight) / 2;
                icon.renderAt(this.x, iconY, this.zLevel, false, false);
            }

            int textY = y + 1 + (this.entryHeight - this.fontHeight) / 2;
            int textColor = entrySelected ? 0xFFFFFFFF : 0xB0B0B0B0;

            String displayString = this.displayStringFunction.apply(entry);
            this.drawStringWithShadow(this.x + iconWidth, textY, textColor, displayString);

            y += this.entryHeight;
        }
    }

    public enum IconType
    {
        UNSELECTED,
        SELECTED,
        UNSELECTED_HOVER,
        SELECTED_HOVER;
    }

    public static class IconProvider implements IIconProvider<IconType>
    {
        @Override
        public int getExpectedWidth()
        {
            return 8;
        }

        @Override
        public IGuiIcon getIconFor(IconType type)
        {
            switch (type)
            {
                case UNSELECTED:        return GuiIconBase.RADIO_BUTTON_UNSELECTED_NORMAL;
                case UNSELECTED_HOVER:  return GuiIconBase.RADIO_BUTTON_UNSELECTED_HOVER;
                case SELECTED:          return GuiIconBase.RADIO_BUTTON_SELECTED_NORMAL;
                case SELECTED_HOVER:    return GuiIconBase.RADIO_BUTTON_SELECTED_HOVER;
            }

            return null;
        }
    }
}
