package malilib.render;

import javax.annotation.Nullable;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RenderContext
{
    public final MatrixStack matrixStack;
    @Nullable public final Matrix4f projMatrix;

    public RenderContext(MatrixStack matrixStack)
    {
        this(matrixStack, null);
    }

    public RenderContext(MatrixStack matrixStack, @Nullable Matrix4f projMatrix)
    {
        this.matrixStack = matrixStack;
        this.projMatrix = projMatrix;
    }
}
