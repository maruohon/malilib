package malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

public class MaLiLib implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Override
    public void init()
    {
    }

    public static boolean canShowCoordinates()
    {
        return MaLiLibConfigs.Generic.HIDE_ALL_COORDINATES.getBooleanValue() == false;
    }

    public static void debugLog(String str, Object... args)
    {
        if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
        {
            LOGGER.info(str, args);
        }
    }
}
