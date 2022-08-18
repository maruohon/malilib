package fi.dy.masa.malilib.render;

import net.minecraft.client.util.math.MatrixStack;

public class RenderContext
{
    public final MatrixStack matrixStack;

    public RenderContext(MatrixStack matrixStack)
    {
        this.matrixStack = matrixStack;
    }
}
