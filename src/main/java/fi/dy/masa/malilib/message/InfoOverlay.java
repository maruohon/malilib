package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.ClientTickHandler;
import fi.dy.masa.malilib.event.PostGameOverlayRenderer;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.BaseWidget;

public class InfoOverlay implements PostGameOverlayRenderer, ClientTickHandler
{
    public static final InfoOverlay INSTANCE = new InfoOverlay();

    protected final HashMap<ScreenLocation, InfoArea> infoAreas = new HashMap<>();
    protected final List<InfoRendererWidget> enabledInfoWidgets = new ArrayList<>();
    protected boolean needsReFetch;

    public InfoArea getOrCreateInfoArea(ScreenLocation location)
    {
        return this.infoAreas.computeIfAbsent(location, (loc) -> new InfoArea(loc, this::notifyChange));
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
    public void notifyChange()
    {
        this.needsReFetch = true;
    }

    protected void fetchEnabledWidgets()
    {
        this.enabledInfoWidgets.clear();

        for (InfoArea infoArea : this.infoAreas.values())
        {
            this.enabledInfoWidgets.addAll(infoArea.getEnabledWidgets());
        }
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
     * @param location
     * @return
     */
    public static StringListRendererWidget getTextHud(ScreenLocation location)
    {
        InfoArea area = INSTANCE.getOrCreateInfoArea(location);
        String id = "text_hud";
        InfoRendererWidget widget = area.getOrCreateWidget(id, StringListRendererWidget::new);

        if ((widget instanceof StringListRendererWidget) == false)
        {
            widget = new StringListRendererWidget();
            area.putWidget(id, widget);
        }

        return (StringListRendererWidget) widget;
    }
}
