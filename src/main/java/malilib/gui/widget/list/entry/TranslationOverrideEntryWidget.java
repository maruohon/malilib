package malilib.gui.widget.list.entry;

import java.util.Map;

import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.registry.Registry;

public class TranslationOverrideEntryWidget extends BaseDataListEntryWidget<Map.Entry<String, String>>
{
    protected final BaseTextFieldWidget langKeyTextField;
    protected final BaseTextFieldWidget overrideTextField;
    protected final GenericButton removeButton;

    public TranslationOverrideEntryWidget(Map.Entry<String, String> data, DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.langKeyTextField = new BaseTextFieldWidget(240, 16, data.getKey());
        this.langKeyTextField.setUpdateListenerAlways(false);
        this.langKeyTextField.setListener(this::replaceKey);

        this.overrideTextField = new BaseTextFieldWidget(280, 16, data.getValue());
        this.overrideTextField.setUpdateListenerAlways(false);
        this.overrideTextField.setListener(this::replaceOverride);

        this.removeButton = GenericButton.create(16, "malilib.button.misc.remove", this::removeOverride);

        this.addHoverStrings(data.getKey());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.langKeyTextField);
        this.addWidget(this.overrideTextField);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int y = this.getY() + 1;
        int availableWidth = this.getWidth() - this.removeButton.getWidth() - 20;

        this.langKeyTextField.setPosition(this.getX() + 4, y);
        this.langKeyTextField.setWidth(availableWidth / 2);

        this.removeButton.setRight(this.getRight() - 2);
        this.removeButton.setY(y);

        this.overrideTextField.setWidth(availableWidth / 2);
        this.overrideTextField.setRight(this.removeButton.getX() - 4);
        this.overrideTextField.setY(y);
    }

    protected void replaceKey(String key)
    {
        this.scheduleTask(() -> {
            Registry.TRANSLATION_OVERRIDE_MANAGER.removeOverride(this.data.getKey());
            Registry.TRANSLATION_OVERRIDE_MANAGER.addOverride(key, this.overrideTextField.getText());
            this.listWidget.refreshEntries();
        });
    }

    protected void replaceOverride(String override)
    {
        this.scheduleTask(() -> {
            String translationKey = this.data.getKey();
            Registry.TRANSLATION_OVERRIDE_MANAGER.addOverride(translationKey, override);
            this.listWidget.refreshEntries();
        });
    }

    protected void removeOverride()
    {
        this.scheduleTask(() -> {
            Registry.TRANSLATION_OVERRIDE_MANAGER.removeOverride(this.data.getKey());
            this.listWidget.refreshEntries();
        });
    }
}
