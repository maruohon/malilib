package fi.dy.masa.malilib.action;

import java.util.Locale;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.util.data.BooleanStorage;
import fi.dy.masa.malilib.util.data.DoubleStorage;
import fi.dy.masa.malilib.util.data.IntegerStorage;

public class ActionUtils
{
    public static ParameterizedAction setBooleanValue(BooleanStorage config)
    {
        return (ctx, str) -> {
            try
            {
                str = str.toLowerCase(Locale.ROOT).trim();
                boolean trueValue = str.equals("true") || str.equals("on") || str.equals("yes");
                boolean falseValue = str.equals("false") || str.equals("off") || str.equals("no");

                // Check that the input represents some kind of valid boolean-like value...
                if (trueValue || falseValue)
                {
                    config.setBooleanValue(trueValue);
                    return ActionResult.SUCCESS;
                }
            }
            catch (Exception ignore) {}

            return ActionResult.FAIL;
        };
    }

    public static ParameterizedAction setIntValue(IntegerStorage config)
    {
        return (ctx, str) -> {
            try
            {
                int value = Integer.parseInt(str.trim());
                config.setIntegerValue(value);
                return ActionResult.SUCCESS;
            }
            catch (Exception ignore)
            {
                return ActionResult.FAIL;
            }
        };
    }

    public static ParameterizedAction setDoubleValue(DoubleStorage config)
    {
        return (ctx, str) -> {
            try
            {
                double value = Double.parseDouble(str.trim());
                config.setDoubleValue(value);
                return ActionResult.SUCCESS;
            }
            catch (Exception ignore)
            {
                return ActionResult.FAIL;
            }
        };
    }

    public static ParameterizedAction setStringValue(StringConfig config)
    {
        return (ctx, str) -> {
            config.setValue(str);
            return ActionResult.SUCCESS;
        };
    }

    public static <T extends OptionListConfigValue> ParameterizedAction setOptionListValue(OptionListConfig<T> config)
    {
        return (ctx, str) -> {
            try
            {
                T value = BaseOptionListConfigValue.findValueByName(str.trim(), config.getAllValues());
                config.setValue(value);
                return ActionResult.SUCCESS;
            }
            catch (Exception ignore)
            {
                return ActionResult.FAIL;
            }
        };
    }

    public static ParameterizedAction vanillaCommand()
    {
        return (ctx, str) -> {
            if (ctx.getPlayer() != null)
            {
                ctx.getPlayer().sendChatMessage(str);
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        };
    }
}
