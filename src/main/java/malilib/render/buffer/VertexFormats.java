package malilib.render.buffer;

public class VertexFormats
{
    public static final VertexFormat POSITION = new VertexFormat(0, -1, -1, -1, 0);
    public static final VertexFormat POSITION_COLOR = new VertexFormat(0, -1, 12, -1, 0);
    public static final VertexFormat POSITION_TEX = new VertexFormat(0, 12, -1, -1, 0);
    /*
    public static final VertexFormat POSITION_NORMAL = new VertexFormat();
    */
    public static final VertexFormat POSITION_TEX_COLOR = new VertexFormat(0, 12, 20, -1, 0);
    /*
    public static final VertexFormat POSITION_TEX_NORMAL = new VertexFormat();
    public static final VertexFormat POSITION_TEX_LMAP_COLOR = new VertexFormat();
    public static final VertexFormat POSITION_TEX_COLOR_NORMAL = new VertexFormat();
    */

    public static final VertexFormat BLOCK = new VertexFormat(0, 16, 12, -1, 24, 0); // pos, color, uv, lightmap
    public static final VertexFormat ITEM = new VertexFormat(0, 16, 12, 24, 1); // pos, color, uv, normal
    /*
    public static final VertexFormat OLDMODEL_POSITION_TEX_NORMAL = new VertexFormat();
    public static final VertexFormat PARTICLE_POSITION_TEX_COLOR_LMAP = new VertexFormat();
    */
}
