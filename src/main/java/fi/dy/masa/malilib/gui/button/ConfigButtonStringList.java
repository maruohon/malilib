package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.gui.GuiStringListEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;

public class ConfigButtonStringList extends ButtonGeneric
{
    private final IConfigStringList config;
    private final IConfigGui configGui;
    @Nullable private final IDialogHandler dialogHandler;

    public ConfigButtonStringList(int id, int x, int y, int width, int height, IConfigStringList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler)
    {
        super(id, x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        this.playPressSound(Minecraft.getMinecraft().getSoundHandler());

        Minecraft mc = Minecraft.getMinecraft();

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new GuiStringListEdit(this.config, this.configGui, this.dialogHandler, null));
        }
        else
        {
            mc.displayGuiScreen(new GuiStringListEdit(this.config, this.configGui, null, mc.currentScreen));
        }
    }

    @Override
    public void updateDisplayString()
    {
        this.displayString = StringUtils.getClampedDisplayStringRenderlen(this.config.getStrings(), this.width - 10, "[ ", " ]");
    }
}
