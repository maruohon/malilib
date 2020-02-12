package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import fi.dy.masa.malilib.interfaces.ITextFieldWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget implements ITextFieldWidget
{
    @Accessor("maxLength")
    @Override
    public abstract int clientcommands_getMaxLength();
}
