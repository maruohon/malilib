package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ElementOffset;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.SelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.SingleTextLineRenderer;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ToBooleanFunction;

public class DropDownListWidget<T> extends ContainerWidget
{
    protected final List<T> filteredEntries = new ArrayList<>();
    protected final EntryWidgetFactory<T> entryWidgetFactory;
    @Nullable protected final Function<T, String> stringFactory;
    protected final ScrollBarWidget scrollBar;
    protected final BaseTextFieldWidget searchTextField;
    protected final GenericButton openCloseButton;
    protected final StyledTextLine searchTipText;
    protected final int lineHeight;
    protected final int maxVisibleEntries;

    protected ImmutableList<T> entries;
    protected SelectionHandler<T> selectionHandler;
    protected String multiSelectionTranslationKey = "malilib.label.misc.dropdown.multiple_entries_selected";
    @Nullable protected SelectionListener<T> selectionListener;
    @Nullable protected InteractableWidget currentEntryBarWidget;
    protected boolean closeOnSelect = true;
    protected boolean isOpen;
    protected boolean searchOpen;
    protected boolean useCurrentEntryBar = true;
    protected float baseZLevel;
    protected int borderColorOpen = 0xFF40F0F0;
    protected int currentMaxVisibleEntries;
    protected int dropdownHeight;
    protected int textColor = 0xFFF0F0F0;
    protected int totalHeight;
    protected int visibleEntries;

    public DropDownListWidget(int height, int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory)
    {
        this(height, maxVisibleEntries, entries, stringFactory, (IconWidgetFactory<T>) null);
    }

    public DropDownListWidget(int height, int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory,
                              @Nullable IconWidgetFactory<T> iconWidgetFactory)
    {
        this(height, maxVisibleEntries, entries, stringFactory,
             new DefaultEntryWidgetFactory<>(stringFactory, iconWidgetFactory));
    }

    public DropDownListWidget(int height, int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory,
                              EntryWidgetFactory<T> entryWidgetFactory)
    {
        super(-1, height);

        this.entries = ImmutableList.copyOf(entries);
        this.lineHeight = height;
        this.stringFactory = stringFactory;
        this.entryWidgetFactory = entryWidgetFactory;
        this.maxVisibleEntries = maxVisibleEntries;
        this.selectionHandler = new DefaultSingleEntrySelectionHandler<>();

        // The position gets updated in updateSubWidgetsToGeometryChanges
        this.scrollBar = new ScrollBarWidget(8, 100);
        this.scrollBar.setValueChangeListener(this::onScrolled);
        this.scrollBar.setBackgroundColor(0xFF202020);

        this.searchTextField = new BaseTextFieldWidget(100, 16); // The width will get updated later
        this.searchTextField.setUpdateListenerAlways(true);
        this.searchTextField.setUpdateListenerFromTextSet(true);
        this.searchTextField.setListener(this::onSearchTextChange);
        this.searchTextField.setFocused(true);
        this.searchTextField.setColorFocused(0xFFFFFF20);

        this.openCloseButton = GenericButton.create((Supplier<MultiIcon>) null, this::toggleOpen);
        this.openCloseButton.setPlayClickSound(false);

        // Raise the z-level, so it's likely to be on top of all other widgets in the same screen
        this.zLevelIncrement = 20;
        this.shouldReceiveOutsideClicks = true;
        this.searchTipText = StyledTextLine.translate("malilib.label.misc.dropdown.type_to_search");

        this.setHoverInfoRequiresShift(true);
        this.setWidthNoUpdate(120); // The width will get updated later
        this.updateCurrentEntryBar();
        this.updateDropDownHeight();
        this.updateFilteredEntries("");
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidgetIfNotNull(this.currentEntryBarWidget);

        if (this.useCurrentEntryBar == false)
        {
            this.addWidget(this.openCloseButton);
        }

        if (this.isOpen())
        {
            this.addWidget(this.scrollBar);

            if (this.searchOpen)
            {
                this.addWidget(this.searchTextField);
            }

            this.createEntryWidgets();
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getRight() - this.scrollBar.getWidth() - 1;
        int y = this.currentEntryBarWidget.getBottom() + 1;
        this.scrollBar.setPosition(x, y);
        this.searchTextField.setBottom(this.getY());
    }

    @Override
    public void moveSubWidgets(int diffX, int diffY)
    {
        super.moveSubWidgets(diffX, diffY);

        // If the dropdown is not open and thus the widgets are not in the widget list,
        // then the super call will not move these
        if (this.isOpen() == false)
        {
            this.scrollBar.moveBy(diffX, diffY);
            this.searchTextField.moveBy(diffX, diffY);
        }
    }

    @Override
    public int getHeight()
    {
        if (this.isOpen())
        {
            return this.totalHeight;
        }

        return this.useCurrentEntryBar ? this.lineHeight : 0;
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = this.getRequiredWidth(this.entries, this.entryWidgetFactory,
                                              this.getSelectionHandler().supportsMultiSelection()) + 20;

            if (this.hasMaxWidth())
            {
                width = Math.min(width, this.maxWidth);
            }

            this.setWidth(width);
        }
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();
        this.updateCurrentEntryBar();
        this.searchTextField.setWidth(this.getWidth());
    }

