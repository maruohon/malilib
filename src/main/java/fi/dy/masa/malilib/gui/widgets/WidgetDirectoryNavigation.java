package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.util.Arrays;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextInputFeedback;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.DirectoryCreator;
import fi.dy.masa.malilib.util.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;

public class WidgetDirectoryNavigation extends WidgetSearchBar
{
    protected final File currentDir;
    protected final File rootDir;
    protected final Minecraft mc;
    protected final IDirectoryNavigator navigator;
    protected final WidgetIcon iconRoot;
    protected final WidgetIcon iconUp;
    protected final WidgetIcon iconCreateDir;

    public WidgetDirectoryNavigation(int x, int y, int width, int height, float zLevel,
            File currentDir, File rootDir, Minecraft mc, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, zLevel, - iconProvider.getIconRoot().getWidth(), iconProvider.getIconSearch(), LeftRight.RIGHT, mc);

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.mc = mc;
        this.navigator = navigator;
        this.iconRoot = new WidgetIcon(x, y + 1, zLevel, iconProvider.getIconRoot(), mc);
        x += this.iconRoot.getWidth() + 2;

        this.iconUp = new WidgetIcon(x, y + 1, zLevel, iconProvider.getIconUp(), mc);
        x += this.iconUp.getWidth() + 2;

        this.iconCreateDir = new WidgetIcon(x, y + 1, zLevel, iconProvider.getIconCreateDirectory(), mc);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

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
            else if (hoveredIcon == this.iconCreateDir)
            {
                String title = "malilib.gui.title.create_directory";
                DirectoryCreator creator = new DirectoryCreator(this.currentDir, this.navigator);
                GuiTextInputFeedback gui = new GuiTextInputFeedback(256, title, "", this.mc.currentScreen, creator);
                this.mc.displayGuiScreen(gui);
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Nullable
    protected WidgetIcon getHoveredIcon(int mouseX, int mouseY)
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
            else if (this.iconCreateDir.isMouseOver(mouseX, mouseY))
            {
                return this.iconCreateDir;
            }
        }

        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        super.render(mouseX, mouseY, selected);

        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

            this.iconRoot.render(false, hoveredIcon == this.iconRoot);
            this.iconUp.render(false, hoveredIcon == this.iconUp);
            this.iconCreateDir.render(false, hoveredIcon == this.iconCreateDir);

            int pathStartX = this.iconCreateDir.x + this.iconCreateDir.getWidth() + 6;

            // Draw the directory path text background
            GuiBase.drawRect(pathStartX, this.y, this.x + this.width, this.y + this.height, 0x20FFFFFF);

            int textColor = 0xC0C0C0C0;
            int maxLen = (this.width - 40) / this.mc.fontRenderer.getStringWidth("a") - 4; // FIXME
            String path = FileUtils.getJoinedTrailingPathElements(this.currentDir, this.rootDir, maxLen, " / ");
            this.mc.fontRenderer.drawString(path, pathStartX + 3, this.y + 3, textColor);
        }
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(mouseX, mouseY, selected);

        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

            if (hoveredIcon == this.iconRoot)
            {
                RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList(I18n.format("malilib.gui.button.hover.directory_widget.root")));
            }
            else if (hoveredIcon == this.iconUp)
            {
                RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList(I18n.format("malilib.gui.button.hover.directory_widget.up")));
            }
            else if (hoveredIcon == this.iconCreateDir)
            {
                RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList(I18n.format("malilib.gui.button.hover.directory_widget.create_directory")));
            }

            RenderHelper.disableStandardItemLighting();
        }
    }
}
