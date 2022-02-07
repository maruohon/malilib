package fi.dy.masa.malilib.action;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.StringUtils;

public enum ActionGroup
{
    ALL             ("malilib.name.action_type.all",             Registry.ACTION_REGISTRY::getAllActions),
    BASE            ("malilib.name.action_type.base",            Registry.ACTION_REGISTRY::getBaseActions),
    ALIAS           ("malilib.name.action_type.alias",           Registry.ACTION_REGISTRY::getAliases),
    MACRO           ("malilib.name.action_type.macro",           Registry.ACTION_REGISTRY::getMacros),
    PARAMETERIZED   ("malilib.name.action_type.parameterized",   Registry.ACTION_REGISTRY::getParameterizedActions),
    PARAMETERIZABLE ("malilib.name.action_type.parameterizable", ActionUtils::getParameterizableActions),
    USER_ADDED      ("malilib.name.action_type.user_added",      ActionUtils::getUserAddedActions),
    SIMPLE          ("malilib.name.action_type.simple",          ActionUtils::getSimpleActions);

    public static final ImmutableList<ActionGroup> VALUES = ImmutableList.copyOf(values());
    public static final ImmutableList<ActionGroup> VALUES_USER_ADDED = ImmutableList.of(ALIAS, MACRO, PARAMETERIZED, USER_ADDED);

    private final Supplier<List<? extends NamedAction>> listSource;
    private final String translationKey;

    ActionGroup(String translationKey, Supplier<List<? extends NamedAction>> listSource)
    {
        this.translationKey = translationKey;
        this.listSource = listSource;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    public ImmutableList<NamedAction> getActions()
    {
        return ImmutableList.copyOf(this.listSource.get());
    }
}
