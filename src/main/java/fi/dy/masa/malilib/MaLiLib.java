package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaLiLib
{
    public static final Logger LOGGER = LogManager.getLogger(MaLiLibReference.MOD_ID);

    public static boolean canShowCoordinates()
    {
        return MaLiLibConfigs.Generic.HIDE_ALL_COORDINATES.getBooleanValue() == false;
    }
}
