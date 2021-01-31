package fi.dy.masa.malilib.input;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

public class CancelCondition extends BaseOptionListConfigValue
{
    public static final CancelCondition NEVER       = new CancelCondition("never",       "malilib.label.cancel_condition.never");
    public static final CancelCondition ALWAYS      = new CancelCondition("always",      "malilib.label.cancel_condition.always");
    public static final CancelCondition ON_SUCCESS  = new CancelCondition("on_success",  "malilib.label.cancel_condition.on_success");
    public static final CancelCondition ON_FAILURE  = new CancelCondition("on_failure",  "malilib.label.cancel_condition.on_failure");

    public static final ImmutableList<CancelCondition> VALUES = ImmutableList.of(NEVER, ALWAYS, ON_SUCCESS, ON_FAILURE);

    public CancelCondition(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
