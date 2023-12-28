package malilib.gui.edit;

import java.util.Map;
import javax.annotation.Nullable;

import malilib.action.ActionContext;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.DualTextInputScreen;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.TranslationOverrideEntryWidget;
import malilib.input.ActionResult;
import malilib.registry.Registry;

public class TranslationOverridesListScreen extends BaseListScreen<DataListWidget<Map.Entry<String, String>>>
{
    protected final GenericButton addOverrideButton;

    public TranslationOverridesListScreen()
    {
        super(10, 52, 20, 64);

        this.setTitle("malilib.title.screen.translation_overrides.list_screen");

        String key = "malilib.button.translation_overrides.add_override";
        this.addOverrideButton = GenericButton.create(key, this::openAddOverrideScreen);

        this.addPreScreenCloseListener(Registry.TRANSLATION_OVERRIDE_MANAGER::saveToFileIfDirty);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addOverrideButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.addOverrideButton.setPosition(this.x + 10, this.y + 28);
    }

    @Nullable
    @Override
    protected DataListWidget<Map.Entry<String, String>> createListWidget()
    {
        DataListWidget<Map.Entry<String, String>> listWidget
                = new DataListWidget<>(Registry.TRANSLATION_OVERRIDE_MANAGER::getAllOverrides, true);

        listWidget.setListEntryWidgetFixedHeight(18);
        listWidget.setDataListEntryWidgetFactory(TranslationOverrideEntryWidget::new);

        return listWidget;
    }

    public boolean addOverride(String translationKey, String override)
    {
        Registry.TRANSLATION_OVERRIDE_MANAGER.addOverride(translationKey, override);
        this.getListWidget().refreshEntries();
        return true;
    }

    protected void openAddOverrideScreen()
    {
        DualTextInputScreen screen = new DualTextInputScreen("malilib.title.screen.translation_overrides.add_new",
                                                             "", "", this::addOverride);
        screen.setScreenWidth(300);
        screen.setInfoText("malilib.label.translation_overrides.add_override_for_key");
        screen.setLabelText("malilib.label.translation_overrides.translation_key");
        screen.setLabelText2("malilib.label.translation_overrides.translation_override");
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
    }

    public static ActionResult openTranslationOverridesListScreenAction(ActionContext ctx)
    {
        BaseScreen.openScreen(new TranslationOverridesListScreen());
        return ActionResult.SUCCESS;
    }
}
