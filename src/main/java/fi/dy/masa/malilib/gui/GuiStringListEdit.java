package fi.dy.masa.malilib.gui;

import java.io.IOException;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.widgets.WidgetListStringList;
import fi.dy.masa.malilib.gui.widgets.WidgetStringListEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

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

    public GuiStringListEdit(IConfigStringList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler, GuiScreen parent)
    {
        super(0, 0);

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;
        this.mc = Minecraft.getMinecraft();
        this.title = I18n.format("malilib.gui.title.string_list_edit", config.getName());

        this.setWidthAndHeight(400, 260);
        this.centerOnScreen();

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

        //this.setWorldAndResolution(this.mc, this.dialogWidth, this.dialogHeight);
    }

    public void setWidthAndHeight(int width, int height)
    {
        this.dialogWidth = width;
        this.dialogHeight = height;
    }

    public void centerOnScreen()
    {
        if (this.getParent() != null)
        {
            this.dialogLeft = this.getParent().width / 2 - this.dialogWidth / 2;
            this.dialogTop = this.getParent().height / 2 - this.dialogHeight / 2;
        }
        else
        {
            ScaledResolution res = new ScaledResolution(this.mc);
            this.dialogLeft = res.getScaledWidth() / 2 - this.dialogWidth / 2;
            this.dialogTop = res.getScaledHeight() / 2 - this.dialogHeight / 2;
        }
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
        System.out.printf("%s - dl: %d, dt: %d, dw: %d\n", this.getClass().getSimpleName(), this.dialogLeft, this.dialogTop, this.dialogWidth);
        return new WidgetListStringList(this.dialogLeft + 10, this.dialogTop + 20,
                this.getBrowserWidth(), this.getBrowserHeight(), this.dialogWidth - 100, this);
    }

    @Override
    public void onGuiClosed()
    {
        //this.config.setStrings(this.strings);
        System.out.printf("%s - onGuiClosed\n", this.getClass().getSimpleName());

        super.onGuiClosed();

        if (this.getListWidget().wereConfigsModified())
        {
            this.getListWidget().applyPendingModifications();
            ConfigManager.getInstance().onConfigsChanged(this.configGui.getModId());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().drawScreen(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.drawString(this.fontRenderer, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.onKeyTyped(typedChar, keyCode);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else
        {
            return super.onKeyTyped(typedChar, keyCode);
        }
    }

    protected static class Listener implements IButtonActionListener<ButtonGeneric>
    {
        @Override
        public void actionPerformed(ButtonGeneric control)
        {
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
        }
    }
}
