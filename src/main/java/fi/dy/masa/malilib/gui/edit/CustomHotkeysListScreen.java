package fi.dy.masa.malilib.gui.edit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.CustomHotkeyDefinitionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;

public class CustomHotkeysListScreen extends BaseListScreen<DataListWidget<CustomHotkeyDefinition>> implements KeybindEditingScreen
{
    protected final GenericButton addHotkeyButton;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;

    public CustomHotkeysListScreen()
    {
        super(10, 74, 20, 80, MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.title.screen.configs.custom_hotkeys", MaLiLibReference.MOD_VERSION);

        this.addHotkeyButton = GenericButton.create(16, "malilib.button.custom_hotkeys.add_hotkey", this::openAddHotkeyScreen);
        this.addHotkeyButton.translateAndAddHoverString("malilib.hover.button.custom_hotkeys.add_new_hotkey");
        this.screenCloseListener = CustomHotkeyManager.INSTANCE::checkIfDirtyAndSaveAndUpdate;
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addHotkeyButton);
        this.getListWidget().refreshEntries();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.addHotkeyButton.setPosition(this.x + 10, this.y + 57);
    }

    protected void openAddHotkeyScreen()
    {
        String title = "malilib.title.screen.custom_hotkey_create";
        TextInputScreen screen = new TextInputScreen(title, "", this::openEditHotkeyScreen);
        screen.setParent(this);
        screen.setLabelText("malilib.label.custom_hotkeys.create.hotkey_name");
        screen.setInfoText("malilib.info.custom_hotkey.name_immutable");
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openEditHotkeyScreen(String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_DEFAULT);
        CustomHotkeyDefinition hotkey = new CustomHotkeyDefinition(name, keyBind, ImmutableList.of());
        CustomHotkeyManager.INSTANCE.addCustomHotkey(hotkey);
        CustomHotkeyEditScreen screen = new CustomHotkeyEditScreen(hotkey);
        screen.setParent(this);
        BaseScreen.openScreen(screen);

        return true;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onKeyTyped(keyCode, scanCode, modifiers);
            return true;
        }
        else
        {
            if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
            {
                return true;
            }

            if (keyCode == Keyboard.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
            {
                this.openParentScreen();
                return true;
            }

            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeyBindButton != null && mouseButton == 0)
        {
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    protected List<CustomHotkeyDefinition> getCustomHotkeyDefinitions()
    {
        List<CustomHotkeyDefinition> hotkeys = new ArrayList<>(CustomHotkeyManager.INSTANCE.getAllCustomHotkeys());
        hotkeys.sort(Comparator.comparing(CustomHotkeyDefinition::getName));
        return hotkeys;
    }

    @Override
    protected DataListWidget<CustomHotkeyDefinition> createListWidget()
    {
        DataListWidget<CustomHotkeyDefinition> listWidget = new DataListWidget<>(this::getCustomHotkeyDefinitions, true);
        
        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setDataListEntryWidgetFactory(this::createWidget);

        return listWidget;
    }

    protected CustomHotkeyDefinitionEntryWidget createWidget(@Nullable CustomHotkeyDefinition data,
                                                             DataListEntryWidgetData constructData)
    {
        return new CustomHotkeyDefinitionEntryWidget(data, constructData, this);
    }

    @Override
    public void setActiveKeyBindButton(@Nullable KeyBindConfigButton button)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onClearSelection();
        }

        this.activeKeyBindButton = button;

        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onSelected();
        }
    }
}
