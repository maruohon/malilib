package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public abstract class WidgetConfigOptionBase<TYPE> extends WidgetListEntryBase<TYPE>
{
    protected final Minecraft mc;
    protected final WidgetListConfigOptionsBase<?, ?> parent;
    @Nullable protected TextFieldWrapper<? extends GuiTextField> textField = null;
    @Nullable protected String initialStringValue;
    protected int maxTextfieldTextLength = 256;
    /**
     * The last applied value for any textfield-based configs.
     * Button based (boolean, option-list) values get applied immediately upon clicking the button.
     */
    protected String lastAppliedValue;

    public WidgetConfigOptionBase(int x, int y, int width, int height, float zLevel, Minecraft mc,
            WidgetListConfigOptionsBase<?, ?> parent, TYPE entry, int listIndex)
    {
        super(x, y, width, height, zLevel, entry, listIndex);

        this.mc = mc;
        this.parent = parent;
    }

    public abstract boolean wasConfigModified();

    public boolean hasPendingModifications()
    {
        if (this.textField != null)
        {
            return this.textField.getTextField().getText().equals(this.lastAppliedValue) == false;
        }

        return false;
    }

    public abstract void applyNewValueToConfig();

    protected GuiTextField createTextField(int id, int x, int y, int width, int height)
    {
        return new GuiTextField(id, this.mc.fontRenderer, x + 2, y, width, height);
    }

    protected void addTextField(GuiTextField field, ConfigOptionChangeListenerTextField listener)
    {
        TextFieldWrapper<? extends GuiTextField> wrapper = new TextFieldWrapper<>(field, listener);
        this.textField = wrapper;
        this.parent.addTextField(wrapper);
    }

    protected ButtonGeneric createResetButton(int id, int x, int y, IConfigResettable config)
    {
        String labelReset = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(labelReset) + 10;

        ButtonGeneric resetButton = new ButtonGeneric(id, x, y, w, 20, labelReset);
        resetButton.enabled = config.isModified();

        return resetButton;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClickedImpl(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        boolean ret = false;

        if (this.textField != null)
        {
            ret |= this.textField.getTextField().mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                ret |= widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        return ret;
    }

    @Override
    public boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.textField != null && this.textField.isFocused())
        {
            if (keyCode == Keyboard.KEY_RETURN)
            {
                this.applyNewValueToConfig();
                return true;
            }
            else
            {
                return this.textField.keyTyped(typedChar, keyCode);
            }
        }

        return false;
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    protected void drawTextFields(int mouseX, int mouseY)
    {
        if (this.textField != null)
        {
            this.textField.getTextField().drawTextBox();
        }
    }
}
