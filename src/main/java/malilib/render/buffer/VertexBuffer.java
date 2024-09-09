package malilib.render.buffer;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL15;

import malilib.util.game.wrap.RenderWrap;

public class VertexBuffer
{
    protected final VertexFormat vertexFormat;
    protected int glBufferId = -1;
    protected int vertexCount;

    public VertexBuffer(VertexFormat vertexFormat)
    {
        this.vertexFormat = vertexFormat;
    }

    public boolean hasData()
    {
        return this.vertexCount > 0 && this.glBufferId > 0;
    }

    protected void allocateBufferIfNeeded()
    {
        if (this.glBufferId <= 0)
        {
            this.glBufferId = RenderWrap.glGenBuffers();
        }
    }

    public boolean bindBuffer()
    {
        if (this.glBufferId <= 0)
        {
            return false;
        }

        RenderWrap.bindBuffer(RenderWrap.GL_ARRAY_BUFFER, this.glBufferId);

        return true;
    }

    public VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }

    public void unbindBuffer()
    {
        RenderWrap.bindBuffer(RenderWrap.GL_ARRAY_BUFFER, 0);
    }

    public void bufferData(ByteBuffer data)
    {
        this.allocateBufferIfNeeded();

        if (this.glBufferId <= 0)
        {
            return;
        }

        this.bindBuffer();
        RenderWrap.bufferData(RenderWrap.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        this.unbindBuffer();
        this.vertexCount = data.limit() / this.vertexFormat.getSize();
        //System.out.printf("bufferData lim: %d, size: %d, buf id: %d\n", data.limit(), this.vertexFormat.getSize(), this.glBufferId);
    }

    public void drawArrays(int mode)
    {
        if (this.glBufferId <= 0 || this.vertexCount == 0)
        {
            return;
        }

        RenderWrap.glDrawArrays(mode, 0, this.vertexCount);
    }

    public void bindSetupDrawUnbind(int glMode)
    {
        if (this.bindBuffer())
        {
            this.vertexFormat.setupDraw();

            this.drawArrays(glMode);

            this.vertexFormat.disableAfterDraw();
            this.unbindBuffer();
        }
    }

    public void deleteGlBuffers()
    {
        if (this.glBufferId > 0)
        {
            RenderWrap.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
            this.vertexCount = 0;
        }
    }
}
