package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.IconProvider;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

/**
 * A dropdown selection widget for entries in the given list.
 * If the stringFactory has been set, then that is used to convert the entries to the
 * display strings, otherwise the normal {@link Object#toString()} method is used.
 * @author masa
 *
 * @param <T>
 */
public class DropDownListWidget<T> extends ContainerWidget
{
    protected final List<T> entries;
    protected final List<T> filteredEntries;
    protected final BaseTextFieldWidget searchField;
    protected final ScrollBarWidget scrollBar;
    protected final SelectionBarWidget<T> widgetSelectionBar;
    @Nullable protected final Function<T, String> stringFactory;

    protected final int maxVisibleEntries;
    protected final int maxHeight;
    protected final int lineHeight;

    @Nullable protected GenericButton buttonOpenClose;
    @Nullable protected IconProvider<T> iconProvider;
    @Nullable protected SelectionListener<T> selectionListener;
    @Nullable protected T selectedEntry;
    protected LeftRight openIconSide = LeftRight.RIGHT;
    protected boolean isOpen;
    protected boolean searchOpen;
    protected boolean noCurrentEntryBar;
    protected int dropdownHeight;
    protected int dropdownTopY;
    protected int selectedIndex;
    protected int textColor = 0xFFE0E0E0;
    protected int totalHeight;
    protected int visibleEntries;

    public DropDownListWidget(int x, int y, int width, int height, int maxHeight,
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
     * @param stringFactory
     */
    public DropDownListWidget(int x, int y, int width, int height, int maxHeight,
                              int maxVisibleEntries, List<T> entries, @Nullable Function<T, String> stringFactory)
    {
        super(x, y, width, height);

        this.lineHeight = height;
        this.maxHeight = maxHeight;
        this.entries = entries;
        this.filteredEntries = new ArrayList<>();
        this.stringFactory = stringFactory;

        int v = Math.min(maxVisibleEntries, entries.size());
        v = Math.min(v, maxHeight / height);
        v = Math.min(v, (GuiUtils.getScaledWindowHeight() - y) / height);
        v = Math.max(v, 1);

        this.maxVisibleEntries = v;
        this.dropdownHeight = v * this.lineHeight + 2;
        this.dropdownTopY = y + this.lineHeight;
        this.totalHeight = this.dropdownHeight + this.lineHeight;

        int scrollbarWidth = 8;
        int scrollbarHeight = this.maxVisibleEntries * this.lineHeight;

        // The position gets updated in updateSubWidgetsToGeometryChanges
        this.scrollBar = new ScrollBarWidget(0, 0, scrollbarWidth, scrollbarHeight);
        this.scrollBar.setMaxValue(this.entries.size() - this.maxVisibleEntries);
        this.scrollBar.setArrowTextures(BaseIcon.SMALL_ARROW_UP, BaseIcon.SMALL_ARROW_DOWN);

        this.updateWidth(false);

        this.widgetSelectionBar = new SelectionBarWidget<>(x, y, this.getWidth(), height, this.textColor, this);
        this.widgetSelectionBar.setZLevel(this.getZLevel() + 2);

        this.searchField = new BaseTextFieldWidget(x, y - 16, this.getWidth(), 16);
        this.searchField.setUpdateListenerAlways(true);
        this.searchField.setListener((newText) -> this.updateFilteredEntries());
        this.searchField.setFocused(true);

        this.updateFilteredEntries(); // This must be called after the search text field has been created
    }

    @Override
    protected int getSubWidgetZLevelIncrement()
    {
        // Raise the z-level so it's likely to be on top of all other widgets in the same GUI
        return 80;
    }

    public void setIconProvider(@Nullable IconProvider<T> iconProvider)
    {
        this.iconProvider = iconProvider;
        //this.updateWidth(true);
    }

    public void setSelectionListener(@Nullable SelectionListener<T> selectionListener)
    {
        this.selectionListener = selectionListener;
    }

    public void setNoBarWhenClosed(int buttonX, int buttonY, Supplier<Icon> iconSupplier)
    {
        this.noCurrentEntryBar = true;
        this.buttonOpenClose = GenericButton.createIconOnly(buttonX, buttonY, iconSupplier);
        this.buttonOpenClose.setRenderOutline(false);
        this.buttonOpenClose.setActionListener((btn, mbtn) -> this.toggleOpen());
        //this.reAddSubWidgets();
    }

    protected void updateWidth(boolean updateSubWidgets)
    {
        this.setWidth(this.getRequiredWidth(-1, this.entries));

        if (updateSubWidgets)
        {
            this.updateSubWidgetsToGeometryChanges();
        }
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.isOpen)
        {
            if (this.searchOpen)
            {
                this.addWidget(this.searchField);
            }

            if (this.scrollBar != null)
            {
                this.addWidget(this.scrollBar);
            }
        }

        if (this.noCurrentEntryBar && this.buttonOpenClose != null)
        {
            this.addWidget(this.buttonOpenClose);
        }

        if (this.noCurrentEntryBar == false && this.widgetSelectionBar != null)
        {
            this.addWidget(this.widgetSelectionBar);
        }
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int yOff = this.noCurrentEntryBar ? 0 : this.lineHeight;
        int scrollbarWidth = 8;

        this.dropdownTopY = y + yOff;
        this.totalHeight = this.dropdownHeight + yOff;

        this.scrollBar.setPosition(x + width - scrollbarWidth - 1, y + yOff + 1);

        if (this.widgetSelectionBar != null)
        {
            this.widgetSelectionBar.setPositionAndSize(x, y, width, this.lineHeight);
            this.widgetSelectionBar.update(this);
        }

        if (this.searchOpen)
        {
            this.searchField.setPositionAndSize(x, y - 16, width, 16);
        }
    }

