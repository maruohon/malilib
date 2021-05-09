package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class AddCustomHotkeyDefinitionScreen extends BaseScreen
{
    protected final BaseTextFieldWidget nameTextField;
    protected final DropDownListWidget<NamedAction> actionDropDownWidget;
    protected final LabelWidget actionLabelWidget;
    protected final LabelWidget nameLabelWidget;
    protected final GenericButton addButton;
    protected final GenericButton cancelButton;

    public AddCustomHotkeyDefinitionScreen()
    {
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;
        this.setTitle("malilib.gui.title.add_new_hotkey");

        this.nameLabelWidget = new LabelWidget(0, 0, -1, 12, 0xFFF0F0F0, "malilib.label.name.colon");
        this.actionLabelWidget = new LabelWidget(0, 0, -1, 12, 0xFFF0F0F0, "malilib.label.action.colon");

        this.nameTextField = new BaseTextFieldWidget(0, 0, 160, 16);
        this.addButton = new GenericButton(0, 0, 60, 20, "malilib.gui.button.add");
        this.addButton.setActionListener(this::onAddButtonClicked);

        this.cancelButton = new GenericButton(0, 0, 60, 20, "malilib.gui.button.cancel");
        this.cancelButton.setActionListener(this::onCancelButtonClicked);

        this.actionDropDownWidget = new DropDownListWidget<>(0, 0, -1, 16, 160, 10,
                                                             ActionRegistry.INSTANCE.getAllActions(),
                                                             NamedAction::getName, null);

        this.nameTextField.setFocused(true);

        this.setScreenWidthAndHeight(320, 120);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.actionLabelWidget);
        this.addWidget(this.actionDropDownWidget);
        this.addWidget(this.addButton);
        this.addWidget(this.cancelButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;

        this.nameLabelWidget.setPosition(x, y);
        y += 12;
        this.nameTextField.setPosition(x, y);

        y += 24;
        this.actionLabelWidget.setPosition(x, y);
        y += 12;
        this.actionDropDownWidget.setPosition(x, y);

        y += 22;
        this.addButton.setPosition(x, y);
        this.cancelButton.setPosition(this.addButton.getRight() + 6, y);
    }

    protected void onAddButtonClicked()
    {
        String name = this.nameTextField.getText();
        NamedAction action = this.actionDropDownWidget.getSelectedEntry();

        if (org.apache.commons.lang3.StringUtils.isBlank(name))
        {
            MessageUtils.error("malilib.message.error.custom_hotkey_add.give_name");
            return;
        }

        if (action == null)
        {
            MessageUtils.error("malilib.message.error.custom_hotkey_add.select_action");
            return;
        }

        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_SUCCESS);
        CustomHotkeyManager.INSTANCE.addCustomHotkey(new CustomHotkeyDefinition(name, keyBind, action));
        BaseScreen.openScreen(this.getParent());
    }

    protected void onCancelButtonClicked()
    {
        BaseScreen.openScreen(this.getParent());
    }
}
