package fi.dy.masa.malilib;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.ShutdownListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.gui.MaLiLibConfigPanel;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.reference.MaLiLibReference;
import net.minecraft.client.Minecraft;

public class LiteModMaLiLib implements Configurable, LiteMod, InitCompleteListener, ShutdownListener
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

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
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader)
    {
        InputEventHandler.getInstance().updateUsedKeys();
    }

    /*
    @Override
    public void onTick(Minecraft mc, float partialTicks, boolean inGame, boolean clock)
    {
        InputEventHandler.getInstance().tickKeybinds();
    }
    */

    @Override
    public void onShutDown()
    {
        ConfigManager.getInstance().saveAllConfigs();
    }
}
