package malilib.mixin.fix;

import java.util.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.locale.LanguageManager;

import malilib.util.StringUtils;

// TODO remove this mixin if OSL gets fallback support for lower case lang file names
@Mixin(LanguageManager.class)
public class LanguageManagerMixin
{
    @Shadow private Properties translations;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void malilib_loadModTranslations(CallbackInfo ci)
    {
        StringUtils.loadLowerCaseLangFile(this.translations);
    }
}
