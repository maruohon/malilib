package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class ActionPromptNamedActionEntryWidget extends BaseDataListEntryWidget<NamedAction>
{
    protected final StyledTextLine nameText;

    public ActionPromptNamedActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                              int originalListIndex, @Nullable NamedAction data,
                                              @Nullable DataListWidget<? extends NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.renderHoverBackground = false;
        this.borderEnabledHovered = true;
        this.setHoveredBorderColor(0xFF00C0C0);
        this.setHoveredBorderWidth(1);

        StyledTextLine fullName = StyledTextLine.of(String.format("%s §8(%s)", data.getName(), data.getMod().getModName()));
        this.nameText = StyledTextUtils.clampStyledTextToMaxWidth(fullName, width - 16, LeftRight.RIGHT, " ...");

        this.translateAndAddHoverString("malilib.action.hover_info.mod", data.getMod().getModName());
        this.translateAndAddHoverString("malilib.action.hover_info.name", data.getName());
        this.translateAndAddHoverString("malilib.action.hover_info.display_name", data.getDisplayName());
        this.translateAndAddHoverString("malilib.action.hover_info.registryname", data.getRegistryName());
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(x + 4, ty, z + 0.1f, 0xFFFFFFFF, true, this.nameText);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        BaseScreen.openScreen(null);
        this.data.getAction().execute(new ActionContext());
        return true;
    }
}
