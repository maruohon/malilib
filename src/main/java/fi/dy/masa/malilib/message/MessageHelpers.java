package fi.dy.masa.malilib.message;

import java.util.Locale;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageHelpers
{
    public static String getOnOff(boolean value, boolean capitalize)
    {
        String key;

        if (capitalize)
        {
            key = value ? "malilib.label.on.caps" : "malilib.label.off.caps";
        }
        else
        {
            key = value ? "malilib.label.on" : "malilib.label.off";
        }

        return StringUtils.translate(key);
    }

    public static String getOnOffColored(boolean value, boolean capitalize)
    {
        String key;

        if (capitalize)
        {
            key = value ? "malilib.label.colored.on.caps" : "malilib.label.colored.off.caps";
        }
        else
        {
            key = value ? "malilib.label.colored.on" : "malilib.label.colored.off";
        }

        return StringUtils.translate(key);
    }

    public static String getTrueFalse(boolean value, boolean capitalize)
    {
        String key = value ? "malilib.label.true" : "malilib.label.false";
        return translateAndCapitalize(key, capitalize);
    }

    public static String getTrueFalseColored(boolean value, boolean capitalize)
    {
        String key = value ? "malilib.label.colored.true" : "malilib.label.colored.false";
        return translateAndCapitalize(key, capitalize);
    }

    public static String getYesNo(boolean value, boolean capitalize)
    {
        String key = value ? "malilib.label.yes" : "malilib.label.no";
        return translateAndCapitalize(key, capitalize);
    }

    public static String getYesNoColored(boolean value, boolean capitalize)
    {
        String key = value ? "malilib.label.colored.yes" : "malilib.label.colored.no";
        return translateAndCapitalize(key, capitalize);
    }

    public static String translateAndCapitalize(String key, boolean capitalize)
    {
        String str = StringUtils.translate(key);
        return capitalize ? str.toUpperCase(Locale.ROOT) : str;
    }
}
