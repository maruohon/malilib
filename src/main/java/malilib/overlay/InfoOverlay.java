package malilib.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import malilib.MaLiLibConfigs;
import malilib.config.value.ScreenLocation;
import malilib.event.ClientTickHandler;
import malilib.event.PostGameOverlayRenderer;
import malilib.event.PostScreenRenderer;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseWidget;
import malilib.overlay.widget.InfoRendererWidget;
import malilib.overlay.widget.StringListRendererWidget;
import malilib.registry.Registry;
import malilib.render.RenderUtils;
import malilib.util.game.wrap.GameUtils;
import net.minecraft.client.util.math.MatrixStack;

public class InfoOverlay implements PostGameOverlayRenderer, PostScreenRenderer, ClientTickHandler
{
    protected final HashMap<ScreenLocation, InfoArea> infoAreas = new HashMap<>();
    protected final List<InfoRendererWidget> enabledInGameWidgets = new ArrayList<>();
    protected final List<InfoRendererWidget> enabledGuiWidgets = new ArrayList<>();
    protected final List<InfoRendererWidget> allEnabledWidgets = new ArrayList<>();
    protected final List<InfoArea> activeInfoAreas = new ArrayList<>();
    protected boolean needsReFetch;

    public InfoArea getOrCreateInfoArea(ScreenLocation location)
    {
        return this.infoAreas.computeIfAbsent(location, (loc) -> new InfoArea(loc, this::notifyEnabledWidgetsChanged));
    }

    @Override
    public void onPostGameOverlayRender(MatrixStack matrices)
    {
        if (GameUtils.Options.hideGui() == false)
        {
            this.renderInGame(matrices);
        }
    }

    @Override
    public void onPostScreenRender(MatrixStack matrices, float tickDelta)
    {
        this.renderScreen(matrices);
    }

    @Override
    public void onClientTick()
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
        this.enabledInGameWidgets.clear();
        this.enabledGuiWidgets.clear();
        this.allEnabledWidgets.clear();
        this.activeInfoAreas.clear();

        for (InfoArea infoArea : this.infoAreas.values())
        {
            List<InfoRendererWidget> widgets = infoArea.getEnabledWidgets();

            if (widgets.isEmpty() == false)
            {
                for (InfoRendererWidget widget : widgets)
                {
                    this.allEnabledWidgets.add(widget);

                    if (widget.isVisibleInContext(OverlayRenderContext.GUI))
                    {
                        this.enabledGuiWidgets.add(widget);
                    }

                    if (widget.isVisibleInContext(OverlayRenderContext.INGAME))
                    {
                        this.enabledInGameWidgets.add(widget);
                    }
                }

                this.activeInfoAreas.add(infoArea);
            }
        }

