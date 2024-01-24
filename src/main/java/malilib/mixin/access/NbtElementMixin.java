package malilib.mixin.access;

import java.io.DataOutput;
import java.io.IOException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtElement;

@Mixin(NbtElement.class)
public interface NbtElementMixin
{
    @Invoker("write")
    void malilib_invokeWrite(DataOutput output) throws IOException;
}