    @Override
    public void setZLevelBasedOnParent(float parentZLevel)
    {
        super.setZLevelBasedOnParent(parentZLevel);

        this.baseZLevel = this.getZ();
    }

    protected int getDropDownY()
    {
        if (this.useCurrentEntryBar)
        {
            return this.getY() + this.lineHeight;
        }

        return this.getY();
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        if (this.useCurrentEntryBar == false && this.isOpen() == false)
        {
            return this.openCloseButton.isMouseOver(mouseX, mouseY);
        }

        if (this.isOpen() && this.searchOpen && this.searchTextField.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        if (this.currentEntryBarWidget != null && this.currentEntryBarWidget.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        int ddY = this.getDropDownY();

        return this.isOpen() &&
               mouseX >= this.getX() && mouseX < this.getRight() &&
               mouseY >= ddY && mouseY < ddY + this.dropdownHeight;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // Close the dropdown when clicking outside the widget
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

        if (this.isOpen() && mouseY >= this.getDropDownY())
        {
            this.scrollBar.onMouseClicked(mouseX, mouseY, mouseButton);
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
            if (this.searchOpen && this.searchTextField.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
            {
                return true;
            }

            int amount = mouseWheelDelta < 0 ? 1 : -1;
            this.scrollBar.offsetValue(amount);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.isOpen())
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                if (this.searchOpen && this.searchTextField.isFocused())
                {
                    this.setSearchOpen(false);
                }
                else
                {
                    this.setOpen(false);
                }

                return true;
            }

            return this.searchTextField.onKeyTyped(keyCode, scanCode, modifiers);
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.isOpen())
        {
            if (this.searchOpen == false && this.searchTextField.isUsableCharacter(charIn, modifiers))
            {
                this.setSearchOpen(true);
            }

            return this.searchTextField.onCharTyped(charIn, modifiers);
        }

        return super.onCharTyped(charIn, modifiers);
    }

    public void setNoEntryBar(int buttonX, int buttonY, Supplier<MultiIcon> iconSupplier)
    {
        this.useCurrentEntryBar = false;
        this.openCloseButton.setButtonIconSupplier(iconSupplier);
        this.openCloseButton.setPosition(buttonX, buttonY);
    }

    public void setCloseOnSelect(boolean closeOnSelect)
    {
        this.closeOnSelect = closeOnSelect;
    }

    public void setSelectionListener(@Nullable SelectionListener<T> selectionListener)
    {
        this.selectionListener = selectionListener;
    }

    public SelectionHandler<T> getSelectionHandler()
    {
        return this.selectionHandler;
    }

    /**
     * Sets a custom selection handler. This can be used to support multi-selection.
     * The {@link #setCloseOnSelect(boolean)} method is probably also relevant for multi-selections.
     */
    public void setSelectionHandler(SelectionHandler<T> selectionHandler)
    {
        this.selectionHandler = selectionHandler;
        this.updateCurrentEntryBar();
    }

    /**
     * Sets the translation key used for the current selection bar, when there is more than one entry selected
     */
    public void setMultiSelectionTranslationKey(String multiSelectionTranslationKey)
    {
        this.multiSelectionTranslationKey = multiSelectionTranslationKey;
        this.updateWidth();
        this.updateCurrentEntryBar();
    }

    public void replaceEntryList(List<T> newEntries)
    {
        this.entries = ImmutableList.copyOf(newEntries);

        this.updateDropDownHeight();
        this.updateWidth();
        this.updateCurrentEntryBar();
        this.updateFilteredEntries("");
    }

    protected void updateDropDownHeight()
    {
        this.currentMaxVisibleEntries = Math.max(1, Math.min(this.maxVisibleEntries, this.entries.size()));
        this.dropdownHeight = this.currentMaxVisibleEntries * this.lineHeight + 2;
        this.totalHeight = this.dropdownHeight + this.lineHeight;
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

        this.clearWidgets();
        this.setZ(this.isOpen ? this.baseZLevel + 50 : this.baseZLevel);
        this.updateCurrentEntryBar();
        this.updateSubWidgetPositions();

        if (this.isOpen)
        {
            this.updateFilteredEntries("");
        }
        else
        {
            this.searchOpen = false;
            this.searchTextField.setTextNoNotify("");
        }

        // Add/remove the sub widgets as needed
        this.reAddSubWidgets();
    }

    protected void setSearchOpen(boolean isOpen)
    {
        this.searchOpen = isOpen;

        if (isOpen)
        {
            this.searchTextField.setFocused(true);
            this.reAddSubWidgets();
        }
        else
        {
            this.searchTextField.setText("");
        }
    }

    protected void onSearchTextChange(String searchText)
    {
        if (MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.getBooleanValue())
        {
            MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.setValue(false);
        }

        this.updateFilteredEntries(searchText);
        this.reAddSubWidgets();
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
        //this.updateMaxSize();
        this.updateScrollBarHeight();
    }

    protected void updateScrollBarHeight()
    {
        this.visibleEntries = Math.min(this.currentMaxVisibleEntries, this.filteredEntries.size());
        int totalHeight = Math.max(this.visibleEntries, this.filteredEntries.size()) * this.lineHeight;
        this.scrollBar.setHeight(this.visibleEntries * this.lineHeight);
        this.scrollBar.setTotalHeight(totalHeight);
    }

    protected boolean entryMatchesFilter(T entry, String filterText)
    {
        return filterText.isEmpty() || this.getDisplayString(entry).toLowerCase().contains(filterText);
    }

    protected int getRequiredWidth(List<T> entriesIn,
                                   EntryWidgetFactory<T> entryFactory,
                                   boolean supportsMultiSelection)
    {
        int maxWidth = 0;

        if (supportsMultiSelection)
        {
            String key = this.multiSelectionTranslationKey;
            maxWidth = StyledTextLine.translate(key, 99).renderWidth;
        }

        for (T entry : entriesIn)
        {
            @Nullable StyledTextLine text = entryFactory.getText(entry);
            @Nullable InteractableWidget iconWidget = entryFactory.createIconWidget(entry);
            int iconWidth = iconWidget != null ? iconWidget.getWidth() : 0;
            int width = iconWidth + (text != null ? text.renderWidth : 0);

            if (iconWidget != null)
            {
                width += iconWidget.getMargin().getRight();
            }

            maxWidth = Math.max(width, maxWidth);
        }

        return maxWidth;
    }

    protected String getDisplayString(T entry)
    {
        return getDisplayString(entry, this.stringFactory);
    }

    /**
     * @return the currently selected entry, if there is exactly one entry selected
     */
    @Nullable
    public T getSelectedEntry()
    {
        return this.selectionHandler.getSelectedEntryIfSingle();
    }

    protected void onEntryClicked(T entry)
    {
        if (this.closeOnSelect)
        {
            this.setOpen(false);
        }

        this.setSelectedEntry(entry);
    }

    public void setSelectedEntry(@Nullable T entry)
    {
        this.selectionHandler.onEntrySelected(entry);
        this.updateCurrentEntryBar();
        this.reAddSubWidgets();

        if (entry != null)
        {
            this.scrollBar.setValue(this.entries.indexOf(entry));
        }

        if (this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }

    protected void onScrolled()
    {
        this.reAddSubWidgets();
    }

    protected void createEntryWidgets()
    {
        int startIndex = this.scrollBar.getValue();
        int endIndex = Math.min(startIndex + this.maxVisibleEntries, this.filteredEntries.size());
        int width = this.getWidth() - this.getBorderRenderer().getNormalSettings().getBorderWidth() * 2 - this.scrollBar.getWidth();
        int height = this.lineHeight;
        int x = this.getX() + 1;
        int y = this.getDropDownY() + 1;
        int colorOdd = 0xFF303030;
        int colorEven = 0xFF404040;

        for (int i = startIndex; i < endIndex; ++i)
        {
            boolean isEven = (i & 0x1) == 0;
            T entry = this.filteredEntries.get(i);
            InteractableWidget widget = this.entryWidgetFactory.createWidget(width - 4, height, entry);

            if (this.selectionHandler.isEntrySelected(entry))
            {
                widget.getTextSettings().setTextColor(0xFFFFFF50);
                widget.getBorderRenderer().getNormalSettings().setBorderWidth(2);
                widget.getBorderRenderer().getNormalSettings().getColor().setTopBottom(0);
                widget.getBorderRenderer().getNormalSettings().getColor().setLeftRight(0xFF00FF90);
            }

            widget.setPosition(x + 2, y);
            widget.setClickListener(() -> this.onEntryClicked(entry));
            int bgColor = isEven ? colorEven : colorOdd;
            widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, bgColor);
            widget.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, bgColor + 0x202020);
            y += height;

            this.addWidget(widget);
        }
    }

