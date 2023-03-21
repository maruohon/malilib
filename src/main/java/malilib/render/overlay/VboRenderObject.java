package malilib.render.overlay;

import java.util.function.Supplier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.ShaderInstance;

public class VboRenderObject extends BaseRenderObject
{
    protected final VertexBuffer vertexBuffer;
    protected final boolean hasTexture;

    public VboRenderObject(VertexFormat.Mode glMode,
                           Supplier<ShaderInstance> shader,
                           //VertexFormat vertexFormat)
                           boolean hasTexture)
    {
        super(glMode, shader);

        this.vertexBuffer = new VertexBuffer();
        //this.hasTexture = vertexFormat.getElements().stream().anyMatch((el) -> el.getType() == VertexFormatElement.Type.UV);
        this.hasTexture = hasTexture;
    }

    @Override
    public void uploadData(BufferBuilder buffer)
    {
        BufferBuilder.BuiltBuffer renderBuffer = buffer.end();
        this.vertexBuffer.bind();
        this.vertexBuffer.upload(renderBuffer);
        VertexBuffer.unbind();
    }

    @Override
    public void draw(PoseStack matrixStack, Matrix4f projMatrix)
    {
        if (this.hasTexture)
        {
            RenderSystem.enableTexture();
        }

        RenderSystem.setShader(this.getShader());

        this.vertexBuffer.bind();
        this.vertexBuffer.drawWithShader(matrixStack.last().pose(), projMatrix, this.getShader().get());
        VertexBuffer.unbind();

        if (this.hasTexture)
        {
            RenderSystem.disableTexture();
        }
    }

    @Override
    public void deleteGlResources()
    {
        this.vertexBuffer.close();
    }
}
