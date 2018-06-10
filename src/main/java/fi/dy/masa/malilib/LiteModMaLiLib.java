package fi.dy.masa.malilib;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mumfrey.liteloader.LiteMod;
import fi.dy.masa.malilib.reference.Reference;

public class LiteModMaLiLib implements LiteMod
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
    public void init(File configPath)
    {
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }
}
