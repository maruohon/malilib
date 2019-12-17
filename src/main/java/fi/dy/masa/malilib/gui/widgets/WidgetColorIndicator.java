package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.IConfigInteger;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiColorEditorHSV;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetColorIndicator extends WidgetBase
{
    protected final IConfigInteger config;
    protected List<String> hoverInfo = new ArrayList<>();

    public WidgetColorIndicator(int x, int y, int width, int height, Color4f color, IntConsumer consumer)
    {
        this(x, y, width, height, new ConfigInteger("", color.intValue, ""));

        ((ConfigInteger) this.config).setValueChangeCallback((cfg) -> consumer.accept(cfg.getIntegerValue()) );
    }

    public WidgetColorIndicator(int x, int y, int width, int height, IConfigInteger config)
    {
        super(x, y, width, height);

        this.config = config;
        this.hoverInfo.add(StringUtils.translate("malilib.gui.hover.open_color_editor"));
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        GuiColorEditorHSV gui = new GuiColorEditorHSV(this.config, null, GuiUtils.getCurrentScreen());
        GuiBase.openGui(gui);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        RenderUtils.drawRect(this.x    , this.y + 0, this.width    , this.height    , 0xFFFFFFFF);
        RenderUtils.drawRect(this.x + 1, this.y + 1, this.width - 2, this.height - 2, 0xFF000000);
        RenderUtils.drawRect(this.x + 2, this.y + 2, this.width - 4, this.height - 4, 0xFF000000 | this.config.getIntegerValue());
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        RenderUtils.drawHoverText(mouseX, mouseY, this.hoverInfo);
    }
}