    protected int getRequiredWidth(int width, List<T> entries)
    {
        if (width == -1)
        {
            width = 0;
            int right = this.lineHeight + 10;

            for (T entry : entries)
            {
                // + right => leave room for a square icon on the right for the open/close arrow
                width = Math.max(width, this.getStringWidth(this.getDisplayString(entry)) + right);
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

    public DropDownListWidget<T> setSelectedEntry(T entry)
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
            this.setSearchOpen(false);
        }
        // setSearchOpen() already re-adds the widgets
        else
        {
            // Add/remove the sub widgets as needed
            this.reAddSubWidgets();
        }

        if (this.noCurrentEntryBar == false)
        {
            this.updateSelectionBar();
        }
    }

    protected void setSearchOpen(boolean isOpen)
    {
        this.searchOpen = isOpen;

        if (isOpen)
        {
            this.searchField.setFocused(true);
        }
        else
        {
            this.searchField.setText("");
            this.updateFilteredEntries();
        }

        this.reAddSubWidgets();
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

        if (this.isOpen && this.searchOpen && this.searchField.isMouseOver(mouseX, mouseY))
        {
            return true;
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
            if (this.searchOpen && this.searchField.isMouseOver(mouseX, mouseY))
            {
                return this.searchField.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
            }
            else
            {
                int amount = mouseWheelDelta < 0 ? 1 : -1;
                this.scrollBar.offsetValue(amount);
                return true;
            }
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
                if (this.searchOpen && this.searchField.isFocused() && this.searchField.getText().isEmpty() == false)
                {
                    this.setSearchOpen(false);
                }
                else
                {
                    this.toggleOpen();
                }

                return true;
            }

            if (this.searchOpen == false && this.searchField.isUsableCharacter(typedChar, 0))
            {
                this.setSearchOpen(true);
            }

            return this.searchField.onKeyTyped(typedChar, keyCode);
        }

        return false;
    }