    protected void updateCurrentEntryBar()
    {
        if (this.useCurrentEntryBar)
        {
            if (this.currentEntryBarWidget != null)
            {
                this.removeWidget(this.currentEntryBarWidget);
            }

            int count = this.selectionHandler.getSelectedEntryCount();
            T selected = this.selectionHandler.getSelectedEntryIfSingle();
            InteractableWidget widget;

            if (count == 1 && selected != null)
            {
                EntryWidget entryWidget = this.entryWidgetFactory.createWidget(this.getWidth(), this.lineHeight, selected);
                int unusableWidth = entryWidget.textOffset.getXOffset() + entryWidget.getPadding().getHorizontalTotal() + 16;
                entryWidget.getTextLineRenderer().setMaxWidth(entryWidget.getWidth() - unusableWidth);
                widget = entryWidget;
            }
            else
            {
                widget = new ContainerWidget(this.getWidth(), this.lineHeight);
                widget.getTextOffset().setYOffset(1);

                if (count > 1)
                {
                    String key = this.multiSelectionTranslationKey;
                    widget.setText(StyledTextLine.translate(key, count));
                }
                else
                {
                    widget.setText(StyledTextLine.of("-"));
                }
            }

            Icon icon = this.isOpen() ? DefaultIcons.ARROW_UP : DefaultIcons.ARROW_DOWN;
            int borderColor = this.isOpen() ? this.borderColorOpen : 0xFFC0C0C0;
            widget.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, borderColor);
            widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF000000);
            widget.setIcon(icon);
            widget.getIconOffset().setXOffset(widget.getWidth() - 15);
            widget.getIconOffset().setYOffset(1);
            widget.setClickListener(this::toggleOpen);
            widget.setHoverInfoFactory(this.getHoverInfoFactory());
            widget.setHoverInfoRequiresShift(true);
            widget.setPosition(this.getX(), this.getY());

