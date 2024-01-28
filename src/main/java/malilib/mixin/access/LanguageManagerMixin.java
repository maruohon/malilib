package malilib.mixin.access;

import java.util.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.locale.LanguageManager;

@Mixin(LanguageManager.class)
public interface LanguageManagerMixin
{
    @Accessor("translations")
    Properties malilib_getProperties();
}
