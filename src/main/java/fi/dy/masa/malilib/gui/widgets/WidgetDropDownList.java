package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.IIconProvider;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.LeftRight;

/**
 * A dropdown selection widget for entries in the given list.
 * If the entries extend {@link IStringValue}, then the {@link IStringValue#getStringValue()}
 * method is used for the display string, otherwise {@link toString()} is used.
 * @author masa
 *
 * @param <T>
 */
public class WidgetDropDownList<T> extends WidgetContainer
{
    protected final List<T> entries;
    protected final List<T> filteredEntries;
    @Nullable protected final IStringRetriever<T> stringRetriever;
    protected final int maxHeight;
    protected final int maxVisibleEntries;
    protected final int lineHeight;

    protected WidgetScrollBar scrollBar;
    protected TextFieldWrapper<GuiTextFieldGeneric> searchField;
    protected LeftRight openIconSide = LeftRight.RIGHT;
    protected boolean isOpen;
    protected boolean noCurrentEntryBar;
    protected int dropdownHeight;
    protected int dropdownTopY;
    protected int selectedIndex;
    protected int textColor = 0xFFE0E0E0;
    protected int totalHeight;
    @Nullable protected WidgetSelectionBar<T> widgetSelectionBar;
    @Nullable protected ButtonGeneric buttonOpenClose;
    @Nullable protected IIconProvider<T> iconProvider;
    @Nullable protected ISelectionListener<T> selectionListener;
    @Nullable protected T selectedEntry;

    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, List<T> entries)
    {
        this(x, y, width, height, maxHeight, maxVisibleEntries, entries, null);
    }

    /**
     * A dropdown selection widget for entries in the given list.
     * This constructor uses the provided string retriever to get the display string for each entry.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param maxHeight
     * @param maxVisibleEntries
     * @param entries
     * @param stringRetriever
     */
    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, List<T> entries, @Nullable IStringRetriever<T> stringRetriever)
    {
        super(x, y, width, height);

        this.lineHeight = height;
        this.maxHeight = maxHeight;
        this.entries = entries;
        this.filteredEntries = new ArrayList<>();
        this.stringRetriever = stringRetriever;

        int v = Math.min(maxVisibleEntries, entries.size());
        v = Math.min(v, maxHeight / height);
        v = Math.min(v, (GuiUtils.getScaledWindowHeight() - y) / height);
        v = Math.max(v, 1);

        this.maxVisibleEntries = v;
        this.dropdownHeight = v * this.lineHeight;
        this.dropdownTopY = y + this.lineHeight;
        this.totalHeight = this.dropdownHeight + this.lineHeight;

        int scrollbarWidth = 8;
        int scrollbarHeight = this.maxVisibleEntries * this.lineHeight;
        // The position gets updated in updateSubElementPositions
        this.scrollBar = new WidgetScrollBar(0, 0, scrollbarWidth, scrollbarHeight);
        this.scrollBar.setMaxValue(this.entries.size() - this.maxVisibleEntries);
        this.scrollBar.setArrowTextures(GuiIconBase.SMALL_ARROW_UP, GuiIconBase.SMALL_ARROW_DOWN);

        this.updateWidth(false); // This creates the search text field, which needs to be set before calling updateFilteredEntries

        this.widgetSelectionBar = new WidgetSelectionBar<T>(x, y, this.getWidth(), height, this.textColor, this);
        this.widgetSelectionBar.setZLevel(this.getZLevel() + 2);

        this.recreateSubElements();
        this.updateFilteredEntries(); // This must be called after the search text field has been created in recreateSubElements
    }

    public void setIconProvider(@Nullable IIconProvider<T> iconProvider)
    {
        this.iconProvider = iconProvider;
        this.updateWidth(true);
    }

    public void setSelectionListener(@Nullable ISelectionListener<T> selectionListener)
    {
        this.selectionListener = selectionListener;
    }

    public void setNoBarWhenClosed(int buttonX, int buttonY, Supplier<IGuiIcon> iconSupplier)
    {
        this.noCurrentEntryBar = true;
        this.buttonOpenClose = ButtonGeneric.createIconOnly(buttonX, buttonY, iconSupplier);
        this.buttonOpenClose.setRenderOutline(false);
        this.buttonOpenClose.setActionListener((btn, mbtn) -> this.toggleOpen());

        this.recreateSubElements();
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);
        this.updateSubElementPositions();
    }

    @Override
    public void setRightAlign(boolean rightAlign, int xRight)
    {
        super.setRightAlign(rightAlign, xRight);
        this.updateSubElementPositions();
    }

    protected void updateWidth(boolean updateSubWidgets)
    {
        this.setWidth(this.getRequiredWidth(-1, this.entries, this.mc));
        this.updatePositionIfRightAligned();

        if (updateSubWidgets)
        {
            this.recreateSubElements();
        }
    }

    protected void recreateSubElements()
    {
        this.clearWidgets();

        if (this.isOpen && this.scrollBar != null)
        {
            this.addWidget(this.scrollBar);
        }

        if (this.noCurrentEntryBar && this.buttonOpenClose != null)
        {
            this.addWidget(this.buttonOpenClose);
        }

        if (this.noCurrentEntryBar == false && this.widgetSelectionBar != null)
        {
            this.addWidget(this.widgetSelectionBar);
        }

        TextFieldListener listener = new TextFieldListener(this);
        this.searchField = new TextFieldWrapper<>(new GuiTextFieldGeneric(this.getX() + 1, this.getY() - 18, this.getWidth() - 2, 16, this.textRenderer), listener);
        this.searchField.getTextField().setFocused(true);

        this.updateSubElementPositions();
    }

    protected void updateSubElementPositions()
    {
        int yOff = this.noCurrentEntryBar ? 0 : this.lineHeight;

        this.dropdownTopY = this.getY() + yOff;
        this.totalHeight = this.dropdownHeight + yOff;

        int x = this.getX();
        int y = this.getY();
        int scrollbarWidth = 8;
        this.scrollBar.setPosition(x + this.getWidth() - scrollbarWidth - 1, y + yOff + 1);

        if (this.widgetSelectionBar != null)
        {
            this.widgetSelectionBar.setPosition(x, y);
            this.widgetSelectionBar.update(this);
        }

        this.searchField.getTextField().x = x + 1;
        this.searchField.getTextField().y = y - 18;
    }

    protected int getRequiredWidth(int width, List<T> entries, Minecraft mc)
    {
        if (width == -1)
        {
            width = 0;
            int right = this.lineHeight + 4;

            for (int i = 0; i < entries.size(); ++i)
            {
                // + right => leave room for a square icon on the right for the open/close arrow
                width = Math.max(width, this.getStringWidth(this.getDisplayString(entries.get(i))) + right);
            }

            if (this.iconProvider != null)
            {
                width += this.iconProvider.getExpectedWidth() + 8;
            }
        }

        return width;
    }

    @Nullable
    public T getSelectedEntry()
    {
        return this.selectedEntry;
    }

    public WidgetDropDownList<T> setSelectedEntry(T entry)
    {
        if (this.entries.contains(entry))
        {
            this.selectedEntry = entry;
            this.updateSelectionBar();
        }

        return this;
    }

    protected boolean setSelectedEntry(int index)
    {
        if (index >= 0 && index < this.filteredEntries.size())
        {
            this.setSelectedEntry(this.filteredEntries.get(index));

            if (this.selectionListener != null)
            {
                this.selectionListener.onSelectionChange(this.selectedEntry);
            }

            return true;
        }

        return false;
    }

    public boolean isOpen()
    {
        return this.isOpen;
    }

    protected void toggleOpen()
    {
        this.isOpen = ! this.isOpen;

        if (this.isOpen == false)
        {
            this.searchField.getTextField().setText("");
            this.updateFilteredEntries();
        }

        // Add/remove the sub widgets as needed
        this.recreateSubElements();

        if (this.noCurrentEntryBar == false)
        {
            this.updateSelectionBar();
        }
    }

    protected void updateSelectionBar()
    {
        if (this.widgetSelectionBar != null)
        {
            this.widgetSelectionBar.update(this);
        }
    }

    @Override
    public int getHeight()
    {
        if (this.isOpen)
        {
            return this.totalHeight;
        }

        return this.noCurrentEntryBar ? 0 : this.lineHeight;
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        if (this.noCurrentEntryBar && this.isOpen == false)
        {
            return this.buttonOpenClose != null && this.buttonOpenClose.isMouseOver(mouseX, mouseY);
        }

        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() &&
               mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }

    @Override
    public boolean getShouldReceiveOutsideClicks()
    {
        return true;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        // Close the dropdown when clicking outside of it
        if (this.isMouseOver(mouseX, mouseY) == false)
        {
            if (this.isOpen)
            {
                this.toggleOpen();
                return true;
            }

            return false;
        }

        // This handles the open/close button in the no-entry-bar case, plus the entry bar clicks 
        if (super.onMouseClickedImpl(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.isOpen && mouseY >= this.dropdownTopY)
        {
            if (mouseX < this.getX() + this.getWidth() - this.scrollBar.getWidth())
            {
                int relIndex = (mouseY - this.dropdownTopY) / this.lineHeight;

                if (this.setSelectedEntry(this.scrollBar.getValue() + relIndex))
                {
                    this.toggleOpen();
                    return true;
                }
            }
            else
            {
                if (this.scrollBar.onMouseClicked(mouseX, mouseY, mouseButton))
                {
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollBar.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.isOpen)
        {
            int amount = mouseWheelDelta < 0 ? 1 : -1;
            this.scrollBar.offsetValue(amount);
        }

        return false;
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.isOpen)
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                if (this.searchField.getTextField().isFocused()
                    && this.searchField.getTextField().getText().isEmpty() == false)
                {
                    this.searchField.getTextField().setText("");
                    this.updateFilteredEntries();
                    return true;
                }

                this.toggleOpen();
                return true;
            }

            return this.searchField.keyTyped(typedChar, keyCode);
        }

        return false;
    }

    protected void updateFilteredEntries()
    {
        this.filteredEntries.clear();
        String filterText = this.searchField.getTextField().getText();

        if (this.isOpen && filterText.isEmpty() == false)
        {
            for (int i = 0; i < this.entries.size(); ++i)
            {
                T entry = this.entries.get(i);

                if (this.entryMatchesFilter(entry, filterText))
                {
                    this.filteredEntries.add(entry);
                }
            }

            this.scrollBar.setValue(0);
        }
        else
        {
            this.filteredEntries.addAll(this.entries);
        }

        this.scrollBar.setMaxValue(this.filteredEntries.size() - this.maxVisibleEntries);
    }

    protected boolean entryMatchesFilter(T entry, String filterText)
    {
        return filterText.isEmpty() || this.getDisplayString(entry).toLowerCase().indexOf(filterText) != -1;
    }

    public String getCurrentEntryDisplayString()
    {
        return this.getDisplayString(this.getSelectedEntry());
    }

    protected String getDisplayString(T entry)
    {
        if (entry != null)
        {
            if (this.stringRetriever != null)
            {
                return this.stringRetriever.getStringValue(entry);
            }

            return entry.toString();
        }

        return "-";
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        super.render(mouseX, mouseY, selected);

        // Render the open dropdown list
        if (this.isOpen)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, this.getZLevel() + 40);

            if (this.searchField.getTextField().getText().isEmpty() == false)
            {
                this.searchField.draw();
            }

            List<T> list = this.filteredEntries;
            int visibleEntries = Math.min(this.maxVisibleEntries, list.size());
            int height = visibleEntries * this.lineHeight;
            int totalHeight = Math.max(visibleEntries, list.size()) * this.lineHeight;
            int x = this.getX();
            int width = this.getWidth();

            RenderUtils.drawOutlinedBox(x, this.dropdownTopY, width, height + 2, 0xD0000000, 0xFFE0E0E0, this.getZLevel());

            boolean mouseOverListOnX = mouseX >= x && mouseX < x + width - this.scrollBar.getWidth();
            int txtY = this.dropdownTopY + this.lineHeight / 2 - this.fontHeight / 2 + 1;
            this.renderListContents(txtY, mouseX, mouseY, mouseOverListOnX);

            this.scrollBar.render(mouseX, mouseY, height, totalHeight);

            GlStateManager.popMatrix();
        }
        else if (this.noCurrentEntryBar && this.buttonOpenClose != null)
        {
            //this.buttonOpenClose.render(mouseX, mouseY, selected);
        }
    }

    protected void renderListContents(int txtY, int mouseX, int mouseY, boolean mouseOverListOnX)
    {
        List<T> list = this.filteredEntries;
        int height = this.lineHeight;
        int y = this.dropdownTopY + 1;
        int startIndex = Math.max(0, this.scrollBar.getValue());
        int max = Math.min(startIndex + this.maxVisibleEntries, list.size());
        int scrollWidth = this.scrollBar.getWidth();
        int defaultIconWidth = this.iconProvider != null ? this.iconProvider.getExpectedWidth() + 2 : 0;

        for (int i = startIndex; i < max; ++i)
        {
            int bg = (i & 0x1) != 0 ? 0x20FFFFFF : 0x30FFFFFF;
            boolean hovered = false;

            if (mouseOverListOnX && mouseY >= y && mouseY < y + height)
            {
                bg = 0x60FFFFFF;
                hovered = true;
            }

            T entry = list.get(i);
            IGuiIcon icon = this.iconProvider != null && entry != null ? this.iconProvider.getIconFor(entry) : null;
            int iconWidth = defaultIconWidth;
            int x = this.getX();
            int width = this.getWidth();

            RenderUtils.drawRect(x, y, width - scrollWidth - 1, height, bg, this.getZLevel());

            if (icon != null)
            {
                iconWidth = icon.getWidth() + 2;
                int iconOffY = (height - icon.getHeight()) / 2;
                icon.renderAt(x + 4, y + iconOffY, this.getZLevel(), true, hovered);
            }

            int txtX = x + iconWidth + 6;
            this.drawString(txtX, txtY, this.textColor, this.getDisplayString(entry));

            y += height;
            txtY += height;
        }
    }

    public static class WidgetSelectionBar<T> extends WidgetClickable
    {
        protected final WidgetLabel widgetLabel;
        protected WidgetIcon widgetOpenCloseIcon;
        protected WidgetIcon widgetEntryIcon;

        public WidgetSelectionBar(int x, int y, int width, int height, int textColor, WidgetDropDownList<T> dropdown)
        {
            super(x, y, width, height, dropdown::toggleOpen);

            this.widgetLabel = new WidgetLabel(0, 0, width - 1, height, textColor, dropdown.getCurrentEntryDisplayString());
            this.widgetLabel.setZLevel(this.getZLevel() + 1);
            this.widgetLabel.setTextOffsetXY(5).setUseTextShadow(false);
            this.widgetLabel.setBackgroundProperties(1, 0xB0101010, 0xFFC0C0C0, 0xFFC0C0C0);

            this.update(dropdown);
        }

        public void update(WidgetDropDownList<T> dropdown)
        {
            this.setWidth(dropdown.getWidth());

            IGuiIcon icon = dropdown.isOpen() ? GuiIconBase.ARROW_UP : GuiIconBase.ARROW_DOWN;
            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();

            this.widgetOpenCloseIcon = new WidgetIcon(x + width - icon.getWidth() - 2, y + (height - icon.getHeight()) / 2 + 1, icon);
            this.widgetOpenCloseIcon.setEnabled(true).setDoHilight(true).setZLevel(this.widgetLabel.getZLevel() + 2);
            this.widgetLabel.setPosition(x, y);
            this.widgetLabel.setWidth(this.getWidth() - 1);
            this.widgetLabel.setText(dropdown.getCurrentEntryDisplayString());

            this.clearWidgets();
            this.addWidget(this.widgetLabel);
            this.addWidget(this.widgetOpenCloseIcon);

            T entry = dropdown.getSelectedEntry();
            icon = dropdown.iconProvider != null && entry != null ? dropdown.iconProvider.getIconFor(entry) : null;

            if (icon != null)
            {
                this.widgetLabel.setTextOffsetX(icon.getWidth() + 7);
                this.addWidget(new WidgetIcon(x + 4, y + (height - icon.getHeight()) / 2 + 1, icon));
            }
        }
    }

    protected static class TextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        protected final WidgetDropDownList<?> widget;

        protected TextFieldListener(WidgetDropDownList<?> widget)
        {
            this.widget = widget;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            this.widget.updateFilteredEntries();
            return true;
        }
    }
}
