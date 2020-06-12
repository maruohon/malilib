package fi.dy.masa.malilib.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public abstract class RenderObjectBase
{
    private final VertexFormat vertexFormat;
    private final int glMode;
    protected final boolean hasTexture;

    public RenderObjectBase(int glMode, VertexFormat vertexFormat)
    {
        this.glMode = glMode;
        this.vertexFormat = vertexFormat;

        boolean hasTexture = false;

        // This isn't really that nice and clean, but it'll do for now...
        for (VertexFormatElement el : this.vertexFormat.getElements())
        {
            if (el.getUsage() == VertexFormatElement.EnumUsage.UV)
            {
                hasTexture = true;
                break;
            }
        }

        this.hasTexture = hasTexture;
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
