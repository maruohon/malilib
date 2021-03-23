package fi.dy.masa.malilib.overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.ClientTickHandler;
import fi.dy.masa.malilib.event.PostGameOverlayRenderer;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.overlay.widget.StringListRendererWidget;

public class InfoOverlay implements PostGameOverlayRenderer, ClientTickHandler
{
    public static final InfoOverlay INSTANCE = new InfoOverlay();

    protected final HashMap<ScreenLocation, InfoArea> infoAreas = new HashMap<>();
    protected final List<InfoRendererWidget> enabledInfoWidgets = new ArrayList<>();
    protected boolean needsReFetch;

    public InfoArea getOrCreateInfoArea(ScreenLocation location)
    {
        return this.infoAreas.computeIfAbsent(location, (loc) -> new InfoArea(loc, this::notifyWidgetChange));
    }

    @Override
    public void onPostGameOverlayRender(Minecraft mc, float partialTicks)
    {
        this.render();
    }

    @Override
    public void onClientTick(Minecraft mc)
    {
        this.tick(mc);
    }

    /**
     * Notifies the InfoOverlay of a change in the set of enabled InfoRendererWidgets,
     * causing the enabled widgets to be fetched again.
     */
    public void notifyWidgetChange()
    {
        this.needsReFetch = true;
        //System.out.printf("InfoOverlay#notifyWidgetChange() - size: %d\n", this.enabledInfoWidgets.size());
    }

    protected void fetchEnabledWidgets()
    {
        this.enabledInfoWidgets.clear();

        for (InfoArea infoArea : this.infoAreas.values())
        {
            this.enabledInfoWidgets.addAll(infoArea.getEnabledWidgets());
        }
        //System.out.printf("InfoOverlay#fetchEnabledWidgets() - size: %d\n", this.enabledInfoWidgets.size());
    }

    /**
     * Calls the InfoRendererWidget#updateState() method on all the currently enabled widgets.
     * Don't call this unless you have your own instance of the InfoOverlay,
     * ie. don't call this on InfoOverlay.INSTANCE
     * @param mc
     */
    public void tick(Minecraft mc)
    {
        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            widget.updateState(mc);
        }

        if (this.needsReFetch)
        {
            this.fetchEnabledWidgets();
            this.needsReFetch = false;
        }
    }

    /**
     * Renders all the currently enabled widgets.
     * Don't call this unless you have your own instance of the InfoOverlay,
     * ie. don't call this on InfoOverlay.INSTANCE
     */
    public void render()
    {
        if (MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue())
        {
            for (InfoArea area : this.infoAreas.values())
            {
                area.renderDebug();
            }
        }

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            widget.render();
        }

        if (MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue())
        {
            BaseWidget.renderDebugTextAndClear();
        }
    }

    /**
     * Convenience method to get or create a text hud at the given screen location,
     * from the default InfoOverlay instance.
     */
    public static StringListRendererWidget getTextHud(ScreenLocation location)
    {
        InfoArea area = INSTANCE.getOrCreateInfoArea(location);
        StringListRendererWidget widget = area.findWidget(StringListRendererWidget.class, (w) -> true);

        if (widget == null)
        {
            widget = new StringListRendererWidget();
            area.addWidget(widget);
        }

        return widget;
    }
}
