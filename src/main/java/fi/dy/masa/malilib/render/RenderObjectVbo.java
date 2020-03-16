package fi.dy.masa.malilib.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RenderObjectVbo extends RenderObjectBase
{
    public static final VertexBufferUploader VERTEX_UPLOADER = new VertexBufferUploader();

    protected final VertexBuffer vertexBuffer;
    protected final IArrayPointerSetter arrayPointerSetter;

    public RenderObjectVbo(int glMode)
    {
        this(glMode, DefaultVertexFormats.POSITION_COLOR, RenderObjectVbo::setupArrayPointersPosColor);
    }

    public RenderObjectVbo(int glMode, VertexFormat format, IArrayPointerSetter arrayPointerSetter)
    {
        super(glMode, format);

        this.vertexBuffer = new VertexBuffer(format);
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
        GlStateManager.pushMatrix();

        this.vertexBuffer.bindBuffer();
        this.arrayPointerSetter.setupArrayPointers();
        this.vertexBuffer.drawArrays(this.getGlMode());

        GlStateManager.popMatrix();
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

    public static void setupArrayPointersPosColorUv()
    {
        GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 28, 0);
        GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12);
        GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public interface IArrayPointerSetter
    {
        void setupArrayPointers();
    }
}
