package malilib.render.buffer;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL15;

import malilib.util.game.wrap.RenderWrap;

public class VertexBuffer
{
    private final VertexFormat vertexFormat;
    private int glBufferId;
    private int vertexCount;

    public VertexBuffer(VertexFormat vertexFormat)
    {
        this.vertexFormat = vertexFormat;
        this.glBufferId = RenderWrap.glGenBuffers();
    }

    public void bindBuffer()
    {
        RenderWrap.glBindBuffer(RenderWrap.GL_ARRAY_BUFFER, this.glBufferId);
    }

    public void unbindBuffer()
    {
        RenderWrap.glBindBuffer(RenderWrap.GL_ARRAY_BUFFER, 0);
    }

    public void bufferData(ByteBuffer data)
    {
        this.bindBuffer();
        RenderWrap.glBufferData(RenderWrap.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        this.unbindBuffer();
        this.vertexCount = data.limit() / this.vertexFormat.getSize();
    }

    public void drawArrays(int mode)
    {
        RenderWrap.glDrawArrays(mode, 0, this.vertexCount);
    }

    public void deleteGlBuffers()
    {
        if (this.glBufferId >= 0)
        {
            RenderWrap.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }
}
