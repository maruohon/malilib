package fi.dy.masa.malilib;

import java.io.File;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.ShutdownListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.event.dispatch.InitializationDispatcherImpl;
import fi.dy.masa.malilib.registry.Registry;

public class LiteModMaLiLib implements Configurable, LiteMod, InitCompleteListener, ShutdownListener
{
    public LiteModMaLiLib()
    {
    }

    @Override
    public String getName()
    {
        return MaLiLibReference.MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return MaLiLibReference.MOD_VERSION;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass()
    {
        return MaLiLibConfigPanel.class;
    }

    @Override
    public void init(File configPath)
    {
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }

    @Override
    public void onInitCompleted(MinecraftClient minecraft, LiteLoader loader)
    {
        // Dispatch the init calls to all the registered handlers
        ((InitializationDispatcherImpl) Registry.INITIALIZATION_DISPATCHER).onGameInitDone();
    }

    @Override
    public void onShutDown()
    {
        ((ConfigManagerImpl) Registry.CONFIG_MANAGER).saveIfDirty();
    }
}
