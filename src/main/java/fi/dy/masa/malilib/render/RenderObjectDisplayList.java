package fi.dy.masa.malilib.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RenderObjectDisplayList extends RenderObjectBase
{
    protected static final WorldVertexBufferUploader VERTEX_UPLOADER = new WorldVertexBufferUploader();

    protected final int baseDisplayList;

    public RenderObjectDisplayList(int glMode)
    {
        this(glMode, DefaultVertexFormats.POSITION_COLOR);
    }

    public RenderObjectDisplayList(int glMode, VertexFormat format)
    {
        super(glMode, format);

        this.baseDisplayList = GLAllocation.generateDisplayLists(1);
    }

    @Override
    public void uploadData(BufferBuilder buffer)
    {
        GlStateManager.glNewList(this.baseDisplayList, GL11.GL_COMPILE);
        GlStateManager.pushMatrix();

        VERTEX_UPLOADER.draw(buffer);

        GlStateManager.popMatrix();
        GlStateManager.glEndList();
    }

    @Override
    public void draw()
    {
        GlStateManager.pushMatrix();
        GlStateManager.callList(this.baseDisplayList);
        GlStateManager.popMatrix();
    }

    @Override
    public void deleteGlResources()
    {
        GLAllocation.deleteDisplayLists(this.baseDisplayList, 1);
    }
}
