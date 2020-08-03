package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseTextInputScreen extends DialogBaseScreen
{
    protected final WidgetTextFieldBase textField;
    protected final String originalText;

    public BaseTextInputScreen(String titleKey, String defaultText, @Nullable GuiScreen parent)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.originalText = defaultText;

        this.setWidthAndHeight(260, 100);
        this.centerOnScreen();

        this.textField = new WidgetTextFieldBase(this.dialogLeft + 12, this.dialogTop + 40, 240, 20, this.originalText);
        this.textField.setFocused(true);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 70;

        this.textField.setPosition(this.dialogLeft + 12, this.dialogTop + 40);
        this.addWidget(this.textField);

        x += this.createButton(x, y, ButtonType.OK) + 2;
        x += this.createButton(x, y, ButtonType.RESET) + 2;
        this.createButton(x, y, ButtonType.CANCEL);

        Keyboard.enableRepeatEvents(true);
    }

    protected int createButton(int x, int y, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, type.getDisplayName());
        button.setWidth(Math.max(40, button.getWidth()));
        return this.addButton(button, new ButtonListener(type, this)).getWidth();
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_RETURN)
        {
            // Only close the GUI if the value was successfully applied
            if (this.applyValue(this.textField.getText()))
            {
                BaseScreen.openGui(this.getParent());
            }

            return true;
        }
        else if (keyCode == Keyboard.KEY_ESCAPE)
        {
            BaseScreen.openGui(this.getParent());
            return true;
        }

        return super.onKeyTyped(typedChar, keyCode);
    }

    protected abstract boolean applyValue(String string);

    protected static class ButtonListener implements IButtonActionListener
    {
        private final BaseTextInputScreen gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, BaseTextInputScreen gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.OK)
            {
                // Only close the GUI if the value was successfully applied
                if (this.gui.applyValue(this.gui.textField.getText()))
                {
                    BaseScreen.openGui(this.gui.getParent());
                }
            }
            else if (this.type == ButtonType.CANCEL)
            {
                BaseScreen.openGui(this.gui.getParent());
            }
            else if (this.type == ButtonType.RESET)
            {
                this.gui.textField.setText(this.gui.originalText);
                this.gui.textField.setCursorToStart();
                this.gui.textField.setFocused(true);
            }
        }
    }

    protected enum ButtonType
    {
        OK      ("malilib.gui.button.ok"),
        CANCEL  ("malilib.gui.button.cancel"),
        RESET   ("malilib.gui.button.reset");

        private final String labelKey;

        ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.labelKey);
        }
    }
}
