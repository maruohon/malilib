package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.IconProvider;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
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
    protected final List<T> filteredEntries = new ArrayList<>();
    protected final List<T> entries;
    protected final List<BaseWidget> dropDownSubWidgets = new ArrayList<>();
    protected final List<BaseWidget> listEntryWidgets = new ArrayList<>();
    protected final ScrollBarWidget scrollBar;
    protected final BaseTextFieldWidget searchField;
    protected final SelectionBarWidget<T> selectionBarWidget;
    @Nullable protected final Function<T, String> stringFactory;
    @Nullable protected final IconWidgetFactory<T> iconWidgetFactory;

    protected final int maxVisibleEntries;
    protected final int maxHeight;
    protected final int lineHeight;

    @Nullable protected GenericButton openCloseButton;
    @Nullable protected IconProvider<T> iconProvider;
    @Nullable protected SelectionListener<T> selectionListener;
    @Nullable protected T selectedEntry;
    protected LeftRight openIconSide = LeftRight.RIGHT;
    protected boolean isOpen;
    protected boolean searchOpen;
    protected boolean noCurrentEntryBar;
    protected int dropdownHeight;
    protected int dropdownTopY;
    protected int textColor = 0xFFE0E0E0;
    protected int previousScrollPosition = -1;
    protected int totalHeight;
    protected int visibleEntries;

    public DropDownListWidget(int x, int y, int width, int height, int maxHeight,
                              int maxVisibleEntries, List<T> entries)
    {
        this(x, y, width, height, maxHeight, maxVisibleEntries, entries, null, null);
    }

    /**
     * A dropdown selection widget for entries in the given list.
     * This constructor uses the provided string retriever to get the display string for each entry.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param maxHeight the maximum total height of the dropdown widget, when open
     * @param maxVisibleEntries the maximum number of visible list entries when open
     * @param entries
     * @param stringFactory
     * @param iconWidgetFactory
     */
    public DropDownListWidget(int x, int y, int width, int height, int maxHeight,
                              int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory,
                              @Nullable IconWidgetFactory<T> iconWidgetFactory)
    {
        super(x, y, width, height);

        this.lineHeight = height;
        this.maxHeight = maxHeight;
        this.entries = entries;
        this.stringFactory = stringFactory;
        this.iconWidgetFactory = iconWidgetFactory;

        int v = Math.min(maxVisibleEntries, entries.size());
        v = Math.min(v, maxHeight / height);
        v = Math.min(v, (GuiUtils.getScaledWindowHeight() - y) / height);
        v = Math.max(v, 1);

        this.maxVisibleEntries = v;
        this.dropdownHeight = v * this.lineHeight + 2;
        this.dropdownTopY = y + this.lineHeight;
        this.totalHeight = this.dropdownHeight + this.lineHeight;

        width = this.getRequiredWidth(-1, this.entries);
        int scrollbarWidth = 8;
        int scrollbarHeight = this.maxVisibleEntries * this.lineHeight;

        // The position gets updated in updateSubWidgetsToGeometryChanges
        this.scrollBar = new ScrollBarWidget(0, 0, scrollbarWidth, scrollbarHeight);
        this.scrollBar.setMaxValue(this.entries.size() - this.maxVisibleEntries);
        this.scrollBar.setArrowTextures(BaseIcon.SMALL_ARROW_UP, BaseIcon.SMALL_ARROW_DOWN);
        this.scrollBar.setValueChangeListener(this::reCreateListEntryWidgets);

        this.selectionBarWidget = new SelectionBarWidget<>(x, y, width, height, this.textColor, this);
        this.selectionBarWidget.setZLevel(2);

        this.searchField = new BaseTextFieldWidget(x, y - 16, width, 16);
        this.searchField.setUpdateListenerAlways(true);
        this.searchField.setUpdateListenerFromTextSet(true);
        this.searchField.setListener((newText) -> this.updateFilteredEntries());
        this.searchField.setFocused(true);

        this.setWidth(width);
        this.updateFilteredEntries(); // This must be called after the search text field has been created
    }

    @Override
    protected int getSubWidgetZLevelIncrement()
    {
        // Raise the z-level so it's likely to be on top of all other widgets in the same GUI
        return 10;
    }

    public void setIconProvider(@Nullable IconProvider<T> iconProvider)
    {
        this.iconProvider = iconProvider;
    }

    public void setSelectionListener(@Nullable SelectionListener<T> selectionListener)
    {
        this.selectionListener = selectionListener;
    }

    public void setNoBarWhenClosed(int buttonX, int buttonY, Supplier<Icon> iconSupplier)
    {
        this.noCurrentEntryBar = true;
        this.openCloseButton = GenericButton.createIconOnly(buttonX, buttonY, iconSupplier);
        this.openCloseButton.setRenderOutline(false);
        this.openCloseButton.setActionListener((btn, mbtn) -> this.toggleOpen());
        this.reAddSubWidgets();
    }

    @Nullable
    public BaseWidget createIconWidgetForEntry(int x, int y, int height, T entry)
    {
        if (this.iconWidgetFactory != null)
        {
            BackgroundWidget widget = this.iconWidgetFactory.create(x, y + 1, height - 2, entry);
            return widget;
        }
        else if (this.iconProvider != null)
        {
            Icon icon = this.iconProvider.getIconFor(entry);
            int offY = (height - icon.getHeight()) / 2;
            return new IconWidget(x, y + offY, icon);
        }

        return null;
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.dropDownSubWidgets.clear();
        this.dropDownSubWidgets.add(this.scrollBar);
        this.dropDownSubWidgets.add(this.selectionBarWidget);
        this.dropDownSubWidgets.add(this.searchField);

        if (this.openCloseButton != null)
        {
            this.dropDownSubWidgets.add(this.openCloseButton);
        }

        if (this.isOpen)
        {
            this.addWidget(this.scrollBar);

            if (this.searchOpen)
            {
                this.addWidget(this.searchField);
            }

            for (BaseWidget widget : this.listEntryWidgets)
            {
                this.addWidget(widget);
            }
        }

        if (this.noCurrentEntryBar && this.openCloseButton != null)
        {
            this.addWidget(this.openCloseButton);
        }

        if (this.noCurrentEntryBar == false && this.selectionBarWidget != null)
        {
            this.addWidget(this.selectionBarWidget);
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

        this.scrollBar.setPosition(x + width - scrollbarWidth - 1, y + yOff);
        this.searchField.setPositionAndSize(x, y - 16, width, 16);
        this.selectionBarWidget.setPositionAndSize(x, y, width, this.lineHeight);
    }

    @Override
    public void moveSubWidgets(int diffX, int diffY)
    {
        for (BaseWidget widget : this.dropDownSubWidgets)
        {
            widget.setPosition(widget.getX() + diffX, widget.getY() + diffY);
        }

        for (BaseWidget widget : this.listEntryWidgets)
        {
            widget.setPosition(widget.getX() + diffX, widget.getY() + diffY);
        }
    }

    protected void reCreateListEntryWidgets()
    {
        this.listEntryWidgets.clear();

        if (this.isOpen == false)
        {
            return;
        }

        int bw = this.borderEnabled ? this.borderWidth * 2 : 0;
        int width = this.getWidth() - this.scrollBar.getWidth() - bw;
        int height = this.lineHeight;
        int x = this.getX();
        int y = this.getY() + height;

        List<T> list = this.filteredEntries;
        int startIndex = Math.max(0, this.scrollBar.getValue());
        int max = Math.min(startIndex + this.maxVisibleEntries, list.size());

        for (int i = startIndex; i < max; ++i)
        {
            T entry = list.get(i);
            DropDownListEntryWidget<T> widget = new DropDownListEntryWidget<>(x, y, width, height, i, entry, this);
            this.listEntryWidgets.add(widget);
            y += height;
        }

        this.reAddSubWidgets();
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

    public void onEntryClicked(T entry)
    {
        this.setSelectedEntry(entry);
        this.setOpen(false);
    }

    public DropDownListWidget<T> setSelectedEntry(T entry)
    {
        if (this.entries.contains(entry))
        {
            this.selectedEntry = entry;
            this.updateSelectionBar();

            if (this.selectionListener != null)
            {
                this.selectionListener.onSelectionChange(this.selectedEntry);
            }
        }

        return this;
    }

    protected boolean setSelectedEntry(int index)
    {
        if (index >= 0 && index < this.filteredEntries.size())
        {
            this.setSelectedEntry(this.filteredEntries.get(index));
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
        this.setOpen(! this.isOpen);
    }

    protected void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen;

        this.reCreateListEntryWidgets();

        if (this.isOpen == false)
        {
            this.setZLevel(this.getZLevel() - 50);
            this.setSearchOpen(false);
        }
        // setSearchOpen() already re-adds the widgets
        else
        {
            this.setZLevel(this.getZLevel() + 50);
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
        if (this.selectionBarWidget != null)
        {
            this.selectionBarWidget.update();
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
            return this.openCloseButton != null && this.openCloseButton.isMouseOver(mouseX, mouseY);
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
                this.setOpen(false);
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
                    this.setOpen(false);
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
                if (this.searchOpen && this.searchField.isFocused())
                {
                    this.setSearchOpen(false);
                }
                else
                {
                    this.setOpen(false);
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
        this.reCreateListEntryWidgets();
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
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        // Render the open dropdown list
        if (this.isOpen)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);

            int x = this.getX();
            int width = this.getWidth();
            int height = this.visibleEntries * this.lineHeight;

            RenderUtils.drawOutlinedBox(x, this.dropdownTopY, width, height + 2, 0xD0000000, 0xFFE0E0E0, this.getZLevel());
        }

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }

    public static class SelectionBarWidget<T> extends ClickableWidget
    {
        protected final DropDownListWidget<T> dropdownWidget;
        protected final LabelWidget labelWidget;
        protected final IconWidget openCloseIconWidget;
        @Nullable protected BaseWidget iconWidget;

        public SelectionBarWidget(int x, int y, int width, int height, int textColor, DropDownListWidget<T> dropDown)
        {
            super(x, y, width, height, dropDown::toggleOpen);

            this.dropdownWidget = dropDown;

            // The positions of these widgets are updated in update()
            this.labelWidget = new LabelWidget(0, 0, textColor, dropDown.getCurrentEntryDisplayString());
            this.labelWidget.setUseTextShadow(false);

            Icon iconOpen = dropDown.isOpen() ? BaseIcon.ARROW_UP : BaseIcon.ARROW_DOWN;
            this.openCloseIconWidget = new IconWidget(0, 0, iconOpen);
            this.openCloseIconWidget.setEnabled(true).setDoHighlight(true);

            this.setBackgroundEnabled(true);
            this.setBorderWidth(1);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();

            this.addWidget(this.labelWidget);
            this.addWidget(this.openCloseIconWidget);

            if (this.iconWidget != null)
            {
                this.addWidget(this.iconWidget);
            }
        }

        @Override
        public void updateSubWidgetsToGeometryChanges()
        {
            super.updateSubWidgetsToGeometryChanges();

            DropDownListWidget<T> dropDown = this.dropdownWidget;
            Icon iconOpen = dropDown.isOpen() ? BaseIcon.ARROW_UP : BaseIcon.ARROW_DOWN;

            this.openCloseIconWidget.setIcon(iconOpen);
            this.labelWidget.setText(dropDown.getCurrentEntryDisplayString());

            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();
            int nextX = x + 4;
            int labelY = y + (height - this.labelWidget.getHeight()) / 2 + 1;

            this.iconWidget = dropDown.createIconWidgetForEntry(nextX, y, height, dropDown.getSelectedEntry());

            if (this.iconWidget != null)
            {
                nextX = this.iconWidget.getRight() + 4;
            }

            this.labelWidget.setPosition(nextX, labelY);

            int openIconX = x + width - iconOpen.getWidth() - 2;
            int openIconY = y + (height - iconOpen.getHeight()) / 2 + 1;

            this.openCloseIconWidget.setPosition(openIconX, openIconY);
        }

        public void update()
        {
            this.updateSubWidgetsToGeometryChanges();
            this.reAddSubWidgets();
        }
    }

    public static class DropDownListEntryWidget<T> extends ClickableWidget
    {
        protected final T entry;
        protected final int listIndex;
        protected LabelWidget labelWidget;
        @Nullable protected BaseWidget iconWidget;

        public DropDownListEntryWidget(int x, int y, int width, int height, int listIndex, T entry, DropDownListWidget<T> dropDown)
        {
            super(x, y, width, height, () -> dropDown.onEntryClicked(entry));

            this.listIndex = listIndex;
            this.entry = entry;

            this.backgroundColor = (listIndex & 0x1) != 0 ? 0x20FFFFFF : 0x30FFFFFF;
            this.backgroundColorHovered = 0x60FFFFFF;
            this.backgroundEnabled = true;

            int nextX = x + 4;

            this.iconWidget = dropDown.createIconWidgetForEntry(nextX, y, height, entry);

            if (this.iconWidget != null)
            {
                nextX = this.iconWidget.getRight() + 4;
            }

            this.labelWidget = new LabelWidget(nextX, y, -1, height, 0xFFFFFFFF, dropDown.getDisplayString(entry));
            this.labelWidget.setPaddingY((height - this.fontHeight) / 2 + 1);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();

            this.addWidget(this.labelWidget);

            if (this.iconWidget != null)
            {
                this.addWidget(this.iconWidget);
            }
        }

        @Override
        public void updateSubWidgetsToGeometryChanges()
        {
            super.updateSubWidgetsToGeometryChanges();

            int nextX = this.getX() + 4;
            int y = this.getY();
            int height = this.getHeight();

            if (this.iconWidget != null)
            {
                int offY = (height - this.iconWidget.getHeight()) / 2;
                this.iconWidget.setPosition(nextX, y + offY);
                nextX = this.iconWidget.getRight() + 4;
            }

            this.labelWidget.setPosition(nextX, y);
        }
    }

    public interface IconWidgetFactory<T>
    {
        BackgroundWidget create(int x, int y, int height, T data);
    }
}
