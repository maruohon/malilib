package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;

import fi.dy.masa.malilib.command.ClientCommandManager;
import fi.dy.masa.malilib.interfaces.ITextFieldWidget;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.server.command.CommandSource;

@Mixin(CommandSuggestor.class)
public class MixinCommandSuggestor
{
    @Shadow protected TextFieldWidget textField;
    @Shadow private ParseResults<CommandSource> parse;

    @Unique private int oldMaxLength;
    @Unique private boolean wasClientCommand = false;

    @Inject(method = "refresh", at = @At("RETURN"))
    public void onRefresh(CallbackInfo ci)
    {
        boolean isClientCommand;
        if (parse == null)
        {
            isClientCommand = false;
        }
        else
        {
            StringReader reader = new StringReader(parse.getReader().getString());
            reader.skip();
            String command = reader.canRead() ? reader.readUnquotedString() : "";
            isClientCommand = ClientCommandManager.isClientSideCommand(command);
        }

        if (isClientCommand && !wasClientCommand)
        {
            wasClientCommand = true;
            oldMaxLength = ((ITextFieldWidget) textField).clientcommands_getMaxLength();
            textField.setMaxLength(Math.max(oldMaxLength, 32500));
        }
        else if (!isClientCommand && wasClientCommand)
        {
            wasClientCommand = false;
            textField.setMaxLength(oldMaxLength);
        }
    }
}