package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.util.ChatAllowedCharacters;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.listener.TextChangeListener;

public class SearchBarWidget extends ContainerWidget
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton buttonSearchToggle;
    protected final MultiIcon toggleButtonIcon;
    protected final HorizontalAlignment toggleButtonAlignment;
    protected final int searchBarOffsetX;
    @Nullable protected EventListener geometryChangeListener;
    protected boolean searchOpen;

    public SearchBarWidget(int x, int y, int width, int height, int searchBarOffsetX,
                           MultiIcon toggleButtonIcon, HorizontalAlignment toggleButtonAlignment,
                           TextChangeListener textChangeListener)
    {
        super(x, y, width, height);

        int iw = toggleButtonIcon.getWidth();
        int ix = toggleButtonAlignment == HorizontalAlignment.RIGHT ? x + width - iw - 1 : x + 1;
        int tx = toggleButtonAlignment == HorizontalAlignment.RIGHT ? x - searchBarOffsetX + 1 : x + iw + 6 + searchBarOffsetX;

        this.toggleButtonIcon = toggleButtonIcon;
        this.toggleButtonAlignment = toggleButtonAlignment;
        this.searchBarOffsetX = searchBarOffsetX;
        this.buttonSearchToggle = GenericButton.createIconOnly(ix, y, toggleButtonIcon);
        this.buttonSearchToggle.setActionListener((btn, mbtn) -> this.toggleSearchOpen());

        this.textField = new BaseTextFieldWidget(tx, y, width - iw - 7 - Math.abs(searchBarOffsetX), 14);
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
        int ix = this.toggleButtonAlignment == HorizontalAlignment.RIGHT ? x + this.getWidth() - iw - 1 : x + 1;
        int tx = this.toggleButtonAlignment == HorizontalAlignment.RIGHT ? x - offX + 1 : x + iw + 6 + offX;

        this.buttonSearchToggle.setPosition(ix, y);
        this.textField.setPosition(tx, y);
        this.textField.setWidth(this.getWidth() -  iw - 7 - Math.abs(offX));
    }

    public void setTextFieldListener(@Nullable TextChangeListener listener)
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
        this.searchOpen = isOpen;

        if (this.searchOpen)
        {
            this.textField.setFocused(true);
        }

        this.reAddSubWidgets();

        // Update the parent or other listeners who may care about
        // the search bar opening/closing and maybe changing in size
        if (this.geometryChangeListener != null)
        {
            this.geometryChangeListener.onEvent();
        }
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.searchOpen)
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                if (BaseScreen.isShiftDown())
                {
                    BaseScreen.openGui(null);
                }
                else
                {
                    this.setSearchOpen(false);
                    this.textField.setText("");
                }

                return true;
            }
        }
        else if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
        {
            this.setSearchOpen(true);
            this.textField.onKeyTyped(typedChar, keyCode);
            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }
}
