package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class WidgetStringListEntry extends WidgetConfigOptionBase
{
    protected final WidgetListStringList parent;
    protected final String defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;

    public WidgetStringListEntry(int x, int y, int width, int height, float zLevel,
            int listIndex, boolean isOdd, String initialValue, String defaultValue, MinecraftClient mc, WidgetListStringList parent)
    {
        super(x, y, width, height, zLevel, mc, parent);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;
        this.lastAppliedValue = initialValue;
        this.initialStringValue = initialValue;
        this.parent = parent;

        int textFieldX = x + 20;
        int textFieldWidth = width - 160;
        int resetX = textFieldX + textFieldWidth + 2;
        int by = y + 4;
        int bx = textFieldX;
        int bOff = 18;

        if (this.isDummy() == false)
        {
            this.addLabel(x + 2, y + 4, 20, 12, 0xC0C0C0C0, String.format("%3d:", listIndex + 1));
            bx = this.addTextField(0, textFieldX, y + 1, resetX, textFieldWidth, 20, initialValue);

            this.addListActionButton(bx, by, ButtonType.ADD);
            bx += bOff;

            this.addListActionButton(bx, by, ButtonType.REMOVE);
            bx += bOff;

            if (this.canBeMoved(true))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_DOWN);
            }

            bx += bOff;

            if (this.canBeMoved(false))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_UP);
                bx += bOff;
            }
        }
        else
        {
            this.addListActionButton(bx, by, ButtonType.ADD);
        }
    }

    protected boolean isDummy()
    {
        return this.listIndex < 0;
    }

    protected void addListActionButton(int x, int y, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(0, x, y, type.getIcon(), I18n.translate(type.getHoverTextKey()));
        ListenerListActions listener = new ListenerListActions(type, button, this);
        this.addButton(button, listener);
    }

    protected int addTextField(int id, int x, int y, int resetX, int configWidth, int configHeight, String initialValue)
    {
        TextFieldWidget field = this.createTextField(id++, x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxLength(this.maxTextfieldTextLength);
        field.setText(initialValue);

        ButtonGeneric resetButton = this.createResetButton(id, resetX, y, field);
        ChangeListenerTextField listenerChange = new ChangeListenerTextField(field, resetButton, this.defaultValue);
        ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

        this.addTextField(field, listenerChange);
        this.addButton(resetButton, listenerReset);

        return resetButton.x + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int id, int x, int y, TextFieldWidget textField)
    {
        String labelReset = I18n.translate("malilib.gui.button.reset.caps");
        int w = this.mc.textRenderer.getStringWidth(labelReset) + 10;

        ButtonGeneric resetButton = new ButtonGeneric(id, x, y, w, 20, labelReset);
        resetButton.active = textField.getText().equals(this.defaultValue) == false;

        return resetButton;
    }

    @Override
    public boolean wasConfigModified()
    {
        return this.isDummy() == false && this.textField.getTextField().getText().equals(this.initialStringValue) == false;
    }

    @Override
    public void applyNewValueToConfig()
    {
        if (this.isDummy() == false)
        {
            IConfigStringList config = this.parent.getParent().getConfig();
            List<String> list = config.getStrings();
            String value = this.textField.getTextField().getText();

            if (list.size() > this.listIndex)
            {
                list.set(this.listIndex, value);
                this.lastAppliedValue = value;
            }
        }
    }

    private void insertEntryBefore()
    {
        List<String> list = this.parent.getParent().getConfig().getStrings();
        final int size = list.size();
        int index = this.listIndex < 0 ? size : (this.listIndex >= size ? size : this.listIndex);
        list.add(index, "");
        this.parent.refreshEntries();
        this.parent.markConfigsModified();
    }

    private void removeEntry()
    {
        List<String> list = this.parent.getParent().getConfig().getStrings();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            list.remove(this.listIndex);
            this.parent.refreshEntries();
            this.parent.markConfigsModified();
        }
    }

    private void moveEntry(boolean down)
    {
        List<String> list = this.parent.getParent().getConfig().getStrings();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            String tmp;
            int index1 = this.listIndex;
            int index2 = -1;

            if (down && this.listIndex < (size - 1))
            {
                index2 = index1 + 1;
            }
            else if (down == false && this.listIndex > 0)
            {
                index2 = index1 - 1;
            }

            if (index2 >= 0)
            {
                this.parent.markConfigsModified();
                this.parent.applyPendingModifications();

                tmp = list.get(index1);
                list.set(index1, list.get(index2));
                list.set(index2, tmp);
                this.parent.refreshEntries();
            }
        }
    }

    private boolean canBeMoved(boolean down)
    {
        final int size = this.parent.getParent().getConfig().getStrings().size();
        return (this.listIndex >= 0 && this.listIndex < size) &&
                ((down && this.listIndex < (size - 1)) || (down == false && this.listIndex > 0));
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        if (this.isOdd)
        {
            DrawableHelper.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            DrawableHelper.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x30FFFFFF);
        }

        this.drawSubWidgets(mouseX, mouseY);
        this.drawTextFields(mouseX, mouseY);
        this.drawButtons(mouseX, mouseY, 0f);
    }

    public static class ChangeListenerTextField extends ConfigOptionChangeListenerTextField
    {
        protected final String defaultValue;

        public ChangeListenerTextField(TextFieldWidget textField, ButtonBase buttonReset, String defaultValue)
        {
            super(null, textField, buttonReset);

            this.defaultValue = defaultValue;
        }

        @Override
        public void onKeyTyped(int keyCode, int scanCode, int modifiers)
        {
            this.buttonReset.active = this.textField.getText().equals(this.defaultValue) == false;
        }
    }

    private static class ListenerResetConfig implements IButtonActionListener<ButtonGeneric>
    {
        private final WidgetStringListEntry parent;
        private final ButtonGeneric buttonReset;

        public ListenerResetConfig(ButtonGeneric buttonReset, WidgetStringListEntry parent)
        {
            this.buttonReset = buttonReset;
            this.parent = parent;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
            this.parent.textField.getTextField().setText(this.parent.defaultValue);
            this.buttonReset.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.buttonReset.active = this.parent.textField.getTextField().getText().equals(this.parent.defaultValue) == false;
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            this.actionPerformed(control);
        }
    }

    private static class ListenerListActions implements IButtonActionListener<ButtonGeneric>
    {
        private final ButtonType type;
        private final WidgetStringListEntry parent;
        private final ButtonGeneric button;

        public ListenerListActions(ButtonType type, ButtonGeneric button, WidgetStringListEntry parent)
        {
            this.type = type;
            this.button = button;
            this.parent = parent;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
            this.button.playDownSound(MinecraftClient.getInstance().getSoundManager());

            if (this.type == ButtonType.ADD)
            {
                this.parent.insertEntryBefore();
            }
            else if (this.type == ButtonType.REMOVE)
            {
                this.parent.removeEntry();
            }
            else
            {
                this.parent.moveEntry(this.type == ButtonType.MOVE_DOWN);
            }
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            this.actionPerformed(control);
        }
    }

    private enum ButtonType
    {
        ADD         (Icons.PLUS, "malilib.gui.button.hovertext.add"),
        REMOVE      (Icons.MINUS, "malilib.gui.button.hovertext.remove"),
        MOVE_UP     (Icons.ARROW_UP, "malilib.gui.button.hovertext.move_up"),
        MOVE_DOWN   (Icons.ARROW_DOWN, "malilib.gui.button.hovertext.move_down");

        private final Icons icon;
        private final String hoverTextkey;

        private ButtonType(Icons icon, String hoverTextkey)
        {
            this.icon = icon;
            this.hoverTextkey = hoverTextkey;
        }

        public IGuiIcon getIcon()
        {
            return this.icon;
        }

        public String getHoverTextKey()
        {
            return this.hoverTextkey;
        }
    }

    private enum Icons implements IGuiIcon
    {
        ARROW_UP    (108,   0, 15, 15),
        ARROW_DOWN  (108,  15, 15, 15),
        PLUS        (108,  30, 15, 15),
        MINUS       (108,  45, 15, 15);

        public static final Identifier TEXTURE = new Identifier(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

        private final int u;
        private final int v;
        private final int w;
        private final int h;

        private Icons(int u, int v, int w, int h)
        {
            this.u = u;
            this.v = v;
            this.w = w;
            this.h = h;
        }

        @Override
        public int getWidth()
        {
            return this.w;
        }

        @Override
        public int getHeight()
        {
            return this.h;
        }

        @Override
        public int getU()
        {
            return this.u;
        }

        @Override
        public int getV()
        {
            return this.v;
        }

        @Override
        public void renderAt(int x, int y, float zLevel, boolean enabled, boolean selected)
        {
            int u = this.u;

            if (enabled)
            {
                u += this.w;
            }

            if (selected)
            {
                u += this.w;
            }

            RenderUtils.drawTexturedRect(x, y, u, this.v, this.w, this.h, zLevel);
        }

        @Override
        public Identifier getTexture()
        {
            return TEXTURE;
        }
    }
}
