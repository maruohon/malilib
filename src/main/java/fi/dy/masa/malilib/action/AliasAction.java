package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.util.StringUtils;

public class AliasAction extends NamedAction
{
    protected final String alias;
    //protected final String aliasRegistryName;

    public AliasAction(String alias, NamedAction action)
    {
        super(action.mod, action.name, action.registryName, action.translationKey, action.action);

        this.alias = alias;
        //this.aliasRegistryName = "alias:" + alias;
    }

    @Override
    public String getName()
    {
        return this.alias;
    }

    @Override
    public String getRegistryName()
    {
        return this.alias;
    }

    public String getOriginalName()
    {
        return super.getName();
    }

    public String getOriginalRegistryName()
    {
        return super.getRegistryName();
    }

    @Override
    public String getWidgetDisplayName()
    {
        String alias = this.getName();
        String originalName = this.getOriginalName();
        String modName = this.getMod().getModName();
        return StringUtils.translate("malilib.label.named_action_alias_entry_widget.name", alias, modName, originalName);
    }

    @Override
    public List<String> getHoverInfo()
    {
        List<String> list = new ArrayList<>();

        list.add(StringUtils.translate("malilib.hover_info.action.alias", this.alias));
        list.add(StringUtils.translate("malilib.hover_info.action.mod", this.getMod().getModName()));
        list.add(StringUtils.translate("malilib.hover_info.action.name", this.getOriginalName()));
        list.add(StringUtils.translate("malilib.hover_info.action.display_name", this.getDisplayName()));
        list.add(StringUtils.translate("malilib.hover_info.action.original_registry_name", this.getOriginalRegistryName()));

        return list;
    }
}
