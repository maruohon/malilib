package fi.dy.masa.malilib.gui.widget;

import java.util.function.IntConsumer;
import fi.dy.masa.malilib.config.option.ConfigInteger;
import fi.dy.masa.malilib.config.option.IConfigInteger;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiColorEditorHSV;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetColorIndicator extends WidgetBase
{
    protected final IConfigInteger config;

    public WidgetColorIndicator(int x, int y, int width, int height, Color4f color, IntConsumer consumer)
    {
        this(x, y, width, height, new ConfigInteger("", color.intValue, ""));

        ((ConfigInteger) this.config).setValueChangeCallback((newValue, oldValue) -> consumer.accept(newValue) );
    }

    public WidgetColorIndicator(int x, int y, int width, int height, IConfigInteger config)
    {
        super(x, y, width, height);

        this.config = config;
        this.addHoverString(StringUtils.translate("malilib.gui.hover.open_color_editor"));
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        GuiColorEditorHSV gui = new GuiColorEditorHSV(this.config, null, GuiUtils.getCurrentScreen());
        GuiBase.openPopupGui(gui);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
        int width = this.getWidth();
        int height = this.getHeight();

        RenderUtils.drawRect(x    , y + 0, width    , height    , 0xFFFFFFFF, z);
        RenderUtils.drawRect(x + 1, y + 1, width - 2, height - 2, 0xFF000000, z);
        RenderUtils.drawRect(x + 2, y + 2, width - 4, height - 4, 0xFF000000 | this.config.getIntegerValue(), z);
    }
}
