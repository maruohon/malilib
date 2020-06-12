package fi.dy.masa.malilib.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RenderObjectVbo extends RenderObjectBase
{
    public static final VertexBufferUploader VERTEX_UPLOADER = new VertexBufferUploader();

    protected final VertexBuffer vertexBuffer;
    protected final IArrayPointerSetter arrayPointerSetter;

    public RenderObjectVbo(int glMode, VertexFormat vertexFormat, IArrayPointerSetter arrayPointerSetter)
    {
        super(glMode, vertexFormat);

        this.vertexBuffer = new VertexBuffer(vertexFormat);
        this.arrayPointerSetter = arrayPointerSetter;
    }

    @Override
    public void uploadData(BufferBuilder buffer)
    {
        VERTEX_UPLOADER.setVertexBuffer(this.vertexBuffer);
        VERTEX_UPLOADER.draw(buffer);
    }

    @Override
    public void draw()
    {
        if (this.hasTexture)
        {
            GlStateManager.enableTexture2D();

            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
        else
        {
            GlStateManager.disableTexture2D();
        }

        this.vertexBuffer.bindBuffer();
        this.arrayPointerSetter.setupArrayPointers();
        this.vertexBuffer.drawArrays(this.getGlMode());

        if (this.hasTexture)
        {
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
    }

    @Override
    public void deleteGlResources()
    {
        this.vertexBuffer.deleteGlBuffers();
    }

    public static void setupArrayPointersPosColor()
    {
        GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 16, 0);
        GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 16, 12);
    }

    public static void setupArrayPointersPosUvColor()
    {
        GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 24, 0);
        GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, 24, 12);
        GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 24, 20);
    }

    public interface IArrayPointerSetter
    {
        void setupArrayPointers();
    }
}
