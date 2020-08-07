package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.util.ChatAllowedCharacters;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.button.GenericButton;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.util.HorizontalAlignment;

public class WidgetSearchBar extends WidgetContainer
{
    protected final WidgetTextFieldBase textField;
    protected final GenericButton buttonSearchToggle;
    protected final IGuiIcon toggleButtonIcon;
    protected final HorizontalAlignment toggleButtonAlignment;
    protected final int searchBarOffsetX;
    protected boolean searchOpen;

    public WidgetSearchBar(int x, int y, int width, int height, int searchBarOffsetX,
                           IGuiIcon toggleButtonIcon, HorizontalAlignment toggleButtonAlignment)
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

        this.textField = new WidgetTextFieldBase(tx, y, width - iw - 7 - Math.abs(searchBarOffsetX), height);
        this.textField.setUpdateListenerAlways(true);

        this.reAddSubWidgets();
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
    public void updateSubWidgetPositions(int oldX, int oldY)
    {
        super.updateSubWidgetPositions(oldX, oldY);

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

    public void setTextFieldListener(@Nullable ITextFieldListener listener)
    {
        this.textField.setListener(listener);
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

                this.setSearchOpen(false);
                this.textField.setText("");
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
