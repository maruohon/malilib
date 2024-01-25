package malilib.mixin.access;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

@Mixin(NbtCompound.class)
public interface NbtCompoundMixin
{
    @Accessor("elements")
    Map<String, NbtElement> malilib_getTags();
}
