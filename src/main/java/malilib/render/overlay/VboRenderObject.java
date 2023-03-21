package malilib.render.overlay;

import java.util.function.Supplier;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import malilib.listener.EventListener;

public class VboRenderObject extends BaseRenderObject
{
    protected final VertexBuffer vertexBuffer;
    protected final boolean hasTexture;

    public VboRenderObject(VertexFormat.DrawMode glMode,
                           Supplier<Shader> shader,
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
    public void draw(MatrixStack matrixStack, Matrix4f projMatrix)
    {
        if (this.hasTexture)
        {
            RenderSystem.enableTexture();
        }

        RenderSystem.setShader(this.getShader());

        this.vertexBuffer.bind();
        this.vertexBuffer.draw(matrixStack.peek().getPositionMatrix(), projMatrix, this.getShader().get());
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