            this.currentEntryBarWidget = widget;
            this.addWidget(widget);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        // Render the open dropdown list
        if (this.isOpen())
        {
            int diffX = x - this.getX();
            int diffY = y - this.getY();
            int ddY = diffY + this.getDropDownY();
            int bgColor = 0xFF404040;
            int height = this.dropdownHeight;

            RenderUtils.color(1f, 1f, 1f, 1f);
            ShapeRenderUtils.renderOutlinedRectangle(x, ddY, z, this.getWidth(), height, bgColor, this.borderColorOpen);

            this.renderSearchTip(diffX, diffY, z, ctx);
        }

        super.renderAt(x, y, z, ctx);
    }

    protected void renderSearchTip(int diffX, int diffY, float z, ScreenContext ctx)
    {
        if (this.searchOpen == false && MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.getBooleanValue())
        {
            int tx = this.searchTextField.getX() + diffX;
            int ty = this.searchTextField.getY() + diffY;
            int sw = this.searchTipText.renderWidth + 10;
            int right = tx + sw;
            int windowWidth = GuiUtils.getScaledWindowWidth();

            if (right > windowWidth)
            {
                tx -= (right - windowWidth);
            }

            ShapeRenderUtils.renderOutlinedRectangle(tx, ty, z, sw, 16, 0xFF000000, 0xFFFFFF20);
            this.renderTextLine(tx + 4, ty + 4, z + 0.1f, 0xFFFFC000, false, this.searchTipText, ctx);
        }
    }

    public static class EntryWidget extends ContainerWidget
    {
        protected final SingleTextLineRenderer textLineRenderer;
        @Nullable protected InteractableWidget iconWidget;

        public EntryWidget(int width, int height, @Nullable InteractableWidget iconWidget)
        {
            super(width, height);

            this.textLineRenderer = new SingleTextLineRenderer(this::getTextSettings);
            this.iconWidget = iconWidget;
            this.textLineRenderer.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF30C0C0);
            this.textLineRenderer.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0xFF000000);
            this.textLineRenderer.getPadding().setAll(3, 3, 1, 3);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();
            this.addWidgetIfNotNull(this.iconWidget);
        }

        @Override
        public void updateSubWidgetPositions()
        {
            super.updateSubWidgetPositions();

            if (this.iconWidget != null)
            {
                this.iconWidget.setX(this.getX() + this.getPadding().getLeft());
                this.iconWidget.centerVerticallyInside(this);
            }
        }

        public SingleTextLineRenderer getTextLineRenderer()
        {
            return this.textLineRenderer;
        }

        @Override
        public void setText(StyledTextLine text)
        {
            this.textLineRenderer.setStyledTextLine(text);
        }

        @Override
        public void renderAt(int x, int y, float z, ScreenContext ctx)
        {
            super.renderAt(x, y, z, ctx);

            int h = this.textLineRenderer.getTotalHeight();
            int tx = x + this.textOffset.getXOffset();
            int ty = y + ElementOffset.getCenteredElementOffset(this.getHeight(), h);

            boolean highlight = this.textLineRenderer.hasClampedContent() && this.isHoveredForRender(ctx);
            this.textLineRenderer.renderAt(tx, ty, z + 0.125f, highlight, ctx);
        }
    }

    public static class DefaultEntryWidgetFactory<T> implements EntryWidgetFactory<T>
    {
        @Nullable protected final Function<T, String> stringFactory;
        @Nullable protected final IconWidgetFactory<T> iconWidgetFactory;

        public DefaultEntryWidgetFactory(@Nullable Function<T, String> stringFactory, @Nullable IconWidgetFactory<T> iconWidgetFactory)
        {
            this.stringFactory = stringFactory;
            this.iconWidgetFactory = iconWidgetFactory;
        }

        @Nullable
        @Override
        public StyledTextLine getText(T data)
        {
            return this.stringFactory != null ? StyledTextLine.of(this.stringFactory.apply(data)) : null;
        }

        @Nullable
        @Override
        public InteractableWidget createIconWidget(T data)
        {
            return this.iconWidgetFactory != null ? this.iconWidgetFactory.create(data) : null;
        }

        @Override
        public EntryWidget createWidget(int width, int height, T entry)
        {
            InteractableWidget iconWidget = null;
            int textOffset = 0;

            if (this.iconWidgetFactory != null)
            {
                iconWidget = this.iconWidgetFactory.create(entry);
                int maxSize = height;
                iconWidget.setAutomaticWidth(false);
                iconWidget.setAutomaticHeight(false);
                iconWidget.setMaxWidth(maxSize);
                iconWidget.setMaxHeight(maxSize);
                iconWidget.updateSize();
                textOffset += maxSize + 4;
            }

            EntryWidget widget = new EntryWidget(width, height, iconWidget);
            widget.getPadding().setLeft(4);

            if (this.stringFactory != null)
            {
                widget.getTextOffset().setXOffset(textOffset);
                widget.getTextOffset().setYOffset(0);
                int unusableWidth = textOffset + 6;
                widget.getTextLineRenderer().setMaxWidth(width - unusableWidth);
                widget.setText(StyledTextLine.of(getDisplayString(entry, this.stringFactory)));
            }

            return widget;
        }
    }

    public static class DefaultSingleEntrySelectionHandler<T> implements SelectionHandler<T>
    {
        @Nullable protected T selectedEntry;

        public DefaultSingleEntrySelectionHandler()
        {
        }

        @Nullable
        @Override
        public T getSelectedEntryIfSingle()
        {
            return this.selectedEntry;
        }

        @Override
        public int getSelectedEntryCount()
        {
            return this.selectedEntry != null ? 1 : 0;
        }

        @Override
        public boolean isEntrySelected(T entry)
        {
            return this.selectedEntry == entry;
        }

        @Override
        public void onEntrySelected(T entry)
        {
            this.selectedEntry = entry;
        }
    }

    public static class DelegatingSingleEntrySelectionHandler<T> implements SelectionHandler<T>
    {
        protected final ToBooleanFunction<T> selectionChecker;
        protected final SelectionListener<T> selectionListener;
        @Nullable protected T selectedEntry;

        public DelegatingSingleEntrySelectionHandler(ToBooleanFunction<T> selectionChecker,
                                                     SelectionListener<T> selectionListener)
        {
            this.selectionChecker = selectionChecker;
            this.selectionListener = selectionListener;
        }

        @Nullable
        @Override
        public T getSelectedEntryIfSingle()
        {
            return this.selectedEntry;
        }

        @Override
        public int getSelectedEntryCount()
        {
            return 1;
        }

        @Override
        public boolean isEntrySelected(T entry)
        {
            return this.selectionChecker.applyAsBoolean(entry);
        }

        @Override
        public void onEntrySelected(T entry)
        {
            this.selectionListener.onSelectionChange(entry);

            if (this.selectionChecker.applyAsBoolean(entry))
            {
                this.selectedEntry = entry;
            }
            else
            {
                this.selectedEntry = null;
            }
        }
    }

    public static class SimpleMultiEntrySelectionHandler<T> implements SelectionHandler<T>
    {
        protected final ToBooleanFunction<T> selectionChecker;
        protected final SelectionListener<T> selectionListener;
        protected final IntSupplier selectionCountSupplier;
        @Nullable protected T selectedEntry;

        public SimpleMultiEntrySelectionHandler(ToBooleanFunction<T> selectionChecker,
                                                SelectionListener<T> selectionListener,
                                                IntSupplier selectionCountSupplier)
        {
            this.selectionChecker = selectionChecker;
            this.selectionListener = selectionListener;
            this.selectionCountSupplier = selectionCountSupplier;
        }

        @Override
        public boolean supportsMultiSelection()
        {
            return true;
        }

        @Nullable
        @Override
        public T getSelectedEntryIfSingle()
        {
            return this.getSelectedEntryCount() == 1 ? this.selectedEntry : null;
        }

        @Override
        public int getSelectedEntryCount()
        {
            return this.selectionCountSupplier.getAsInt();
        }

        @Override
        public boolean isEntrySelected(T entry)
        {
            return this.selectionChecker.applyAsBoolean(entry);
        }

        @Override
        public void onEntrySelected(T entry)
        {
            this.selectionListener.onSelectionChange(entry);

            if (this.getSelectedEntryCount() == 1)
            {
                this.selectedEntry = entry;
            }
            else
            {
                this.selectedEntry = null;
            }
        }
    }

    public interface IconWidgetFactory<T>
    {
        InteractableWidget create(T data);
    }

    public interface EntryWidgetFactory<T>
    {
        @Nullable StyledTextLine getText(T data);

        @Nullable InteractableWidget createIconWidget(T data);

        EntryWidget createWidget(int width, int height, T entry);
    }

    public interface SelectionHandler<T>
    {
        default boolean supportsMultiSelection()
        {
            return false;
        }

        int getSelectedEntryCount();

        @Nullable T getSelectedEntryIfSingle();

        boolean isEntrySelected(T entry);

        void onEntrySelected(T entry);
    }

    protected static <T> String getDisplayString(T entry, @Nullable Function<T, String> stringFactory)
    {
        if (stringFactory != null)
        {
            return stringFactory.apply(entry);
        }

        return entry.toString();
    }
}
