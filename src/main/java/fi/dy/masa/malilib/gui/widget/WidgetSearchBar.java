package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.input.Keyboard;
import net.minecraft.util.ChatAllowedCharacters;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.data.HorizontalAlignment;

public class WidgetSearchBar extends WidgetContainer
{
    protected final WidgetTextFieldBase searchBox;
    protected final ButtonGeneric buttonSearchToggle;
    protected boolean searchOpen;

    public WidgetSearchBar(int x, int y, int width, int height,
            int searchBarOffsetX, IGuiIcon iconSearch, HorizontalAlignment iconAlignment)
    {
        super(x, y, width, height);

        int iw = iconSearch.getWidth();
        int ix = iconAlignment == HorizontalAlignment.RIGHT ? x + width - iw - 1 : x + 1;
        int tx = iconAlignment == HorizontalAlignment.RIGHT ? x - searchBarOffsetX + 1 : x + iw + 6 + searchBarOffsetX;

        this.buttonSearchToggle = ButtonGeneric.createIconOnly(ix, y, iconSearch);
        this.addButton(this.buttonSearchToggle, (btn, mbtn) -> this.toggleSearchOpen());

        this.searchBox = new WidgetTextFieldBase(tx, y, width - iw - 7 - Math.abs(searchBarOffsetX), height);
        this.searchBox.setUpdateListenerAlways(true);
    }

    public String getFilter()
    {
        return this.searchOpen ? this.searchBox.getText() : "";
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
            this.searchBox.setFocused(true);
        }

        this.updateSubWidgets();
    }

    protected void updateSubWidgets()
    {
        this.clearWidgets();

        this.addWidget(this.buttonSearchToggle);

        if (this.searchOpen)
        {
            this.addWidget(this.searchBox);
        }
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.searchOpen)
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                if (GuiBase.isShiftDown())
                {
                    GuiBase.openGui(null);
                }

                this.setSearchOpen(false);
                this.searchBox.setText("");
                return true;
            }
        }
        else if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
        {
            this.setSearchOpen(true);
            this.searchBox.onKeyTyped(typedChar, keyCode);
            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }
}
