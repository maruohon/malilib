package malilib.mixin.access;

import java.io.DataOutput;
import java.io.IOException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.Tag;

@Mixin(Tag.class)
public interface NbtElementMixin
{
    @Invoker("write")
    void invokeWrite(DataOutput output) throws IOException;
}
