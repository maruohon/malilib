package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class RadioButtonWidget<T extends Enum<T>> extends InteractableWidget
{
    protected final List<T> options;
    protected final List<StyledTextLine> displayStrings = new ArrayList<>();
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

        int width = 0;

        for (T val : options)
        {
            String displayString = displayStringFunction.apply(val);
            width = Math.max(width, this.getStringWidth(displayString));
            this.displayStrings.add(StyledTextLine.of(displayString));
        }

        this.options = options;
        this.textWidth = width;

        if (hoverInfoKey != null)
        {
            this.translateAndAddHoverString(hoverInfoKey);
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int count = Math.min(this.options.size(), this.displayStrings.size());
        boolean hovered = this.isHoveredForRender(ctx);
        int width = this.getWidth();
        int wx = this.getX();
        int mouseX = ctx.mouseX;
        int mouseY = ctx.mouseY;

        for (int i = 0; i < count; ++i)
        {
            T entry = this.options.get(i);
            StyledTextLine displayString = this.displayStrings.get(i);
            boolean entrySelected = this.selectedEntry == entry;
            MultiIcon icon = entrySelected ? this.iconSelected : this.iconUnselected;
            int iconWidth = 0;

            if (icon != null)
            {
                iconWidth = icon.getWidth() + 3;
                int iconHeight = icon.getHeight();
                int iconY = y + (this.entryHeight - iconHeight) / 2;
                boolean isMouseOverChoice = GuiUtils.isMouseInRegion(mouseX, mouseY, wx, y, width, this.entryHeight);
                boolean entryHovered = hovered && isMouseOverChoice;
                icon.renderAt(x, iconY, z, false, entryHovered);
            }

            int textY = y + 1 + (this.entryHeight - this.fontHeight) / 2;
            int textColor = entrySelected ? 0xFFFFFFFF : 0xB0B0B0B0;

            this.renderTextLine(x + iconWidth, textY, z, textColor, true, ctx, displayString);
            y += this.entryHeight;
        }
    }
}