        this.needsReFetch = false;
        //System.out.printf("InfoOverlay#fetchEnabledWidgets() - size: %d\n", this.enabledInfoWidgets.size());
    }

    /**
     * Calls the InfoRendererWidget#updateState() method on all the currently enabled widgets.
     * Don't call this unless you have your own instance of the InfoOverlay,
     * ie. don't call this on {@code Registry.INFO_OVERLAY}
     */
    public void tick()
    {
        if (this.needsReFetch)
        {
            this.fetchEnabledWidgets();
        }

        if (GuiUtils.getCurrentScreen() != null)
        {
            for (InfoRendererWidget widget : this.enabledGuiWidgets)
            {
                // This allows the widgets to update their contents, which may also change their dimensions
                widget.updateState();
            }
        }
        else
        {
            for (InfoRendererWidget widget : this.enabledInGameWidgets)
            {
                // This allows the widgets to update their contents, which may also change their dimensions
                widget.updateState();
            }
        }

        for (InfoArea infoArea : this.activeInfoAreas)
        {
            // This allows the InfoArea to re-layout its widgets
            infoArea.updateState();
        }
    }

    /**
     * Renders all the currently enabled widgets that are set to be rendered in the in-game context.
     * Don't call this unless you have your own instance of the InfoOverlay,
     * ie. don't call this on {@code Registry.INFO_OVERLAY}
     */
    public void renderInGame(MatrixStack matrices)
    {
        if (GameUtils.Options.hideGui() == false)
        {
            boolean isScreenOpen = GuiUtils.getCurrentScreen() != null;
            boolean debug = MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue();
            ScreenContext ctx = new ScreenContext(0, 0, -1, true, matrices);

            if (debug)
            {
                for (InfoArea area : this.infoAreas.values())
                {
                    area.renderDebug(ctx);
                }
            }

            for (InfoRendererWidget widget : this.enabledInGameWidgets)
            {
                if (widget.shouldRenderFromContext(OverlayRenderContext.INGAME, isScreenOpen))
                {
                    widget.render(ctx);
                }
            }

            if (debug)
            {
                BaseWidget.renderDebugTextAndClear(ctx);
            }
        }
    }

    /**
     * Renders all the currently enabled widgets that are set to be rendered in the gui context.
     * Don't call this unless you have your own instance of the InfoOverlay,
     * ie. don't call this on {@code Registry.INFO_OVERLAY}
     */
    public void renderScreen(MatrixStack matrices)
    {
        boolean isScreenOpen = GuiUtils.getCurrentScreen() != null;
        boolean debug = MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue();
        ScreenContext ctx = new ScreenContext(0, 0, -1, true, matrices);
        RenderUtils.disableItemLighting();

        if (debug)
        {
            for (InfoArea area : this.infoAreas.values())
            {
                area.renderDebug(ctx);
            }
        }

        for (InfoRendererWidget widget : this.enabledGuiWidgets)
        {
            if (widget.shouldRenderFromContext(OverlayRenderContext.GUI, isScreenOpen))
            {
                widget.render(ctx);
            }
        }

        if (debug)
        {
            BaseWidget.renderDebugTextAndClear(ctx);
        }
    }

    /**
     * Convenience method to get or create a text hud at the given screen location,
     * from the default InfoOverlay instance.
     */
    public static StringListRendererWidget getTextHud(ScreenLocation location)
    {
        InfoArea area = Registry.INFO_OVERLAY.getOrCreateInfoArea(location);
        StringListRendererWidget widget = area.findWidget(StringListRendererWidget.class, (w) -> true);

        if (widget == null)
        {
            widget = new StringListRendererWidget();
            area.addWidget(widget);
        }

        return widget;
    }

    /**
     * Returns the first widget that passes the test, if any
     */
    @Nullable
    public <C extends InfoRendererWidget> C findWidget(Class<C> clazz, Predicate<C> predicate)
    {
        return findWidget(clazz, predicate, this.allEnabledWidgets);
    }

    /**
     * Returns the first widget that passes the test, if any
     */
    @Nullable
    public static <C extends InfoRendererWidget> C findWidget(Class<C> clazz, Predicate<C> predicate,
                                                              Collection<InfoRendererWidget> collection)
    {
        for (InfoRendererWidget widget : collection)
        {
            if (clazz.isAssignableFrom(widget.getClass()))
            {
                C obj = clazz.cast(widget);

                if (predicate.test(obj))
                {
                    return obj;
                }
            }
        }

        return null;
    }

    /**
     * Convenience method to find a matching widget at the given screen location
     * from the default InfoOverlay instance, or create and add a new widget if no matches are found.
     */
    public static <C extends InfoRendererWidget>
    C findOrCreateWidget(ScreenLocation location, Class<C> clazz, Predicate<C> validator, Supplier<C> factory)
    {
        InfoArea area = Registry.INFO_OVERLAY.getOrCreateInfoArea(location);
        C widget = area.findWidget(clazz, validator);

        if (widget == null)
        {
            widget = factory.get();
            area.addWidget(widget);
        }

        return widget;
    }

    public enum OverlayRenderContext
    {
        INGAME,
        GUI,
        BOTH
    }
}
