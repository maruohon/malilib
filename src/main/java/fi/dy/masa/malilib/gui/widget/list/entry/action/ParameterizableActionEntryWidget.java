package fi.dy.masa.malilib.gui.widget.list.entry.action;

import java.util.function.BiFunction;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizableNamedAction;
import fi.dy.masa.malilib.action.ParameterizedNamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DualTextInputScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;
import fi.dy.masa.malilib.util.data.ToBooleanFunction;

public class ParameterizableActionEntryWidget extends ActionListBaseActionEntryWidget
{
    protected final GenericButton parameterizeButton;
    protected ToBooleanFunction<ParameterizedNamedAction> parameterizedActionConsumer;

    public ParameterizableActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                            int originalListIndex, @Nullable NamedAction data,
                                            @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        StyledTextLine nameText = data.getColoredWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(nameText, width - 20, LeftRight.RIGHT, " ..."));

        this.parameterizedActionConsumer = Registry.ACTION_REGISTRY::addParameterizedAction;
        this.parameterizeButton = GenericButton.simple(14, "malilib.label.button.action_list_screen_widget.parameterize",
                                                       this::openParameterizationPrompt);

        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);
        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF00FF60);

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.parameterizeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        this.parameterizeButton.centerVerticallyInside(this);
        this.parameterizeButton.setRight(this.nextElementRight);
        this.nextElementRight = this.parameterizeButton.getX() - 2;
    }

    public void setParameterizedActionConsumer(ToBooleanFunction<ParameterizedNamedAction> actionConsumer)
    {
        this.parameterizedActionConsumer = actionConsumer;
    }

    public void setParameterizationButtonHoverText(String translationKey)
    {
        this.parameterizeButton.getHoverInfoFactory().removeAll();
        this.parameterizeButton.translateAndAddHoverStrings(translationKey);
    }

    protected void openParameterizationPrompt()
    {
        DualTextInputScreen screen = createParameterizationPrompt("", "", this::parameterizeAction);
        BaseScreen.openPopupScreen(screen);
    }

    public static DualTextInputScreen createParameterizationPrompt(String name, String arg,
                                                                   BiFunction<String, String, Boolean> consumer)
    {
        DualTextInputScreen screen = new DualTextInputScreen("malilib.gui.prompt.title.parameterize_action",
                                                             name, arg, consumer);

        String part1 = StringUtils.translate("malilib.info.action.create_parameterized_copy");
        String part2 = StringUtils.translate("malilib.info.action.action_name_and_arg_immutable");
        screen.setInfoText(StyledText.of(part1 + "\n\n" + part2));
        screen.setLabelText("malilib.label.action.parameterized_action_name");
        screen.setLabelText2("malilib.label.action.parameterized_action_argument");
        screen.setParent(GuiUtils.getCurrentScreen());

        return screen;
    }

    protected boolean parameterizeAction(String name, String arg)
    {
        ParameterizedNamedAction action = ((ParameterizableNamedAction) this.data).parameterize(name, arg);
        return this.parameterizedActionConsumer.applyAsBoolean(action);
    }
}
