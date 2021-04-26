package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.gui.CustomHotkeysEditScreen;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;

public class CustomHotkeyDefinitionEntryWidget extends BaseDataListEntryWidget<CustomHotkeyDefinition>
{
    protected final CustomHotkeysEditScreen screen;
    protected final LabelWidget nameLabelWidget;
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;
    protected final GenericButton removeButton;

    public CustomHotkeyDefinitionEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                             @Nullable CustomHotkeyDefinition data,
                                             @Nullable DataListWidget<? extends CustomHotkeyDefinition> listWidget,
                                             CustomHotkeysEditScreen screen)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.screen = screen;

        StyledTextLine name = StyledTextLine.translate("malilib.gui.label.custom_hotkey_name", data.getName());
        TextStyle actionStyle = TextStyle.normal(0xFFC0C0C0);
        StyledTextLine actionName = data.getAction().getWidgetDisplayName().withStartingStyle(actionStyle);

        this.nameLabelWidget = new LabelWidget(x, y, -1, height, 0xFFF0F0F0);
        this.nameLabelWidget.getPadding().setTop(2).setLeft(4);
        this.nameLabelWidget.setStyledTextLines(Lists.newArrayList(name, actionName));

        this.keybindButton = new KeyBindConfigButton(x, y, 120, 20, data.getKeyBind(), screen);
        this.settingsWidget = new KeybindSettingsWidget(x, y, 20, 20, data.getKeyBind(), data.getName(), null);

        this.removeButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.label.remove");
        this.removeButton.setActionListener(this::removeHotkey);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.keybindButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.removeButton);

        this.keybindButton.updateHoverStrings();
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int midY = y + this.getHeight() / 2;

        this.nameLabelWidget.setPosition(x, y);

        x = this.getRight() - this.removeButton.getWidth() - 2;
        this.removeButton.setPosition(x, midY - this.removeButton.getHeight() / 2);

        x = this.removeButton.getX() - this.settingsWidget.getWidth() - 4;
        this.settingsWidget.setPosition(x, midY - this.settingsWidget.getHeight() / 2);

        x = this.settingsWidget.getX() - this.keybindButton.getWidth() - 4;
        this.keybindButton.setPosition(x, midY - this.keybindButton.getHeight() / 2);
    }

    protected void removeHotkey()
    {
        this.scheduleTask(() -> {
            CustomHotkeyManager.INSTANCE.removeCustomHotkey(this.data);
            this.screen.getListWidget().refreshEntries();
        });
    }
}
