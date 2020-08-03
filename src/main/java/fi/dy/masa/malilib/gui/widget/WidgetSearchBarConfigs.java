package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeyBind;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindMulti;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyBindSettings.Context;
import fi.dy.masa.malilib.util.data.HorizontalAlignment;

public class WidgetSearchBarConfigs extends WidgetSearchBar
{
    protected final KeyBindMulti searchKey;
    protected final ConfigButtonKeyBind button;

    public WidgetSearchBarConfigs(int x, int y, int width, int height, int searchBarOffsetX,
            IGuiIcon iconSearch, HorizontalAlignment iconAlignment)
    {
        super(x, y + 3, width - 160, 14, searchBarOffsetX, iconSearch, iconAlignment);

        KeyBindSettings settings = KeyBindSettings.create(Context.ANY, KeyAction.BOTH, true, true, false, false, false);
        this.searchKey = KeyBindMulti.fromStorageString("", "", settings);
        this.button = new ConfigButtonKeyBind(x + width - 150, y, 140, 20, this.searchKey, null);
    }

    public IKeyBind getKeybind()
    {
        return this.searchKey;
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.searchOpen && this.searchKey.getKeys().size() > 0);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen)
        {
            if (this.button.isMouseOver(mouseX, mouseY))
            {
                boolean selectedPre = this.button.isSelected();
                this.button.onMouseClicked(mouseX, mouseY, mouseButton);

                if (selectedPre == false)
                {
                    this.button.onSelected();
                }

                return true;
            }
            else if (this.button.isSelected())
            {
                this.button.onClearSelection();
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.searchOpen && this.button.isSelected())
        {
            this.button.onKeyPressed(keyCode);

            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.button.onClearSelection();
            }

            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.render(mouseX, mouseY, isActiveGui, hovered);

        if (this.searchOpen)
        {
            this.button.render(mouseX, mouseY, isActiveGui, hovered);
        }
    }
}