    protected void updateFilteredEntries()
    {
        this.filteredEntries.clear();
        String filterText = this.searchField.getText();

        if (this.searchOpen && filterText.isEmpty() == false)
        {
            for (T entry : this.entries)
            {
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
        this.updateScrollBarHeight();
    }

    protected void updateScrollBarHeight()
    {
        this.visibleEntries = Math.min(this.maxVisibleEntries, this.filteredEntries.size());
        int totalHeight = Math.max(this.visibleEntries, this.filteredEntries.size()) * this.lineHeight;
        this.scrollBar.setHeight(this.visibleEntries * this.lineHeight);
        this.scrollBar.setTotalHeight(totalHeight);
    }

    protected boolean entryMatchesFilter(T entry, String filterText)
    {
        return filterText.isEmpty() || this.getDisplayString(entry).toLowerCase().contains(filterText);
    }

    public String getCurrentEntryDisplayString()
    {
        return this.getDisplayString(this.getSelectedEntry());
    }

    protected String getDisplayString(T entry)
    {
        if (entry != null)
        {
            if (this.stringFactory != null)
            {
                return this.stringFactory.apply(entry);
            }

            return entry.toString();
        }

        return "-";
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.render(mouseX, mouseY, isActiveGui, hovered);

        // Render the open dropdown list
        if (this.isOpen)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, this.getZLevel() + 40);

            if (this.searchField.getText().isEmpty() == false)
            {
                this.searchField.render(mouseX, mouseY, isActiveGui, this.searchField.isHoveredForRender(mouseX, mouseY));
            }

            int x = this.getX();
            int width = this.getWidth();
            int height = this.visibleEntries * this.lineHeight;

            RenderUtils.drawOutlinedBox(x, this.dropdownTopY, width, height + 2, 0xD0000000, 0xFFE0E0E0, this.getZLevel());

            boolean mouseOverListOnX = mouseX >= x && mouseX < x + width - this.scrollBar.getWidth();
            int txtY = this.dropdownTopY + this.lineHeight / 2 - this.fontHeight / 2 + 1;
            this.renderListContents(txtY, mouseX, mouseY, mouseOverListOnX && hovered);

            this.scrollBar.render(mouseX, mouseY);

            GlStateManager.popMatrix();
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
            boolean hovered = mouseOverListOnX && mouseY >= y && mouseY < y + height;

            if (hovered)
            {
                bg = 0x60FFFFFF;
            }

            T entry = list.get(i);
            Icon icon = this.iconProvider != null && entry != null ? this.iconProvider.getIconFor(entry) : null;
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

    public static class SelectionBarWidget<T> extends ClickableWidget
    {
        protected final LabelWidget widgetLabel;
        protected final IconWidget widgetOpenCloseIcon;
        protected final IconWidget widgetEntryIcon;

        public SelectionBarWidget(int x, int y, int width, int height, int textColor, DropDownListWidget<T> dropdown)
        {
            super(x, y, width, height, dropdown::toggleOpen);

            this.setBackgroundEnabled(true);
            this.setBorderWidth(1);

            // The positions of these widgets are updated in update()
            this.widgetLabel = new LabelWidget(0, 0, textColor, dropdown.getCurrentEntryDisplayString());
            this.widgetLabel.setUseTextShadow(false);

            Icon iconOpen = dropdown.isOpen() ? BaseIcon.ARROW_UP : BaseIcon.ARROW_DOWN;
            this.widgetOpenCloseIcon = new IconWidget(0, 0, iconOpen);
            this.widgetOpenCloseIcon.setEnabled(true).setDoHighlight(true);

            this.widgetEntryIcon = new IconWidget(0, 0, BaseIcon.EMPTY);

            this.update(dropdown);
        }

        public void update(DropDownListWidget<T> dropdown)
        {
            this.clearWidgets();
            this.setWidth(dropdown.getWidth());

            T entry = dropdown.getSelectedEntry();
            Icon entryIcon = dropdown.iconProvider != null && entry != null ? dropdown.iconProvider.getIconFor(entry) : null;
            Icon iconOpen = dropdown.isOpen() ? BaseIcon.ARROW_UP : BaseIcon.ARROW_DOWN;

            this.widgetOpenCloseIcon.setIcon(iconOpen);
            this.widgetLabel.setText(dropdown.getCurrentEntryDisplayString());

            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();
            int labelX = x + 4;
            int labelY = y + (height - this.widgetLabel.getHeight()) / 2 + 1;
            int openIconX = x + width - iconOpen.getWidth() - 2;
            int openIconY = y + (height - iconOpen.getHeight()) / 2 + 1;

            this.widgetEntryIcon.setIcon(entryIcon);

            if (entryIcon != null)
            {
                labelX += this.widgetEntryIcon.getWidth() + 4;
                this.widgetEntryIcon.setPosition(x + 4, y + (height - entryIcon.getHeight()) / 2);
                this.addWidget(this.widgetEntryIcon);
            }

            this.widgetLabel.setPosition(labelX, labelY);
            this.widgetOpenCloseIcon.setPosition(openIconX, openIconY);

            this.addWidget(this.widgetLabel);
            this.addWidget(this.widgetOpenCloseIcon);
        }
    }
}
