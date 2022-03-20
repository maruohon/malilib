package fi.dy.masa.malilib.gui.widget;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.SelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class RadioButtonWidget<T extends Enum<T>> extends InteractableWidget
{
    protected final ImmutableList<T> options;
    protected final ImmutableList<StyledTextLine> displayStrings;
    protected final int textWidth;
    protected MultiIcon iconSelected = DefaultIcons.RADIO_BUTTON_SELECTED;
    protected MultiIcon iconUnselected = DefaultIcons.RADIO_BUTTON_UNSELECTED;
    protected int entryHeight;
    protected int selectedTextColor = 0xFFFFFFFF;
    protected int unselectedTextColor = 0xFFB0B0B0;
    @Nullable protected T selectedEntry;
    @Nullable protected SelectionListener<RadioButtonWidget<T>> listener;

    public RadioButtonWidget(List<T> options, Function<T, String> displayStringFunction)
    {
        this(options, displayStringFunction, null);
    }

    public RadioButtonWidget(List<T> options, Function<T, String> displayStringFunction, @Nullable String hoverInfoKey)
    {
        super(10, 10);

        this.options = ImmutableList.copyOf(options);

        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();
        int width = 0;

        for (T val : this.options)
        {
            String displayString = displayStringFunction.apply(val);
            width = Math.max(width, this.getStringWidth(displayString));
            builder.add(StyledTextLine.of(displayString));
        }

        this.displayStrings = builder.build();
        this.textWidth = width;

        if (hoverInfoKey != null)
        {
            this.translateAndAddHoverString(hoverInfoKey);
        }

        this.updateSizes();
    }

    public void setTextColor(int selectedTextColor, int unselectedTextColor)
    {
        this.selectedTextColor = selectedTextColor;
        this.unselectedTextColor = unselectedTextColor;
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
        int iconHeight = icon != null ? icon.getHeight() : 0;

        this.entryHeight = Math.max(this.getLineHeight() + 1, iconHeight);

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
        if (this.isEnabled() == false)
        {
            return;
        }

        this.selectedEntry = entry;

        if (notifyListener && this.listener != null)
        {
            this.listener.onSelectionChange(this);
        }
    }

    @Override
    protected int getTextColorForRender(boolean entrySelected)
    {
        if (this.isEnabled() == false)
        {
            return 0xFF707070;
        }

        return entrySelected ? this.selectedTextColor : this.unselectedTextColor;
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        final boolean hovered = this.isHoveredForRender(ctx);
        final int count = Math.min(this.options.size(), this.displayStrings.size());
        final int width = this.getWidth();
        final int wx = this.getX();
        final int mouseX = ctx.mouseX;
        final int mouseY = ctx.mouseY;

        for (int i = 0; i < count; ++i)
        {
            final boolean entrySelected = this.selectedEntry == this.options.get(i);
            final MultiIcon icon = entrySelected ? this.iconSelected : this.iconUnselected;
            int textOffsetX = 0;

            if (icon != null)
            {
                textOffsetX = icon.getWidth() + 3;
                int iconHeight = icon.getHeight();
                int iconY = y + (this.entryHeight - iconHeight) / 2;
                boolean entryHovered = hovered && GuiUtils.isMouseInRegion(mouseX, mouseY, wx, y, width, this.entryHeight);
                icon.renderAt(x, iconY, z, true, entryHovered);
            }

            final int textY = y + 1 + (this.entryHeight - this.getLineHeight()) / 2;
            final int textColor = this.getTextColorForRender(entrySelected);

            StyledTextLine displayString = this.displayStrings.get(i);
            this.renderTextLine(x + textOffsetX, textY, z, textColor, true, displayString, ctx);

            y += this.entryHeight;
        }
    }
}
