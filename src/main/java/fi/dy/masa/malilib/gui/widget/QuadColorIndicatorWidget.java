package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.edit.EdgeIntEditScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.EdgeInt;

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
        this.translateAndAddHoverString("malilib.gui.hover.open_quad_color_editor",
                                        colorTop, colorBottom, colorLeft, colorRight);
    }

    protected void openColorEditorScreen()
    {
        String title = "malilib.gui.title.edit_edge_colors";
        String centerStr = "malilib.label.colors";
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.colorStorage, true, title, centerStr);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openPopupScreen(screen);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        int middleX = x + width / 2;
        int middleY = y + height / 2;

        ShapeRenderUtils.renderRectangle(x    , y    , z, width    , height    , 0xFFFFFFFF);
        ShapeRenderUtils.renderRectangle(x + 1, y + 1, z, width - 2, height - 2, 0xFF000000);

        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR, false);

        int x1 = x + 2;
        int y1 = y + 2;
        int x2 = x + width - 2;
        int y2 = y + height - 2;
        z += 0.125f;
        ShapeRenderUtils.renderTriangle(x2, y1, z, x1, y1, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getTop(), buffer);
        ShapeRenderUtils.renderTriangle(x2, y2, z, x2, y1, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getRight(), buffer);
        ShapeRenderUtils.renderTriangle(x1, y2, z, x2, y2, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getBottom(), buffer);
        ShapeRenderUtils.renderTriangle(x1, y1, z, x1, y2, z, middleX, middleY, z, 0xFF000000 | this.colorStorage.getLeft(), buffer);

        RenderUtils.drawBuffer();
    }
}
