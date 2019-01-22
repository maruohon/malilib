package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.widgets.WidgetListStringList;
import fi.dy.masa.malilib.gui.widgets.WidgetStringListEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resource.language.I18n;

public class GuiStringListEdit extends GuiListBase<String, WidgetStringListEntry, WidgetListStringList>
{
    protected final IConfigStringList config;
    protected final IConfigGui configGui;
    protected int dialogWidth;
    protected int dialogHeight;
    protected int dialogLeft;
    protected int dialogTop;
    protected int labelWidth;
    protected int textFieldWidth;
    @Nullable protected final IDialogHandler dialogHandler;

    public GuiStringListEdit(IConfigStringList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler, Gui parent)
    {
        super(0, 0);

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;
        this.client = MinecraftClient.getInstance();
        this.title = I18n.translate("malilib.gui.title.string_list_edit", config.getName());

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }
    }

    protected void setWidthAndHeight()
    {
        this.dialogWidth = 400;
        this.dialogHeight = this.client.window.getScaledHeight() - 90;
    }

    protected void centerOnScreen()
    {
        if (this.getParent() != null)
        {
            this.dialogLeft = this.getParent().width / 2 - this.dialogWidth / 2;
            this.dialogTop = this.getParent().height / 2 - this.dialogHeight / 2;
        }
        else
        {
            this.dialogLeft = 20;
            this.dialogTop = 20;
        }
    }

    @Override
    public void initialize(MinecraftClient mc, int width, int height)
    {
        if (this.getParent() != null)
        {
            this.getParent().initialize(mc, width, height);
        }

        super.initialize(mc, width, height);

        this.setWidthAndHeight();
        this.centerOnScreen();

        this.reCreateListWidget();
        this.onInitialized();
    }

    public IConfigStringList getConfig()
    {
        return this.config;
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.dialogWidth - 14;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.dialogHeight - 30;
    }

    @Override
    protected WidgetListStringList createListWidget(int listX, int listY)
    {
        // The listX and listY are set via the constructor, which in this dialog-like GUI's case is too early to know them
        return new WidgetListStringList(this.dialogLeft + 10, this.dialogTop + 20, this.getBrowserWidth(), this.getBrowserHeight(), this.dialogWidth - 100, this);
    }

    @Override
    public void onClosed()
    {
        if (this.getListWidget().wereConfigsModified())
        {
            this.getListWidget().applyPendingModifications();
            ConfigManager.getInstance().onConfigsChanged(this.configGui.getModId());
        }

        super.onClosed();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().draw(mouseX, mouseY, partialTicks);
        }

        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.drawString(this.client.fontRenderer, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == KeyCodes.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else
        {
            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }
}
