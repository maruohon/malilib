package malilib.render.overlay;

import java.util.function.Supplier;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.ShaderInstance;

public abstract class BaseRenderObject
{
    protected final VertexFormat.Mode glMode;
    protected final Supplier<ShaderInstance> shader;

    public BaseRenderObject(VertexFormat.Mode glMode, Supplier<ShaderInstance> shader)
    {
        this.glMode = glMode;
        this.shader = shader;
    }

    public VertexFormat.Mode getGlMode()
    {
        return this.glMode;
    }

    public Supplier<ShaderInstance> getShader()
    {
        return this.shader;
    }

    /*
    public BaseRenderObject(int glMode, VertexFormat vertexFormat)
    {
        this.glMode = glMode;
        this.vertexFormat = vertexFormat;
        this.hasTexture = this.vertexFormat.getElements().stream().anyMatch((el) -> el.getType() == VertexFormatElement.Type.UV);
    }

    public BaseRenderObject(int glMode, VertexFormat vertexFormat, boolean usesTexture)
    {
        this.glMode = glMode;
        this.vertexFormat = vertexFormat;
        this.hasTexture = usesTexture;
    }

    public int getGlMode()
    {
        return this.glMode;
    }

    public VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }
    */

    /**
     * Uploads the given BufferBuilder to the VBO
     */
    public abstract void uploadData(BufferBuilder buffer);

    /**
     * Draws the VBO to the screen
     */
    public abstract void draw(PoseStack matrixStack, Matrix4f projMatrix);

    /**
     * De-allocates the VBO
     */
    public abstract void deleteGlResources();
}
