package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.config.option.CommonDescription;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class NamedAction extends CommonDescription
{
    protected final String registryName;

    public NamedAction(ModInfo mod, String name, String registryName, String translationKey)
    {
        super(name, mod);

        this.registryName = registryName;
        this.nameTranslationKey = translationKey;

        this.setCommentIfTranslationExists(mod.getModId(), name);
    }

    public abstract ActionResult execute(ActionContext ctx);

    public ActionResult execute()
    {
        return this.execute(ActionContext.COMMON);
    }

    public boolean needsArgument()
    {
        return false;
    }

    public String getRegistryName()
    {
        return this.registryName;
    }

    public List<String> getSearchString()
    {
        return ImmutableList.of(this.getName(), this.getDisplayName());
    }

    public StyledTextLine getWidgetDisplayName()
    {
        String name = this.getName();
        String modName = this.modInfo.getModName();
        return StyledTextLine.translate("malilib.label.named_action_entry_widget.name", name, modName);
    }

    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> list = new ArrayList<>();

        list.add(StyledTextLine.translate("malilib.hover_info.action.mod", this.modInfo.getModName()));
        list.add(StyledTextLine.translate("malilib.hover_info.action.name", this.name));
        list.add(StyledTextLine.translate("malilib.hover_info.action.display_name", this.getDisplayName()));
        list.add(StyledTextLine.translate("malilib.hover_info.action.registry_name", this.registryName));

        return list;
    }

    /**
     * Sets a comment translation key in the format "modid.action.comment.action_name",
     * if a translation exists for that key.
     */
    public void setCommentIfTranslationExists(String modId, String name)
    {
        String key = modId + ".action.comment." + name.toLowerCase(Locale.ROOT);
        String comment = StringUtils.translate(key);

        if (key.equals(comment) == false)
        {
            this.setCommentTranslationKey(key);
        }
    }
}
