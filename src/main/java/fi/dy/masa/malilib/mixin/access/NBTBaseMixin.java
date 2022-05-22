package fi.dy.masa.malilib.mixin.access;

import java.io.DataOutput;
import java.io.IOException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.nbt.NBTBase;

@Mixin(NBTBase.class)
public interface NBTBaseMixin
{
    @Invoker("write")
    void invokeWrite(DataOutput output) throws IOException;
}
