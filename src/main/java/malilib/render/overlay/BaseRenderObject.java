package malilib.render.overlay;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public abstract class BaseRenderObject
{
    protected final VertexFormat vertexFormat;
    protected final boolean hasTexture;
    protected final int glMode;

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

    /**
     * Uploads the given BufferBuilder to the VBO or Render List
     * @param buffer the BufferBuilder to upload
     */
    public abstract void uploadData(BufferBuilder buffer);

    /**
     * Draws the VBO or Render List to the screen
     */
    public abstract void draw();

    /**
     * De-allocates the VBO or Render List
     */
    public abstract void deleteGlResources();
}
