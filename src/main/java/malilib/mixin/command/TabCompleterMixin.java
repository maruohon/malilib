package malilib.mixin.command;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.TabCompleter;

import malilib.registry.Registry;
import malilib.render.text.TextRendererUtils;

@Mixin(TabCompleter.class)
public abstract class TabCompleterMixin
{
    @Inject(method = "requestCompletions(Ljava/lang/String;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void onRequestCompletions(String prefix, CallbackInfo ci)
    {
        if (prefix.length() >= 1)
        {
            Registry.CLIENT_COMMAND_HANDLER.autoComplete(prefix);
        }
    }

    @ModifyVariable(method = "setCompletions([Ljava/lang/String;)V",
            argsOnly = true,
            at = @At(
                value = "INVOKE",
                shift = Shift.AFTER,
                remap = false,
                target = "Ljava/util/List;clear()V"))
    private String[] addCompletionsAndRemoveFormattingCodes2(String[] newCompl)
    {
        String[] complete = Registry.CLIENT_COMMAND_HANDLER.latestAutoComplete;

        if (complete != null)
        {
            String[] result = new String[newCompl.length + complete.length];

            int i = 0;

            for (String str : complete)
            {
                result[i++] = TextRendererUtils.stripVanillaFormattingCodes(str);
            }

            for (String str : newCompl)
            {
                result[i++] = TextRendererUtils.stripVanillaFormattingCodes(str);
            }

            return result;
        }

        return newCompl;
    }

    @ModifyArg(method = "complete()V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/GuiTextField;writeText(Ljava/lang/String;)V"
            ))
    private String removeFormattingCodes2(String text)
    {
        return TextRendererUtils.stripVanillaFormattingCodes(text);
    }
}
