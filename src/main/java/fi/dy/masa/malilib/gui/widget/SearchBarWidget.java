package fi.dy.masa.malilib.gui.widget;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.position.Vec2i;

public class SearchBarWidget extends ContainerWidget
{
    protected final BaseTextFieldWidget textField;
    protected HorizontalAlignment toggleButtonAlignment = HorizontalAlignment.LEFT;
    protected Vec2i searchBarOffset = Vec2i.ZERO;
    @Nullable protected GenericButton searchToggleButton;
    @Nullable protected EventListener geometryChangeListener;
    @Nullable protected EventListener openCloseListener;
    protected boolean alwaysOpen;
    protected boolean isSearchOpen;

    public SearchBarWidget(int width,
                           int height,
                           Consumer<String> textChangeListener,
                           @Nullable EventListener openCloseListener)
    {
        super(width, height);

        this.openCloseListener = openCloseListener;
        this.margin.setTop(1);

        this.textField = new BaseTextFieldWidget(width - 7, height);
        this.textField.setUpdateListenerAlways(true);
        this.textField.setUpdateListenerFromTextSet(true);
        this.textField.setListener(textChangeListener);
    }

    public SearchBarWidget(int width,
                           int height,
                           Consumer<String> textChangeListener,
                           @Nullable EventListener openCloseListener,
                           MultiIcon toggleButtonIcon)
    {
        this(width, height, textChangeListener, openCloseListener);

        this.searchToggleButton = GenericButton.createIconOnly(toggleButtonIcon);
        this.searchToggleButton.setActionListener(this::toggleSearchOpen);
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
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

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

        this.textField.setPosition(tx, ty);
        this.textField.centerVerticallyInside(this);
        this.textField.setWidth(tw);
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

    public void setGeometryChangeListener(@Nullable EventListener geometryChangeListener)
    {
        this.geometryChangeListener = geometryChangeListener;
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

        if (this.isSearchOpen)
        {
            this.textField.setFocused(true);
        }

        this.reAddSubWidgets();

        if (this.openCloseListener != null && wasOpen != isOpen)
        {
            this.openCloseListener.onEvent();
        }

        // Update the parent or other listeners who may care about
        // the search bar opening/closing and maybe changing in size
        if (this.geometryChangeListener != null)
        {
            this.geometryChangeListener.onEvent();
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
                this.setSearchOpen(false);
                this.textField.setText("");
            }

            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.isSearchOpen() == false && charIn != ' ')
        {
            this.setSearchOpen(true);
            this.textField.onCharTyped(charIn, modifiers);
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }
}
