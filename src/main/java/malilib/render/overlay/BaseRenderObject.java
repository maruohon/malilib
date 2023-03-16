package malilib.render.overlay;

import java.util.function.Supplier;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public abstract class BaseRenderObject
{
    protected final VertexFormat.DrawMode glMode;
    protected final Supplier<Shader> shader;

    public BaseRenderObject(VertexFormat.DrawMode glMode, Supplier<Shader> shader)
    {
        this.glMode = glMode;
        this.shader = shader;
    }

    public VertexFormat.DrawMode getGlMode()
    {
        return this.glMode;
    }

    public Supplier<Shader> getShader()
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
    public abstract void draw(MatrixStack matrixStack, Matrix4f projMatrix);

    /**
     * De-allocates the VBO
     */
    public abstract void deleteGlResources();
}
