package malilib.render.overlay;

import malilib.render.buffer.VertexBuilder;
import malilib.render.buffer.VertexFormat;

public abstract class BaseRenderObject
{
    protected final VertexFormat vertexFormat;
    protected final boolean hasTexture;
    protected final int glMode;

    public BaseRenderObject(int glMode, VertexFormat vertexFormat)
    {
        this.glMode = glMode;
        this.vertexFormat = vertexFormat;
        this.hasTexture = this.vertexFormat.hasTexture();
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
     * Uploads the given VertexBuilder to the VBO or Render List
     * @param builder the VertexBuilder to upload
     */
    public abstract void uploadData(VertexBuilder builder);

    /**
     * Draws the VBO or Render List to the screen
     */
    public abstract void draw();

    /**
     * De-allocates the VBO or Render List
     */
    public abstract void deleteGlResources();
}
