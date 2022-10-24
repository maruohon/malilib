package malilib.gui.config.liteloader;

import java.util.function.Supplier;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import malilib.gui.BaseScreen;

public abstract class RedirectingConfigPanel extends AbstractConfigPanel
{
    protected final Supplier<BaseScreen> configScreenFactory;

    public RedirectingConfigPanel(Supplier<BaseScreen> configScreenFactory)
    {
        this.configScreenFactory = configScreenFactory;
    }

    @Override
    public void onPanelHidden()
    {
        // NO-OP
    }

    @Override
    protected void addOptions(ConfigPanelHost host)
    {
        BaseScreen.openScreen(this.configScreenFactory.get());
    }

    @Override
    public String getPanelTitle()
    {
        return "<none>";
    }

    // This is mainly useful for the LiteFabric mod or related things
    public Supplier<BaseScreen> getConfigScreenFactory()
    {
        return this.configScreenFactory;
    }
}
