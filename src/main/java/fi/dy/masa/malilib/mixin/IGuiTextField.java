package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.GuiTextField;

@Mixin(GuiTextField.class)
public interface IGuiTextField
{
    @Accessor("height")
    int getHeight();
}
