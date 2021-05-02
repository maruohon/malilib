package fi.dy.masa.malilib.gui.action;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.CheckBoxWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.StringUtils;

public class AddActionExecutionWidgetScreen extends BaseScreen
{
    protected final Consumer<ActionExecutionWidget> widgetConsumer;
    protected final DropDownListWidget<NamedAction> dropDownWidget;
    protected final LabelWidget actionLabelWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget argumentLabelWidget;
    protected final CheckBoxWidget addArgumentCheckbox;
    protected final BaseTextFieldWidget nameTextField;
    protected final BaseTextFieldWidget argumentTextField;
    protected final GenericButton addButton;
    protected final GenericButton cancelButton;

    public AddActionExecutionWidgetScreen(Consumer<ActionExecutionWidget> widgetConsumer)
    {
        this.widgetConsumer = widgetConsumer;

        this.title = StringUtils.translate("malilib.gui.title.create_action_execution_widget");
        this.useTitleHierarchy = false;

        this.actionLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.action.colon");
        this.nameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.argumentLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.argument.colon");

        this.dropDownWidget = new DropDownListWidget<>(0, 0, 160, 16, 240, 20,
                                                       ActionRegistry.INSTANCE.getAllActions(),
                                                       NamedAction::getDisplayName, null);
        this.dropDownWidget.setSelectionListener(this::onActionSelected);

        this.addArgumentCheckbox = new CheckBoxWidget(0, 0, "malilib.label.add_action_execution_widget.add_argument",
                                                      "malilib.hover_info.add_action_execution_widget.add_argument");

        this.nameTextField = new BaseTextFieldWidget(0, 0, 140, 16, "");
        this.argumentTextField = new BaseTextFieldWidget(0, 0, 160, 16, "");

        this.addButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.add");
        this.addButton.setActionListener(this::createActionWidget);

        this.cancelButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.cancel");
        this.cancelButton.setActionListener(this::cancel);

        this.backgroundColor = 0xFF101010;
        this.setScreenWidthAndHeight(240, 140);
        this.centerOnScreen();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;

        this.actionLabelWidget.setPosition(x, y + 4);
        this.dropDownWidget.setPosition(this.actionLabelWidget.getRight() + 6, y);

        y += 24;
        this.nameLabelWidget.setPosition(x, y + 4);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);

        y += 24;
        this.addArgumentCheckbox.setPosition(x, y);

        y += 12;
        this.argumentLabelWidget.setPosition(x, y + 4);
        this.argumentTextField.setPosition(this.argumentLabelWidget.getRight() + 6, y);

        y += 32;
        this.addButton.setPosition(x, y);
        this.cancelButton.setPosition(this.addButton.getRight() + 10, y);

        this.addWidget(this.actionLabelWidget);
        this.addWidget(this.dropDownWidget);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);

        this.addWidget(this.addButton);
        this.addWidget(this.cancelButton);

        /*
        NamedAction action = this.dropDownWidget.getSelectedEntry();

        if (action != null && action.getNeedsArguments())
        {
            this.addWidget(this.addArgumentCheckbox);
            this.addWidget(this.argumentLabelWidget);
            this.addWidget(this.argumentTextField);
        }
        */
    }

    protected void onActionSelected(@Nullable NamedAction action)
    {
        String name = action != null ? action.getDisplayName() : "";

        this.nameTextField.setText(name);
        this.argumentTextField.setText("");
        this.addArgumentCheckbox.setSelected(false);

        if (action != null && action.getNeedsArguments())
        {
            this.addWidget(this.addArgumentCheckbox);
            this.addWidget(this.argumentLabelWidget);
            this.addWidget(this.argumentTextField);
        }
        else
        {
            this.removeWidget(this.addArgumentCheckbox);
            this.removeWidget(this.argumentLabelWidget);
            this.removeWidget(this.argumentTextField);
        }
    }

    protected void createActionWidget()
    {
        NamedAction action = this.dropDownWidget.getSelectedEntry();

        if (action != null)
        {
            if (this.addArgumentCheckbox.isSelected() && action.getNeedsArguments())
            {
                action = action.createAlias(action.getName() + "_parameterized", this.argumentTextField.getText());
            }

            ActionExecutionWidget widget = new ActionExecutionWidget();
            widget.setAction(action);
            widget.setName(this.nameTextField.getText());

            this.widgetConsumer.accept(widget);
            this.closeScreen(true);
        }
    }

    protected void cancel()
    {
        this.closeScreen(true);
    }
}
