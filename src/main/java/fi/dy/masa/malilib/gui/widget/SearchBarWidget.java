package fi.dy.masa.malilib.gui.widget;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.EventListener;

public class SearchBarWidget extends ContainerWidget
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton buttonSearchToggle;
    protected final MultiIcon toggleButtonIcon;
    protected final HorizontalAlignment toggleButtonAlignment;
    protected final int searchBarOffsetX;
    @Nullable protected EventListener geometryChangeListener;
    @Nullable protected EventListener openCloseListener;
    protected boolean searchOpen;

    public SearchBarWidget(int x, int y, int width, int height, int searchBarOffsetX,
                           MultiIcon toggleButtonIcon,
                           HorizontalAlignment toggleButtonAlignment,
                           Consumer<String> textChangeListener,
                           @Nullable EventListener openCloseListener)
    {
        super(x, y, width, height);

        this.toggleButtonIcon = toggleButtonIcon;
        this.toggleButtonAlignment = toggleButtonAlignment;
        this.searchBarOffsetX = searchBarOffsetX;
        this.openCloseListener = openCloseListener;
        this.buttonSearchToggle = GenericButton.createIconOnly(toggleButtonIcon);
        this.buttonSearchToggle.setActionListener(this::toggleSearchOpen);
        this.buttonSearchToggle.setPlayClickSound(false);

        int iw = toggleButtonIcon.getWidth();
        this.textField = new BaseTextFieldWidget(width - iw - 7 - Math.abs(searchBarOffsetX), height);
        this.textField.setUpdateListenerAlways(true);
        this.textField.setUpdateListenerFromTextSet(true);
        this.textField.setListener(textChangeListener);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.buttonSearchToggle);

        if (this.searchOpen)
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
        int offX = this.searchBarOffsetX;
        int iw = this.toggleButtonIcon.getWidth();
        int ix = this.toggleButtonAlignment == HorizontalAlignment.RIGHT ? x + this.getWidth() - iw - 2 : x + 2;
        int tx = this.toggleButtonAlignment == HorizontalAlignment.RIGHT ? x - offX + 1 : x + iw + 6 + offX;

        this.buttonSearchToggle.setPosition(ix, y + 1);
        this.textField.setPosition(tx, y);
        this.textField.setWidth(this.getWidth() -  iw - 7 - Math.abs(offX));
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
        return this.searchOpen ? this.textField.getText() : "";
    }

    public boolean hasFilter()
    {
        return this.getFilter().isEmpty() == false;
    }

    public boolean isSearchOpen()
    {
        return this.searchOpen;
    }

    public void toggleSearchOpen()
    {
        this.setSearchOpen(! this.searchOpen);
    }

    public void setSearchOpen(boolean isOpen)
    {
        boolean wasOpen = this.searchOpen;
        this.searchOpen = isOpen;

        if (this.searchOpen)
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
        if (this.searchOpen && keyCode == Keyboard.KEY_ESCAPE)
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
        if (this.searchOpen == false && charIn != ' ')
        {
            this.setSearchOpen(true);
            this.textField.onCharTyped(charIn, modifiers);
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }
}
