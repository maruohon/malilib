package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetDirectoryNavigation extends WidgetBase
{
    protected final File currentDir;
    protected final File rootDir;
    protected final Minecraft mc;
    protected final IDirectoryNavigator navigator;
    protected final Icon iconRoot;
    protected final Icon iconUp;
    protected final Icon iconSearch;
    protected final GuiTextFieldGeneric searchBox;
    protected boolean searchOpen;

    public WidgetDirectoryNavigation(int x, int y, int width, int height, float zLevel,
            File currentDir, File rootDir, Minecraft mc, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, zLevel);

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.mc = mc;
        this.navigator = navigator;
        this.iconRoot = new Icon(x, y + 1, zLevel, iconProvider.getIconRoot(), mc);
        this.iconUp = new Icon(x + iconProvider.getIconRoot().getWidth() + 2, y + 1, zLevel, iconProvider.getIconUp(), mc);
        this.iconSearch = new Icon(x + width - iconProvider.getIconRoot().getWidth() - 2, y + 1, zLevel, iconProvider.getIconSearch(), mc);
        this.searchBox = new GuiTextFieldGeneric(x + 14, y, width - 32, height, mc.fontRenderer);
    }

    public String getFilter()
    {
        return this.searchOpen ? this.searchBox.getText() : "";
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        Icon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

        if (this.searchOpen == false)
        {
            if (hoveredIcon == this.iconRoot)
            {
                this.navigator.switchToRootDirectory();
                return true;
            }
            else if (hoveredIcon == this.iconUp)
            {
                this.navigator.switchToParentDirectory();
                return true;
            }
        }

        if (this.searchBox.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }
        else if (hoveredIcon == this.iconSearch)
        {
            this.searchOpen = ! this.searchOpen;

            if (this.searchOpen)
            {
                this.searchBox.setFocused(true);
            }

            return true;
        }

        return false;
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
                this.searchOpen = false;
                return true;
            }
        }
        else if (keyCode != Keyboard.KEY_ESCAPE &&
                 keyCode != Keyboard.KEY_BACK &&
                 keyCode != Keyboard.KEY_RETURN &&
                 keyCode != Keyboard.KEY_LEFT &&
                 keyCode != Keyboard.KEY_RIGHT &&
                 keyCode != Keyboard.KEY_UP &&
                 keyCode != Keyboard.KEY_DOWN &&
                 keyCode != Keyboard.KEY_PRIOR &&
                 keyCode != Keyboard.KEY_NEXT &&
                 keyCode != Keyboard.KEY_HOME &&
                 keyCode != Keyboard.KEY_END)
        {
            this.searchOpen = true;
            this.searchBox.setFocused(true);
            this.searchBox.setText("");
            this.searchBox.setCursorPositionEnd();
            this.searchBox.textboxKeyTyped(typedChar, keyCode);
            return true;
        }

        return false;
    }

    @Nullable
    protected Icon getHoveredIcon(int mouseX, int mouseY)
    {
        if (this.searchOpen == false)
        {
            if (this.iconRoot.isMouseOver(mouseX, mouseY))
            {
                return this.iconRoot;
            }
            else if (this.iconUp.isMouseOver(mouseX, mouseY))
            {
                return this.iconUp;
            }
        }

        if (this.iconSearch.isMouseOver(mouseX, mouseY))
        {
            return this.iconSearch;
        }

        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        Icon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

        GlStateManager.color(1f, 1f, 1f, 1f);
        this.iconSearch.render(false, hoveredIcon == this.iconSearch);

        if (this.searchOpen)
        {
            this.searchBox.drawTextBox();
        }
        else
        {
            this.iconRoot.render(false, hoveredIcon == this.iconRoot);
            this.iconUp.render(false, hoveredIcon == this.iconUp);

            final int widthUp = this.iconUp.getWidth();

            // Draw the directory path text background
            GuiBase.drawRect(this.iconUp.x + widthUp + 6, this.y, this.x + this.width, this.y + this.height, 0x20FFFFFF);

            int textColor = 0xC0C0C0C0;
            int maxLen = (this.width - 40) / this.mc.fontRenderer.getStringWidth("a") - 4; // FIXME
            String path = FileUtils.getJoinedTrailingPathElements(this.currentDir, this.rootDir, maxLen, " / ");
            this.mc.fontRenderer.drawString(path, this.iconUp.x + widthUp + 9, this.y + 3, textColor);
        }
    }

    public static class Icon extends WidgetBase
    {
        protected final Minecraft mc;
        protected final IGuiIcon icon;

        public Icon(int x, int y, float zLevel, IGuiIcon icon, Minecraft mc)
        {
            super(x, y, icon.getWidth(), icon.getHeight(), zLevel);

            this.mc = mc;
            this.icon = icon;
        }

        public void render(boolean enabled, boolean selected)
        {
            this.mc.getTextureManager().bindTexture(this.icon.getTexture());
            this.icon.renderAt(this.x, this.y, this.zLevel, enabled, selected);

            if (selected)
            {
                RenderUtils.drawOutlinedBox(this.x, this.y, this.width, this.height, 0x20C0C0C0, 0xE0FFFFFF);
            }
        }
    }
}
