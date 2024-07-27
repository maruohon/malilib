package malilib.render.overlay;

import org.lwjgl.opengl.GL11;

import malilib.render.buffer.VertexBuffer;
import malilib.render.buffer.VertexBuilder;
import malilib.render.buffer.VertexFormat;
import malilib.util.game.wrap.RenderWrap;

public class VboRenderObject extends BaseRenderObject
{
    protected final VertexBuffer vertexBuffer;
    protected final Runnable arrayPointerSetter;

    public VboRenderObject(int glMode, VertexFormat vertexFormat, Runnable arrayPointerSetter)
    {
        super(glMode, vertexFormat);

        this.vertexBuffer = new VertexBuffer(vertexFormat);
        this.arrayPointerSetter = arrayPointerSetter;
    }

    @Override
    public void uploadData(VertexBuilder builder)
    {
        builder.finishDrawing();
        builder.reset();
        this.vertexBuffer.bufferData(builder.getByteBuffer());
    }

    @Override
    public void draw()
    {
        if (this.hasTexture)
        {
            RenderWrap.enableTexture2D();

            RenderWrap.setClientActiveTexture(RenderWrap.DEFAULT_TEX_UNIT);
            RenderWrap.enableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
        else
        {
            RenderWrap.disableTexture2D();
        }

        this.vertexBuffer.bindBuffer();
        this.arrayPointerSetter.run();
        this.vertexBuffer.drawArrays(this.getGlMode());

        if (this.hasTexture)
        {
            RenderWrap.setClientActiveTexture(RenderWrap.DEFAULT_TEX_UNIT);
            RenderWrap.disableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
    }

    @Override
    public void deleteGlResources()
    {
        this.vertexBuffer.deleteGlBuffers();
    }

    public static void setupArrayPointersPosColor()
    {
        RenderWrap.vertexPointer(3, GL11.GL_FLOAT, 16, 0);
        RenderWrap.colorPointer(4, GL11.GL_UNSIGNED_BYTE, 16, 12);
    }

    public static void setupArrayPointersPosUvColor()
    {
        RenderWrap.vertexPointer(3, GL11.GL_FLOAT, 24, 0);
        RenderWrap.texCoordPointer(2, GL11.GL_FLOAT, 24, 12);
        RenderWrap.colorPointer(4, GL11.GL_UNSIGNED_BYTE, 24, 20);
    }
}
