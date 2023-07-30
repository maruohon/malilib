package malilib.gui.widget;

import malilib.gui.BaseScreen;
import malilib.gui.edit.EdgeIntEditScreen;
import malilib.gui.util.ScreenContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Color4f;
import malilib.util.data.EdgeInt;

public class QuadColorIndicatorWidget extends InteractableWidget
{
    private final EdgeInt colorStorage;

    public QuadColorIndicatorWidget(int width, int height, EdgeInt colorStorage)
    {
        super(width, height);

        this.colorStorage = colorStorage;
        this.setClickListener(this::openColorEditorScreen);

        String colorTop    = Color4f.getHexColorString(colorStorage.getTop());
        String colorBottom = Color4f.getHexColorString(colorStorage.getBottom());
        String colorLeft   = Color4f.getHexColorString(colorStorage.getLeft());
        String colorRight  = Color4f.getHexColorString(colorStorage.getRight());
        this.translateAndAddHoverString("malilib.hover.config.open_quad_color_editor",
                                        colorTop, colorBottom, colorLeft, colorRight);
    }

    protected void openColorEditorScreen()
    {
        String title = "malilib.title.screen.edit_edge_colors";
        String centerStr = "malilib.label.misc.colors";
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.colorStorage, true, title, centerStr);
        BaseScreen.openPopupScreenWithCurrentScreenAsParent(screen);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        int middleX = x + width / 2;
        int middleY = y + height / 2;

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        ShapeRenderUtils.renderRectangle(x    , y    , z, width    , height    , 0xFFFFFFFF, builder);
        ShapeRenderUtils.renderRectangle(x + 1, y + 1, z, width - 2, height - 2, 0xFF000000, builder);
        builder.draw();


        int x1 = x + 2;
        int y1 = y + 2;
        int x2 = x + width - 2;
        int y2 = y + height - 2;
        z += 0.125f;

        builder = VanillaWrappingVertexBuilder.coloredTriangles();
        ShapeRenderUtils.renderTriangle(x2, y1, z, x1, y1, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getTop(), builder);
        ShapeRenderUtils.renderTriangle(x2, y2, z, x2, y1, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getRight(), builder);
        ShapeRenderUtils.renderTriangle(x1, y2, z, x2, y2, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getBottom(), builder);
        ShapeRenderUtils.renderTriangle(x1, y1, z, x1, y2, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getLeft(), builder);
        builder.draw();
    }
}
