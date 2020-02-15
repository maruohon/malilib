package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.options.IConfigResettable;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class WidgetConfigOptionBase<TYPE> extends WidgetListEntryBase<TYPE>
{
    protected final WidgetListConfigOptionsBase<?, ?> parent;
    @Nullable protected WidgetTextFieldBase textField = null;
    @Nullable protected String initialStringValue;
    /**
     * The last applied value for any textfield-based configs.
     * Button based (boolean, option-list) values get applied immediately upon clicking the button.
     */
    protected String lastAppliedValue;

    public WidgetConfigOptionBase(int x, int y, int width, int height,
            WidgetListConfigOptionsBase<?, ?> parent, TYPE entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);

        this.parent = parent;
    }

    public abstract boolean wasConfigModified();

    public boolean hasPendingModifications()
    {
        if (this.textField != null)
        {
            return this.textField.getText().equals(this.lastAppliedValue) == false;
        }

        return false;
    }

    public abstract void applyNewValueToConfig();

    protected ButtonGeneric createResetButton(int x, int y, IConfigResettable config)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(config.isModified());

        return resetButton;
    }

    @Override
    public boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_RETURN && this.textField != null && this.textField.isFocused())
        {
            this.applyNewValueToConfig();
            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    protected void drawTextFields(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.textField != null)
        {
            this.textField.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }
    }
}
