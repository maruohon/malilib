package malilib.render;

import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

public class RenderContext
{
    public final PoseStack matrixStack;
    @Nullable public final Matrix4f projMatrix;

    public RenderContext(PoseStack matrixStack)
    {
        this(matrixStack, null);
    }

    public RenderContext(PoseStack matrixStack, @Nullable Matrix4f projMatrix)
    {
        this.matrixStack = matrixStack;
        this.projMatrix = projMatrix;
    }
}
