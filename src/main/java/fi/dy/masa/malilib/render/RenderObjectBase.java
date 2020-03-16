package fi.dy.masa.malilib.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class RenderObjectBase
{
    private final VertexFormat vertexFormat;
    private final int glMode;

    public RenderObjectBase(int glMode, VertexFormat format)
    {
        this.glMode = glMode;
        this.vertexFormat = format;
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
     * @param buffer
     */
    public abstract void uploadData(BufferBuilder buffer);

    /**
     * Draws the VBO or Render List
     */
    public abstract void draw();

    /**
     * De-allocates the VBO or Render List
     */
    public abstract void deleteGlResources();
}
