package fi.dy.masa.malilib.gui.widget.list.search;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.position.Vec2i;

public class SearchBarWidget extends ContainerWidget
{
    protected final BaseTextFieldWidget textField;
    protected HorizontalAlignment toggleButtonAlignment = HorizontalAlignment.LEFT;
    protected Vec2i searchBarOffset = Vec2i.ZERO;
    @Nullable protected EventListener openCloseListener;
    @Nullable protected GenericButton searchToggleButton;
    protected boolean alwaysOpen;
    protected boolean isSearchOpen;

    public SearchBarWidget(int width,
                           int height,
                           EventListener searchInputChangeListener,
                           @Nullable EventListener openCloseListener)
    {
        super(width, height);

        this.openCloseListener = openCloseListener;
        this.margin.setTop(1);

        this.textField = new BaseTextFieldWidget(width - 7, height);
        this.textField.setUpdateListenerAlways(true);
        this.textField.setUpdateListenerFromTextSet(true);
        this.textField.setListener((s) -> searchInputChangeListener.onEvent());
    }

    public SearchBarWidget(int width,
                           int height,
                           EventListener searchInputChangeListener,
                           @Nullable EventListener openCloseListener,
                           MultiIcon toggleButtonIcon)
    {
        this(width, height, searchInputChangeListener, openCloseListener);

        this.searchToggleButton = GenericButton.create(toggleButtonIcon, this::toggleSearchOpen);
        this.searchToggleButton.setPlayClickSound(false);

        this.textField.setWidth(width - this.searchToggleButton.getWidth() - 4);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidgetIfNotNull(this.searchToggleButton);

        if (this.isSearchOpen())
        {
            this.addWidget(this.textField);
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        int offX = this.searchBarOffset.x;
        int offY = this.searchBarOffset.y;
        int tx = x + offX + 1;
        int ty = y + offY + 1;
        int tw = this.getWidth() - offX - 2;
        boolean rightAlignButton = this.toggleButtonAlignment == HorizontalAlignment.RIGHT;

        if (this.searchToggleButton != null)
        {
            int buttonWidth = this.searchToggleButton.getWidth();
            this.searchToggleButton.setX(rightAlignButton ? this.getRight() - buttonWidth - 2 : x + 2);
            this.searchToggleButton.centerVerticallyInside(this);
            tw -= buttonWidth + 3;

            if (rightAlignButton == false)
            {
                tx += buttonWidth + 3;
            }
        }

        this.textField.setWidth(tw);
        this.textField.setPosition(tx, ty);
        //this.textField.centerVerticallyInside(this);
    }

    @Override
    public void moveSubWidgets(int diffX, int diffY)
    {
        super.moveSubWidgets(diffX, diffY);

        // If the search bar is not open and the text field is not in the widget list,
        // then the super call will not move the text field
        if (this.isSearchOpen() == false)
        {
            this.textField.moveBy(diffX, diffY);
        }
    }

    public void setAlwaysOpen(boolean alwaysOpen)
    {
        this.alwaysOpen = alwaysOpen;
    }

    public void setToggleButtonAlignment(HorizontalAlignment toggleButtonAlignment)
    {
        this.toggleButtonAlignment = toggleButtonAlignment;
    }

    public void setSearchBarOffset(Vec2i searchBarOffset)
    {
        this.searchBarOffset = searchBarOffset;
    }

    public void setTextFieldListener(@Nullable Consumer<String> listener)
    {
        this.textField.setListener(listener);
    }

    public String getFilter()
    {
        return this.isSearchOpen() ? this.textField.getText() : "";
    }

    public boolean hasFilter()
    {
        return this.isSearchOpen() && this.getFilter().isEmpty() == false;
    }

    public boolean isSearchOpen()
    {
        return this.alwaysOpen || this.isSearchOpen;
    }

    public void toggleSearchOpen()
    {
        this.setSearchOpen(! this.isSearchOpen);
    }

    public void setSearchOpen(boolean isOpen)
    {
        if (this.alwaysOpen)
        {
            return;
        }

        boolean wasOpen = this.isSearchOpen;

        this.isSearchOpen = isOpen;
        this.textField.setFocused(this.isSearchOpen);
        this.reAddSubWidgets();

        if (this.openCloseListener != null && wasOpen != isOpen)
        {
            this.openCloseListener.onEvent();
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.isSearchOpen && this.alwaysOpen == false && keyCode == Keyboard.KEY_ESCAPE)
        {
            if (BaseScreen.isShiftDown())
            {
                BaseScreen.openScreen(null);
            }
            else
            {
                this.textField.setTextNoNotify("");
                this.setSearchOpen(false);
            }

            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        // 0x7F = Delete key (wtf?)
        if (this.isSearchOpen() == false && charIn != ' ' && charIn != 0x7F)
        {
            this.setSearchOpen(true);
            this.textField.onCharTyped(charIn, modifiers);
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }
}
