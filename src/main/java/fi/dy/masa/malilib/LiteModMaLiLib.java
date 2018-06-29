package fi.dy.masa.malilib;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import fi.dy.masa.malilib.config.gui.MaLiLibConfigPanel;
import fi.dy.masa.malilib.hotkeys.KeybindEventHandler;
import fi.dy.masa.malilib.reference.Reference;
import net.minecraft.client.Minecraft;

public class LiteModMaLiLib implements Configurable, LiteMod, InitCompleteListener, Tickable
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public LiteModMaLiLib()
    {
    }

    @Override
    public String getName()
    {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return Reference.MOD_VERSION;
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
        KeybindEventHandler.getInstance().updateUsedKeys();
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
    {
        KeybindEventHandler.getInstance().tickKeybinds();
    }
}
