package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.icon.MultiIconProvider;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.SelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.TextRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.StyledTextUtils;
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
    protected final List<InteractableWidget> dropDownSubWidgets = new ArrayList<>();
    protected final List<InteractableWidget> listEntryWidgets = new ArrayList<>();
    protected final ScrollBarWidget scrollBar;
    protected final BaseTextFieldWidget searchField;
    protected final SelectionBarWidget<T> selectionBarWidget;
    protected final StyledTextLine searchTipText;
    @Nullable protected final Function<T, String> stringFactory;
    @Nullable protected final IconWidgetFactory<T> iconWidgetFactory;

    protected final int maxHeight;
    protected final int maxVisibleEntries;
    protected final int lineHeight;

    @Nullable protected GenericButton openCloseButton;
    @Nullable protected MultiIconProvider<T> iconProvider;
    @Nullable protected SelectionListener<T> selectionListener;
    @Nullable protected T selectedEntry;
    protected LeftRight openIconSide = LeftRight.RIGHT;
    protected boolean isOpen;
    protected boolean searchOpen;
    protected boolean noCurrentEntryBar;
    protected int borderColorDisabled = 0xFF707070;
    protected int borderColorOpen = 0xFF40F0F0;
    protected int dropdownHeight;
    protected int dropdownTopY;
    protected int currentMaxVisibleEntries;
    protected int previousScrollPosition = -1;
    protected int textColor = 0xFFF0F0F0;
    protected int totalHeight;
    protected int visibleEntries;

    /**
     * A dropdown selection widget for entries in the given list.
     * This constructor uses the provided string retriever to get the display string for each entry.
     * @param width either fixed width (>= 0) or an automatic width (== -1) or an automatic width with a max value (< -1, where max width -width)
     * @param height the height of one entry in the list
     * @param maxHeight the maximum total height of the dropdown widget, when open
     * @param maxVisibleEntries the maximum number of visible list entries when open
     * @param entries the list of entries to show in the widget
     * @param stringFactory the function to convert the list entries to a display string
     */
    public DropDownListWidget(int width, int height, int maxHeight,
                              int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory)
    {
        this(width, height, maxHeight, maxVisibleEntries, entries, stringFactory, null);
    }

    /**
     * A dropdown selection widget for entries in the given list.
     * This constructor uses the provided string retriever to get the display string for each entry.
     * @param width either fixed width (>= 0) or an automatic width (== -1) or an automatic width with a max value (< -1, where max width -width)
     * @param height the height of one entry in the list
     * @param maxHeight the maximum total height of the dropdown widget, when open
     * @param maxVisibleEntries the maximum number of visible list entries when open
     * @param entries the list of entries to show in the widget
     * @param stringFactory the function to convert the list entries to a display string
     * @param iconWidgetFactory a factory to create the icon for each entry, if any
     */
    public DropDownListWidget(int width, int height, int maxHeight,
                              int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory,
                              @Nullable IconWidgetFactory<T> iconWidgetFactory)
    {
        super(width, height);

        this.lineHeight = height;
        this.maxHeight = maxHeight;
        this.entries = entries;
        this.stringFactory = stringFactory;
        this.iconWidgetFactory = iconWidgetFactory;
        this.searchTipText = StyledTextLine.translate("malilib.label.misc.dropdown.type_to_search");

        // Raise the z-level, so it's likely to be on top of all other widgets in the same screen
        this.zLevelIncrement = 10;

        int v = Math.min(maxVisibleEntries, entries.size());
        v = Math.min(v, maxHeight / height);
        v = Math.max(v, 1);

        this.maxVisibleEntries = v;
        this.currentMaxVisibleEntries = this.maxVisibleEntries;
        this.dropdownHeight = v * this.lineHeight + 2;
        this.totalHeight = this.dropdownHeight + this.lineHeight;

        width = this.getRequiredWidth(width, this.entries);
        int scrollbarWidth = 8;
        int scrollbarHeight = this.maxVisibleEntries * this.lineHeight;

        // The position gets updated in updateSubWidgetsToGeometryChanges
        this.scrollBar = new ScrollBarWidget(scrollbarWidth, scrollbarHeight);
        this.scrollBar.setMaxValue(this.entries.size() - this.maxVisibleEntries);
        this.scrollBar.setValueChangeListener(this::onScrolled);

        this.selectionBarWidget = new SelectionBarWidget<>(width, height, this.textColor, this);
        this.selectionBarWidget.setZ(2);
        this.selectionBarWidget.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0xFF202020);

        this.searchField = new BaseTextFieldWidget(width, 16);
        this.searchField.setUpdateListenerAlways(true);
        this.searchField.setUpdateListenerFromTextSet(true);
        this.searchField.setListener(this::onSearchTextChange);
        this.searchField.setFocused(true);
        this.searchField.setColorFocused(0xFFFFFF20);

        this.setShouldReceiveOutsideClicks(true);
        this.setWidth(width);
        this.updateFilteredEntries(""); // This must be called after the search text field has been created
        this.updateMaxSize();
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

        if (this.isOpen())
        {
            this.addWidget(this.scrollBar);

            if (this.searchOpen)
            {
                this.addWidget(this.searchField);
            }

            for (InteractableWidget widget : this.listEntryWidgets)
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
    public void updateSubWidgetPositions()
    {
        this.updateMaxSize();

        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        this.selectionBarWidget.setPosition(x, y);
        this.searchField.setPosition(x, y - 16);
    }

    public void setIconProvider(@Nullable MultiIconProvider<T> iconProvider)
    {
        this.iconProvider = iconProvider;
    }

    public void setSelectionListener(@Nullable SelectionListener<T> selectionListener)
    {
        this.selectionListener = selectionListener;
    }

    public void setNoBarWhenClosed(int buttonX, int buttonY, Supplier<MultiIcon> iconSupplier)
    {
        this.noCurrentEntryBar = true;
        this.openCloseButton = GenericButton.create(iconSupplier, this::toggleOpen);
        this.openCloseButton.setPosition(buttonX, buttonY);
        this.openCloseButton.setPlayClickSound(false);
        this.reAddSubWidgets();
    }

    @Override
    protected void onEnabledStateChanged(boolean isEnabled)
    {
        this.updateSelectionBar();
    }

    public void setTextColor(int color)
    {
        this.textColor = color;
        this.selectionBarWidget.textColor = color;
    }

    public void setBorderColorOpen(int color)
    {
        this.borderColorOpen = color;
    }

    /**
     * Sets a hover text that will only be rendered when hovering over the
     * selection bar, and the dropdown widget is open.
     */
    public void setOpenStateHoverText(@Nullable String hoverText)
    {
        this.selectionBarWidget.openStateHoverText = hoverText;
    }

    @Nullable
    public InteractableWidget createIconWidgetForEntry(int height, @Nullable T entry)
    {
        if (entry == null)
        {
            return null;
        }

        if (this.iconWidgetFactory != null)
        {
            return this.iconWidgetFactory.create(height - 2, entry);
        }
        else if (this.iconProvider != null)
        {
            MultiIcon icon = this.iconProvider.getIconFor(entry);
            return new IconWidget(icon);
        }

        return null;
    }

    protected void updateMaxSize()
    {
        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int selBarHeight = this.noCurrentEntryBar ? 0 : this.lineHeight;
        int spaceBelow = GuiUtils.getScaledWindowHeight() - (y + selBarHeight);
        int searchY = y - this.searchField.getHeight();

        // Shrink the dropdown to fit on screen, but show at least one entry
        int visible = this.filteredEntries.size();

        if (visible > 0)
        {
            int maxSpace = Math.max(spaceBelow, y) - 2;
            this.currentMaxVisibleEntries = Math.max(1, maxSpace / this.lineHeight);
            this.currentMaxVisibleEntries = Math.min(this.currentMaxVisibleEntries, this.maxVisibleEntries);
            this.currentMaxVisibleEntries = Math.min(this.currentMaxVisibleEntries, visible);
            this.dropdownHeight = this.currentMaxVisibleEntries * this.lineHeight + 2;

            this.scrollBar.setHeight(this.dropdownHeight);
            this.scrollBar.setMaxValue(visible - this.currentMaxVisibleEntries);
        }

        // Can't fit the dropdown below, but can fit it above
        if (spaceBelow < this.dropdownHeight && y >= this.dropdownHeight)
        {
            this.dropdownTopY = y - this.dropdownHeight + 1;
            searchY = y + selBarHeight;
        }
        else
        {
            this.dropdownTopY = y + selBarHeight;
        }

        this.totalHeight = this.dropdownHeight + selBarHeight;
        this.searchField.setPositionAndSize(x, searchY, width, 16);
        this.scrollBar.setPosition(x + width - this.scrollBar.getWidth() - 1, this.dropdownTopY + 1);
        this.selectionBarWidget.setPositionAndSize(x, y, width, this.lineHeight);
    }

    @Override
    public void moveSubWidgets(int diffX, int diffY)
    {
        for (InteractableWidget widget : this.dropDownSubWidgets)
        {
            widget.setPosition(widget.getX() + diffX, widget.getY() + diffY);
        }

        for (InteractableWidget widget : this.listEntryWidgets)
        {
            widget.setPosition(widget.getX() + diffX, widget.getY() + diffY);
        }
    }

    protected void reCreateListEntryWidgets()
    {
        this.listEntryWidgets.clear();

        if (this.isOpen() == false)
        {
            return;
        }

        int width = this.getWidth() - this.scrollBar.getWidth() - 2;
        int height = this.lineHeight;
        int x = this.getX() + 1; // hard coded 1 pixel border when open
        int y = this.dropdownTopY + 1;

        List<T> list = this.filteredEntries;
        int startIndex = Math.max(0, this.scrollBar.getValue());
        int max = Math.min(startIndex + this.currentMaxVisibleEntries, list.size());

        for (int i = startIndex; i < max; ++i)
        {
            DropDownListEntryWidget<T> widget = new DropDownListEntryWidget<>(width, height, i, list.get(i), this.textColor, this);
            widget.setPosition(x, y);
            this.listEntryWidgets.add(widget);
            y += height;
        }

        this.reAddSubWidgets();
    }

    protected int getRequiredWidth(int width, List<T> entries)
    {
        if (width < 0)
        {
            int maxWidth = 8192;

            if (width < -1)
            {
                maxWidth = -width;
            }

            width = 0;
            int right = this.lineHeight + 10;

            for (T entry : entries)
            {
                // + right => leave room for a square icon on the right for the open/close arrow
                width = Math.max(width, this.getStringWidth(this.getDisplayString(entry)) + right);
            }

            if (entries.size() > 0)
            {
                InteractableWidget iconWidget = this.createIconWidgetForEntry(this.lineHeight, entries.get(0));

                if (iconWidget != null)
                {
                    width += iconWidget.getWidth() + 8;
                }
            }

            width = Math.min(width, maxWidth);
        }

        return width;
    }

    @Nullable
    public T getSelectedEntry()
    {
        return this.selectedEntry;
    }

    public void onEntryClicked(@Nullable T entry)
    {
        this.setSelectedEntry(entry);
        this.setOpen(false);
    }

    public DropDownListWidget<T> setSelectedEntry(@Nullable T entry)
    {
        if (entry == null || this.entries.contains(entry))
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

    public void onScrolled()
    {
        this.previousScrollPosition = this.scrollBar.getValue();
        this.reCreateListEntryWidgets();
    }

    public boolean isOpen()
    {
        return this.isOpen;
    }

    protected void toggleOpen()
    {
        if (this.isEnabled())
        {
            this.setOpen(! this.isOpen);
        }
    }

    protected void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen && this.isEnabled();

        this.reCreateListEntryWidgets();

        if (this.isOpen == false)
        {
            this.setZ(this.getZ() - 50);
            this.setSearchOpen(false);
        }
        // setSearchOpen() already re-adds the widgets
        else
        {
            this.setZ(this.getZ() + 50);
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
            this.updateFilteredEntries("");
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
        if (this.isOpen())
        {
            return this.totalHeight;
        }

        return this.noCurrentEntryBar ? 0 : this.lineHeight;
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        if (this.noCurrentEntryBar && this.isOpen() == false)
        {
            return this.openCloseButton != null && this.openCloseButton.isMouseOver(mouseX, mouseY);
        }

        if (this.isOpen() && this.searchOpen && this.searchField.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        if (this.selectionBarWidget.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        int x = this.getX();

        return this.isOpen() && mouseX >= x && mouseX < x + this.getWidth() &&
               mouseY >= this.dropdownTopY && mouseY < this.dropdownTopY + this.dropdownHeight;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // Close the dropdown when clicking outside of it
        if (this.isMouseOver(mouseX, mouseY) == false)
        {
            if (this.isOpen())
            {
                this.setOpen(false);
                return true;
            }

            return false;
        }

        // This handles the open/close button in the no-entry-bar case, plus the entry bar clicks 
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.isOpen() && mouseY >= this.dropdownTopY)
        {
            if (this.scrollBar.onMouseClicked(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }

        return true;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollBar.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.isOpen())
        {
            if (this.searchOpen)
            {
                return this.searchField.tryMouseScroll(mouseX, mouseY, mouseWheelDelta);
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
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if (this.isOpen())
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

            return this.searchField.onKeyTyped(keyCode, scanCode, modifiers);
        }

        return false;
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.isOpen())
        {
            if (this.searchOpen == false && this.searchField.isUsableCharacter(charIn, modifiers))
            {
                this.setSearchOpen(true);
            }

            return this.searchField.onCharTyped(charIn, modifiers);
        }

        return super.onCharTyped(charIn, modifiers);
    }

    protected void onSearchTextChange(String searchText)
    {
        if (MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.getBooleanValue())
        {
            MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.setValue(false);
        }

        this.updateFilteredEntries(searchText);
    }

    protected void updateFilteredEntries(String searchText)
    {
        this.filteredEntries.clear();

        if (this.searchOpen && searchText.isEmpty() == false)
        {
            for (T entry : this.entries)
            {
                if (this.entryMatchesFilter(entry, searchText))
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

        this.scrollBar.setMaxValue(this.filteredEntries.size() - this.currentMaxVisibleEntries);
        this.updateMaxSize();
        this.updateScrollBarHeight();
        this.reCreateListEntryWidgets();
    }

    protected void updateScrollBarHeight()
    {
        this.visibleEntries = Math.min(this.currentMaxVisibleEntries, this.filteredEntries.size());
        int totalHeight = Math.max(this.visibleEntries, this.filteredEntries.size()) * this.lineHeight;
        this.scrollBar.setHeight(this.visibleEntries * this.lineHeight);
        this.scrollBar.setTotalHeight(totalHeight);
    }

    protected boolean entryMatchesFilter(@Nullable T entry, String filterText)
    {
        return filterText.isEmpty() || this.getDisplayString(entry).toLowerCase().contains(filterText);
    }

    public String getCurrentEntryDisplayString()
    {
        return this.getDisplayString(this.getSelectedEntry());
    }

    protected String getDisplayString(@Nullable T entry)
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        // Render the open dropdown list
        if (this.isOpen())
        {
            int diffY = y - this.getY();

            RenderUtils.color(1f, 1f, 1f, 1f);
            ShapeRenderUtils.renderOutlinedRectangle(x, this.dropdownTopY + diffY, z, this.getWidth(), this.dropdownHeight, 0xFF000000, this.borderColorOpen);

            if (this.searchOpen == false && MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.getBooleanValue())
            {
                int tx = this.searchField.getX();
                int ty = this.searchField.getY();
                int sw = this.searchTipText.renderWidth;
                int right = tx + sw + 10;
                int windowWidth = GuiUtils.getScaledWindowWidth();

                if (right > windowWidth)
                {
                    tx -= (right - windowWidth);
                }

                ShapeRenderUtils.renderOutlinedRectangle(tx, ty, z, sw + 10, 16, 0xFF000000, 0xFFFFFF20);
                this.renderTextLine(tx + 4, ty + 4, z + 0.1f, 0xFFFFC000, false, this.searchTipText, ctx);
            }
        }

        super.renderAt(x, y, z, ctx);
    }

    public static class SelectionBarWidget<T> extends ContainerWidget
    {
        protected final DropDownListWidget<T> dropdownWidget;
        protected final IconWidget openCloseIconWidget;
        protected StyledTextLine displayStringFull;
        protected StyledTextLine displayStringClamped;
        @Nullable protected InteractableWidget iconWidget;
        @Nullable protected String openStateHoverText;
        protected int displayStringWidth;
        protected int nonTextWidth;
        protected int textColor;

        public SelectionBarWidget(int width, int height, int textColor, DropDownListWidget<T> dropDown)
        {
            super(width, height);

            this.textColor = textColor;
            this.dropdownWidget = dropDown;
            this.iconWidget = dropDown.createIconWidgetForEntry(height, dropDown.getSelectedEntry());

            MultiIcon iconOpen = dropDown.isOpen() ? DefaultIcons.ARROW_UP : DefaultIcons.ARROW_DOWN;
            this.openCloseIconWidget = new IconWidget(iconOpen);
            this.openCloseIconWidget.setUseEnabledVariant(true).setDoHighlight(true);
            this.nonTextWidth = this.openCloseIconWidget.getWidth() + 6;

            if (this.iconWidget != null)
            {
                this.nonTextWidth += this.iconWidget.getWidth() + 4;
            }

            this.getBackgroundRenderer().getNormalSettings().setEnabled(true);
            this.getBorderRenderer().getNormalSettings().setBorderWidth(1);
            this.setClickListener(dropDown::toggleOpen);
            this.setDisplayString(dropDown.getCurrentEntryDisplayString());
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();

            this.addWidget(this.openCloseIconWidget);

            if (this.iconWidget != null)
            {
                this.addWidget(this.iconWidget);
            }
        }

        @Override
        public void updateSubWidgetPositions()
        {
            super.updateSubWidgetPositions();

            DropDownListWidget<T> dropDown = this.dropdownWidget;
            MultiIcon iconOpen = dropDown.isOpen() ? DefaultIcons.ARROW_UP : DefaultIcons.ARROW_DOWN;

            this.openCloseIconWidget.setIcon(iconOpen);
            this.setDisplayString(dropDown.getCurrentEntryDisplayString());

            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();

            if (this.iconWidget != null)
            {
                this.iconWidget.setPosition(x + 2, y + (height - this.iconWidget.getHeight()) / 2);
            }

            int openIconX = x + width - iconOpen.getWidth() - 2;
            int openIconY = y + (height - iconOpen.getHeight()) / 2 + 1;
            this.openCloseIconWidget.setPosition(openIconX, openIconY);
        }

        protected boolean shouldRenderExpandedBackground(ScreenContext ctx)
        {
            int totalWidth = this.nonTextWidth + this.displayStringWidth;
            return ctx.isActiveScreen && totalWidth > this.getWidth() &&
                   this.isMouseOver(ctx.mouseX, ctx.mouseY);
        }

        protected void setDisplayString(String text)
        {
            this.displayStringFull = StyledTextLine.of(text);
            this.displayStringWidth = this.displayStringFull.renderWidth;

            int width = this.getWidth();
            int usableWidth = width - this.openCloseIconWidget.getWidth() - 6;

            if (this.iconWidget != null)
            {
                usableWidth -= this.iconWidget.getWidth() + 4;
            }

            if (this.displayStringWidth > usableWidth)
            {
                text = StringUtils.clampTextToRenderLength(text, usableWidth, LeftRight.RIGHT, " ...");
                this.displayStringClamped = StyledTextLine.of(text);
            }
            else
            {
                this.displayStringClamped = this.displayStringFull;
            }
        }

        @Override
        protected int getBackgroundWidth(boolean hovered, ScreenContext ctx)
        {
            if (this.shouldRenderExpandedBackground(ctx))
            {
                return this.nonTextWidth + this.displayStringWidth;
            }

            return super.getBackgroundWidth(hovered, ctx);
        }

        @Override
        public void renderAt(int x, int y, float z, ScreenContext ctx)
        {
            DropDownListWidget<T> dropDown = this.dropdownWidget;
            int ddColor = dropDown.getBorderRenderer().getNormalSettings().getColor().getTop();
            int color = dropDown.isEnabled() ? (dropDown.isOpen() ? dropDown.borderColorOpen : ddColor) : dropDown.borderColorDisabled;
            this.getBorderRenderer().getNormalSettings().setColor(color);

            super.renderAt(x, y, z, ctx);

            StyledTextLine text = this.isMouseOver(ctx.mouseX, ctx.mouseY) ? this.displayStringFull : this.displayStringClamped;
            int tx = x + 4;
            int ty = y + (this.getHeight() - this.getFontHeight()) / 2;

            if (this.iconWidget != null)
            {
                int xDiff = x - this.getX();
                tx = this.iconWidget.getRight() + 4 + xDiff;
            }

            int textColor = dropDown.isEnabled() ? this.textColor : 0xFF505050;
            this.renderTextLine(tx, ty, z, textColor, false, text, ctx);
        }

        @Override
        protected void renderSubWidgets(int x, int y, float z, ScreenContext ctx)
        {
            int diffX = x - this.getX();
            int diffY = y - this.getY();
            float diffZ = z - this.getZ();

            for (InteractableWidget widget : this.subWidgets)
            {
                int wx;
                int wy;
                float wz = widget.getZ() + diffZ;

                if (widget != this.openCloseIconWidget || this.shouldRenderExpandedBackground(ctx) == false)
                {
                    wx = widget.getX() + diffX;
                    wy = widget.getY() + diffY;
                }
                else
                {
                    wx = x + this.nonTextWidth + this.displayStringWidth - 17;
                    wy = y + (this.getHeight() - this.openCloseIconWidget.getHeight()) / 2 + 1;
                }

                widget.renderAt(wx, wy, wz, ctx);
            }
        }

        @Override
        public void postRenderHovered(ScreenContext ctx)
        {
            super.postRenderHovered(ctx);

            this.dropdownWidget.postRenderHovered(ctx); // FIXME this used the dropdown ID

            if (this.dropdownWidget.isOpen() && this.openStateHoverText != null)
            {
                TextRenderUtils.renderHoverText(ctx.mouseX + 4, ctx.mouseY + 4, this.getZ() + 50, this.openStateHoverText);
            }
        }

        public void update()
        {
            DropDownListWidget<T> dropDown = this.dropdownWidget;
            this.iconWidget = dropDown.createIconWidgetForEntry(this.getHeight(), dropDown.getSelectedEntry());

            this.updateSubWidgetPositions();
            this.reAddSubWidgets();
        }
    }

    public static class DropDownListEntryWidget<T> extends ContainerWidget
    {
        @Nullable protected final T entry;
        protected final int listIndex;
        protected final DropDownListWidget<T> dropDown;
        @Nullable protected InteractableWidget iconWidget;
        protected final int totalWidth;
        protected StyledTextLine displayStringFull;
        protected StyledTextLine displayStringClamped;
        protected int displayStringWidth;
        protected int textColor;

        public DropDownListEntryWidget(int width, int height, int listIndex, @Nullable T entry, int textColor, DropDownListWidget<T> dropDown)
        {
            super(width, height);

            this.listIndex = listIndex;
            this.entry = entry;
            this.dropDown = dropDown;
            this.textColor = textColor;
            this.setClickListener(this::onClicked);

            int normalBgColor = (listIndex & 0x1) != 0 ? 0xFF202020 : 0xFF404040;
            this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, normalBgColor);
            this.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0xFF606060);

            int iconWidth = 0;

            this.iconWidget = dropDown.createIconWidgetForEntry(height, entry);

            if (this.iconWidget != null)
            {
                this.iconWidget.setClickListener(this::onClicked);
                iconWidth = this.iconWidget.getWidth() + 4;
            }

            this.setDisplayString(dropDown.getDisplayString(entry));
            this.totalWidth = this.displayStringWidth + 6 + iconWidth;
        }

        protected void setDisplayString(String text)
        {
            int width = this.getWidth();
            int usableWidth = (this.iconWidget != null ? width - this.iconWidget.getWidth() - 4 : width) - 10;
            this.displayStringFull = StyledTextLine.of(text);

            if (this.displayStringFull.renderWidth > usableWidth)
            {
                this.displayStringClamped = StyledTextUtils.clampStyledTextToMaxWidth(this.displayStringFull, usableWidth, LeftRight.RIGHT, " ...");
            }
            else
            {
                this.displayStringClamped = this.displayStringFull;
            }

            this.displayStringWidth = this.getStringWidth(text);
        }

        protected void onClicked()
        {
            this.dropDown.onEntryClicked(this.entry);
        }

        @Override
        protected int getBackgroundWidth(boolean hovered, ScreenContext ctx)
        {
            if (ctx.isActiveScreen && this.totalWidth > this.getWidth() &&
                this.isMouseOver(ctx.mouseX, ctx.mouseY))
            {
                return this.totalWidth;
            }

            return super.getBackgroundWidth(hovered, ctx);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();

            if (this.iconWidget != null)
            {
                this.addWidget(this.iconWidget);
            }
        }

        @Override
        public void updateSubWidgetPositions()
        {
            super.updateSubWidgetPositions();

            if (this.iconWidget != null)
            {
                int offY = (this.getHeight() - this.iconWidget.getHeight()) / 2;
                this.iconWidget.setPosition(this.getX() + 1, this.getY() + offY);
            }
        }

        @Override
        public void renderAt(int x, int y, float z, ScreenContext ctx)
        {
            super.renderAt(x, y, z, ctx);

            StyledTextLine text = this.isMouseOver(ctx.mouseX, ctx.mouseY) ? this.displayStringFull : this.displayStringClamped;
            int tx = this.iconWidget != null ? this.iconWidget.getRight() + 4 : x + 4;
            int ty = y + (this.getHeight() - this.getFontHeight()) / 2;

            this.renderTextLine(tx, ty, z, this.textColor, true, text, ctx);
        }
    }

    public interface IconWidgetFactory<T>
    {
        InteractableWidget create(int height, T data);
    }
}
