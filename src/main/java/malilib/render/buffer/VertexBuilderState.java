package malilib.render.buffer;

public class VertexBuilderState
{
    public final VertexFormat vertexFormat;
    public final int[] vertexData;
    public final int vertexCount;

    public VertexBuilderState(VertexFormat vertexFormat, int[] vertexData)
    {
        this.vertexFormat = vertexFormat;
        this.vertexData = vertexData;
        this.vertexCount = vertexData.length / (vertexFormat.getSize() >> 2);
    }

    public VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }

    public int getVertexCount()
    {
        return this.vertexCount;
    }

    public int[] getVertexData()
    {
        return this.vertexData;
    }
}
