package fi.dy.masa.malilib;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;

public class MaLiLibGuiFactory extends DefaultGuiFactory
{
    public MaLiLibGuiFactory()
    {
        super(MaLiLibReference.MOD_ID, MaLiLibReference.MOD_NAME + " configs");
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        MaLiLibConfigGui gui = new MaLiLibConfigGui();
        gui.setParent(parent);
        return gui;
    }
}
