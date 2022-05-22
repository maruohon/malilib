package fi.dy.masa.malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.nbt.NBTTagLongArray;

@Mixin(NBTTagLongArray.class)
public interface NBTTagLongArrayMixin
{
    @Accessor("data")
    long[] getArray();
}
