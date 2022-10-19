package malilib.gui.widget.list.entry.action;

import java.util.function.BiFunction;
import malilib.action.NamedAction;
import malilib.action.ParameterizableNamedAction;
import malilib.action.ParameterizedNamedAction;
import malilib.gui.BaseScreen;
import malilib.gui.DualTextInputScreen;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.registry.Registry;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;
import malilib.util.StringUtils;
import malilib.util.data.LeftRight;
import malilib.util.data.ToBooleanFunction;

public class ParameterizableActionEntryWidget extends ActionListBaseActionEntryWidget
{
    protected final GenericButton parameterizeButton;
    protected ToBooleanFunction<ParameterizedNamedAction> parameterizedActionConsumer;

    public ParameterizableActionEntryWidget(NamedAction data,
                                            DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        StyledTextLine nameText = data.getColoredWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(nameText, this.getWidth() - 20, LeftRight.RIGHT, " ..."));

        this.parameterizedActionConsumer = Registry.ACTION_REGISTRY::addParameterizedAction;
        this.parameterizeButton = GenericButton.create(14, "malilib.button.action_list_screen_widget.parameterize",
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
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

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
        this.parameterizeButton.translateAndAddHoverString(translationKey);
    }

    protected void openParameterizationPrompt()
    {
        DualTextInputScreen screen = createParameterizationPrompt("", "", this::parameterizeAction);
        BaseScreen.openPopupScreen(screen);
    }

    public static DualTextInputScreen createParameterizationPrompt(String name, String arg,
                                                                   BiFunction<String, String, Boolean> consumer)
    {
        DualTextInputScreen screen = new DualTextInputScreen("malilib.title.screen.create_parameterized_action",
                                                             name, arg, consumer);

        String part1 = StringUtils.translate("malilib.info.action.create_parameterized_copy");
        String part2 = StringUtils.translate("malilib.info.action.action_name_and_arg_immutable");
        screen.setInfoText(StyledText.of(part1 + "\n\n" + part2));
        screen.setLabelText("malilib.label.actions.create_parameterized_action.action_name");
        screen.setLabelText2("malilib.label.actions.create_parameterized_action.argument");
        screen.setParent(GuiUtils.getCurrentScreen());

        return screen;
    }

    protected boolean parameterizeAction(String name, String arg)
    {
        ParameterizedNamedAction action = ((ParameterizableNamedAction) this.data).parameterize(name, arg);
        return this.parameterizedActionConsumer.applyAsBoolean(action);
    }
}
