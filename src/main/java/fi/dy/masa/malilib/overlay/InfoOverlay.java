package fi.dy.masa.malilib.overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
    protected final List<InfoArea> activeInfoAreas = new ArrayList<>();
    protected boolean needsReFetch;

    public InfoArea getOrCreateInfoArea(ScreenLocation location)
    {
        return this.infoAreas.computeIfAbsent(location, (loc) -> new InfoArea(loc, this::notifyEnabledWidgetsChanged));
    }

    @Override
    public void onPostGameOverlayRender(Minecraft mc, float partialTicks)
    {
        if (mc.gameSettings.hideGUI == false)
        {
            this.render();
        }
    }

    @Override
    public void onClientTick(Minecraft mc)
    {
        this.tick();
    }

    /**
     * Notifies the InfoOverlay of a change in the set of enabled InfoRendererWidgets,
     * causing the enabled widgets to be fetched again.
     */
    public void notifyEnabledWidgetsChanged()
    {
        this.needsReFetch = true;
        //System.out.printf("InfoOverlay#notifyWidgetChange() - size: %d\n", this.enabledInfoWidgets.size());
    }

    protected void fetchEnabledWidgets()
    {
        this.enabledInfoWidgets.clear();
        this.activeInfoAreas.clear();

        for (InfoArea infoArea : this.infoAreas.values())
        {
            List<InfoRendererWidget> widgets = infoArea.getEnabledWidgets();

            if (widgets.isEmpty() == false)
            {
                this.enabledInfoWidgets.addAll(widgets);
                this.activeInfoAreas.add(infoArea);
            }
        }
        //System.out.printf("InfoOverlay#fetchEnabledWidgets() - size: %d\n", this.enabledInfoWidgets.size());
    }

    /**
     * Calls the InfoRendererWidget#updateState() method on all the currently enabled widgets.
     * Don't call this unless you have your own instance of the InfoOverlay,
     * ie. don't call this on InfoOverlay.INSTANCE
     */
    public void tick()
    {
        if (this.needsReFetch)
        {
            this.fetchEnabledWidgets();
            this.needsReFetch = false;
        }

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            // This allows the widgets to update their contents, which may also change their dimensions
            widget.updateState();
        }

        for (InfoArea infoArea : this.activeInfoAreas)
        {
            // This allows the InfoArea to re-layout its widgets
            infoArea.updateState();
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

    /**
     * Convenience method to find a matching widget at the given screen location
     * from the default InfoOverlay instance, or create and add a new widget if no matches are found.
     */
    public static <C extends InfoRendererWidget>
    C findOrCreateWidget(ScreenLocation location, Class<C> clazz, Predicate<C> validator, Supplier<C> factory)
    {
        InfoArea area = INSTANCE.getOrCreateInfoArea(location);
        C widget = area.findWidget(clazz, validator);

        if (widget == null)
        {
            widget = factory.get();
            area.addWidget(widget);
        }

        return widget;
    }
}
