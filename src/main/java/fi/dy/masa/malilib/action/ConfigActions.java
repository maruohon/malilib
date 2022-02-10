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

public class ConfigActions
{
    public static ParameterizedAction createSetBooleanValueAction(BooleanStorage config)
    {
        return (ctx, str) -> {
            try
            {
                str = str.toLowerCase(Locale.ROOT).trim();

                if (str.equals("true") || str.equals("on") || str.equals("yes"))
                {
                    config.setBooleanValue(true);
                    return ActionResult.SUCCESS;
                }
                else if (str.equals("false") || str.equals("off") || str.equals("no"))
                {
                    config.setBooleanValue(false);
                    return ActionResult.SUCCESS;
                }
            }
            catch (Exception ignore) {}

            return ActionResult.FAIL;
        };
    }

    public static ParameterizedAction createSetIntValueAction(IntegerStorage config)
    {
        return (ctx, str) -> {
            try
            {
                int value = Integer.parseInt(str.trim());
                config.setIntegerValue(value);
                return ActionResult.SUCCESS;
            }
            catch (Exception ignore) {}

            return ActionResult.FAIL;
        };
    }

    public static ParameterizedAction createSetDoubleValueAction(DoubleStorage config)
    {
        return (ctx, str) -> {
            try
            {
                double value = Double.parseDouble(str.trim());
                config.setDoubleValue(value);
                return ActionResult.SUCCESS;
            }
            catch (Exception ignore) {}

            return ActionResult.FAIL;
        };
    }

    public static ParameterizedAction createSetStringValueAction(StringConfig config)
    {
        return (ctx, str) -> {
            config.setValue(str);
            return ActionResult.SUCCESS;
        };
    }

    public static <T extends OptionListConfigValue>
    ParameterizedAction createSetOptionListValueAction(OptionListConfig<T> config)
    {
        return (ctx, str) -> {
            try
            {
                T value = BaseOptionListConfigValue.findValueByName(str.trim(), config.getAllValues(), null);

                if (value != null)
                {
                    config.setValue(value);
                    return ActionResult.SUCCESS;
                }
            }
            catch (Exception ignore) {}

            return ActionResult.FAIL;
        };
    }
}
