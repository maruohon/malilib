package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import fi.dy.masa.malilib.gui.position.ScreenLocation;

public class InfoOverlay
{
    public static final InfoOverlay INSTANCE = new InfoOverlay();

    protected final HashMap<ScreenLocation, InfoArea> infoAreas = new HashMap<>();
    protected final List<InfoRendererWidget> enabledInfoWidgets = new ArrayList<>();
    protected boolean needsReLayout;

    public InfoArea getOrCreateInfoArea(ScreenLocation location)
    {
        return this.infoAreas.computeIfAbsent(location, (loc) -> new InfoArea(loc, this::requestReLayout));
    }

    protected void requestReLayout()
    {
        this.needsReLayout = true;
    }

    protected void reLayoutWidgets()
    {
        this.enabledInfoWidgets.clear();

        for (InfoArea infoArea : this.infoAreas.values())
        {
            this.enabledInfoWidgets.addAll(infoArea.getEnabledWidgets());
        }
    }

    public void render()
    {
        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            widget.updateState();
        }

        if (this.needsReLayout)
        {
            this.reLayoutWidgets();
            this.needsReLayout = false;
        }

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            widget.render();
        }
    }

    /**
     * Convenience method to get or create a text hud at the given screen location
     * @param location
     * @return
     */
    public static StringListRendererWidget getTextHud(ScreenLocation location)
    {
        return INSTANCE.getOrCreateInfoArea(location).getOrCreateInfoWidget("text_hud", StringListRendererWidget.class, StringListRendererWidget::new);
    }
}
