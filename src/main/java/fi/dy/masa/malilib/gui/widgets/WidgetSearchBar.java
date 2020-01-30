package fi.dy.masa.malilib.gui.widgets;

import org.lwjgl.input.Keyboard;
import net.minecraft.util.ChatAllowedCharacters;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.HorizontalAlignment;

public class WidgetSearchBar extends WidgetContainer
{
    protected final GuiTextFieldGeneric searchBox;
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

        this.searchBox = new GuiTextFieldGeneric(tx, y, width - iw - 7 - Math.abs(searchBarOffsetX), height, this.textRenderer);
        this.searchBox.setZLevel(this.zLevel);
    }

    public String getFilter()
    {
        return this.searchOpen ? this.searchBox.getText() : "";
    }

    public boolean hasFilter()
    {
        return this.searchOpen && this.searchBox.getText().isEmpty() == false;
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
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen && this.searchBox.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.searchOpen)
        {
            if (this.searchBox.textboxKeyTyped(typedChar, keyCode))
            {
                return true;
            }
            else if (keyCode == Keyboard.KEY_ESCAPE)
            {
                if (GuiBase.isShiftDown())
                {
                    GuiBase.openGui(null);
                }

                this.searchOpen = false;
                return true;
            }
        }
        else if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
        {
            this.searchOpen = true;
            this.searchBox.setFocused(true);
            this.searchBox.setText("");
            this.searchBox.setCursorPositionEnd();
            this.searchBox.textboxKeyTyped(typedChar, keyCode);
            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.searchOpen)
        {
            this.searchBox.drawTextBox();
        }

        super.render(mouseX, mouseY, selected);
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);

        GuiBase.renderTextFieldDebug(this.searchBox, mouseX, mouseY, this.zLevel, renderAll, infoAlways);
    }
}
