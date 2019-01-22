package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldWrapper;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.wrappers.ButtonWrapper;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

public abstract class WidgetConfigOptionBase extends WidgetBase
{
    protected final MinecraftClient mc;
    protected final List<WidgetBase> widgets = new ArrayList<>();
    protected final List<ButtonWrapper<? extends ButtonBase>> buttons = new ArrayList<>();
    protected final WidgetListConfigOptionsBase<?, ?> parent;
    @Nullable protected GuiTextFieldWrapper textField = null;
    @Nullable protected String initialStringValue;
    protected int maxTextfieldTextLength = 256;
    /**
     * The last applied value for any textfield-based configs.
     * Button based (boolean, option-list) values get applied immediately upon clicking the button.
     */
    protected String lastAppliedValue;

    public WidgetConfigOptionBase(int x, int y, int width, int height, float zLevel, MinecraftClient mc, WidgetListConfigOptionsBase<?, ?> parent)
    {
        super(x, y, width, height, zLevel);

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

    protected <T extends ButtonBase> ButtonWrapper<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonWrapper<T> entry = new ButtonWrapper<>(button, listener);
        this.buttons.add(entry);
        return entry;
    }

    protected TextFieldWidget createTextField(int id, int x, int y, int width, int height)
    {
        return new TextFieldWidget(id, this.mc.fontRenderer, x + 2, y, width, height);
    }

    protected void addTextField(TextFieldWidget field, ConfigOptionChangeListenerTextField listener)
    {
        GuiTextFieldWrapper wrapper = new GuiTextFieldWrapper(field, listener);
        this.textField = wrapper;
        this.parent.addTextField(wrapper);
    }

    protected ButtonGeneric createResetButton(int id, int x, int y, IConfigResettable config)
    {
        String labelReset = I18n.translate("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(labelReset) + 10;

        ButtonGeneric resetButton = new ButtonGeneric(id, x, y, w, 20, labelReset);
        resetButton.enabled = config.isModified();

        return resetButton;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
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
    public boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers)
    {
        if (this.textField != null)
        {
            if (keyCode == KeyCodes.KEY_ENTER)
            {
                this.applyNewValueToConfig();
            }
            else
            {
                return this.textField.onKeyTyped(keyCode, scanCode, modifiers);
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(char charIn, int modifiers)
    {
        if (this.textField != null && this.textField.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.onCharTypedImpl(charIn, modifiers);
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void drawTextFields(int mouseX, int mouseY)
    {
        if (this.textField != null)
        {
            this.textField.getTextField().render(mouseX, mouseY, 0f);
        }
    }
}
