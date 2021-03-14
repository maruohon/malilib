package fi.dy.masa.malilib.gui.widget;

import java.util.function.IntConsumer;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.ColorEditorHSVScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;

public class ColorIndicatorWidget extends InteractableWidget
{
    protected final IntegerConfig config;
    protected final IntConsumer valueConsumer;

    public ColorIndicatorWidget(int x, int y, int width, int height, Color4f color, IntConsumer consumer)
    {
        this(x, y, width, height, new IntegerConfig("", color.intValue), consumer);
    }

    public ColorIndicatorWidget(int x, int y, int width, int height, IntegerConfig config, IntConsumer consumer)
    {
        super(x, y, width, height);

        this.config = config;
        this.valueConsumer = consumer;
        String color = Color4f.getHexColorString(this.config.getIntegerValue());
        this.translateAndAddHoverString("malilib.gui.hover.open_color_editor", color);

        this.setClickListener(this::openColorEditorScreen);
    }

    protected void openColorEditorScreen()
    {
        if (this.config.isLocked() == false)
        {
            ColorEditorHSVScreen gui = new ColorEditorHSVScreen(this.config.getIntegerValue(), this.valueConsumer, null, GuiUtils.getCurrentScreen());
            BaseScreen.openPopupScreen(gui);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        int width = this.getWidth();
        int height = this.getHeight();

        ShapeRenderUtils.renderRectangle(x    , y    , z, width    , height    , 0xFFFFFFFF);
        ShapeRenderUtils.renderRectangle(x + 1, y + 1, z, width - 2, height - 2, 0xFF000000);
        ShapeRenderUtils.renderRectangle(x + 2, y + 2, z, width - 4, height - 4, 0xFF000000 | this.config.getIntegerValue());
    }
}
