package malilib.render.overlay;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class DisplayListRenderObject extends BaseRenderObject
{
    protected static final WorldVertexBufferUploader VERTEX_UPLOADER = new WorldVertexBufferUploader();

    protected final int baseDisplayList;

    public DisplayListRenderObject(int glMode, VertexFormat vertexFormat)
    {
        super(glMode, vertexFormat);

        this.baseDisplayList = GLAllocation.generateDisplayLists(1);
    }

    @Override
    public void uploadData(BufferBuilder buffer)
    {
        GlStateManager.glNewList(this.baseDisplayList, GL11.GL_COMPILE);

        VERTEX_UPLOADER.draw(buffer);

        GlStateManager.glEndList();
    }

    @Override
    public void draw()
    {
        if (this.hasTexture)
        {
            GlStateManager.enableTexture2D();
        }
        else
        {
            GlStateManager.disableTexture2D();
        }

        GlStateManager.callList(this.baseDisplayList);

        if (this.hasTexture)
        {
            GlStateManager.disableTexture2D();
        }
    }

    @Override
    public void deleteGlResources()
    {
        GLAllocation.deleteDisplayLists(this.baseDisplayList, 1);
    }
}
