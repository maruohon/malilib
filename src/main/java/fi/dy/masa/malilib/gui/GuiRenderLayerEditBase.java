package fi.dy.masa.malilib.gui;

import net.minecraft.util.EnumFacing;
import fi.dy.masa.malilib.config.value.LayerMode;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.listener.TextFieldListenerInteger;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.gui.widget.WidgetCheckBox;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldInteger;
import fi.dy.masa.malilib.util.position.LayerRange;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class GuiRenderLayerEditBase extends GuiBase
{
    protected WidgetTextFieldBase textField1;
    protected WidgetTextFieldBase textField2;
    protected int nextY;
    protected boolean addPlayerFollowingOptions;

    protected abstract LayerRange getLayerRange();

    protected IGuiIcon getValueAdjustButtonIcon()
    {
        return BaseGuiIcon.BTN_PLUSMINUS_16;
    }

    protected void createLayerEditControls(int x, int y, LayerRange layerRange)
    {
        int origX = x;

        x += this.createLayerConfigButton(x, y, ButtonListenerLayerEdit.Type.MODE, layerRange);
        this.createLayerConfigButton(x, y, ButtonListenerLayerEdit.Type.AXIS, layerRange);
        y += 26;

        this.nextY = this.createTextFields(origX, y, 60, layerRange);
    }

    protected int createLayerConfigButton(int x, int y, ButtonListenerLayerEdit.Type type, LayerRange layerRange)
    {
        if (type == ButtonListenerLayerEdit.Type.MODE || layerRange.getLayerMode() != LayerMode.ALL)
        {
            ButtonListenerLayerEdit listener = new ButtonListenerLayerEdit(type, layerRange, this);
            ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, type.getDisplayName(layerRange));
            this.addButton(button, listener);

            return button.getWidth() + 2;
        }

        return 0;
    }

    protected int createHotkeyCheckBoxes(int x, int y, LayerRange layerRange)
    {
        return y;
    }

    protected int createTextFields(int x, int y, int width, final LayerRange layerRange)
    {
        int origX = x;
        LayerMode layerMode = layerRange.getLayerMode();

        if (layerMode == LayerMode.ALL)
        {
            return y;
        }

        if (layerMode == LayerMode.LAYER_RANGE)
        {
            String labelMin = StringUtils.translate("malilib.gui.label.render_layers.layer_min") + ":";
            String labelMax = StringUtils.translate("malilib.gui.label.render_layers.layer_max") + ":";
            int w1 = this.addLabel(x, y +  5, 0xFFFFFF, labelMax).getWidth();
            int w2 = this.addLabel(x, y + 28, 0xFFFFFF, labelMin).getWidth();

            x += Math.max(w1, w2) + 4;
        }
        else
        {
            String label = StringUtils.translate("malilib.gui.label.render_layers.layer") + ":";
            x += this.addLabel(x, y + 5, 0xFFFFFF, label).getWidth() + 4;
        }

        IGuiIcon valueAdjustIcon = this.getValueAdjustButtonIcon();

        if (layerMode == LayerMode.LAYER_RANGE)
        {
            this.textField2 = new WidgetTextFieldBase(x, y, width, 20);
            this.textField2.setTextValidator(WidgetTextFieldBase.VALIDATOR_INTEGER);
            this.textField2.setListener(new TextFieldListener(layerMode, layerRange, true));
            this.addWidget(this.textField2);

            this.createHotkeyCheckBoxes(x + width + 24, y, layerRange);

            this.createValueAdjustButton(x + width + 3, y, true, layerRange, valueAdjustIcon);
            y += 23;
        }
        else
        {
            this.textField2 = null;
        }

        this.textField1 = new WidgetTextFieldBase(x, y, width, 20);
        this.textField1.setTextValidator(WidgetTextFieldBase.VALIDATOR_INTEGER);
        this.textField1.setListener(new TextFieldListener(layerMode, layerRange, false));
        this.addWidget(this.textField1);
        this.createValueAdjustButton(x + width + 3, y, false, layerRange, valueAdjustIcon);
        y += 23;

        this.updateTextFieldValues(layerRange);

        this.createLayerConfigButton(x - 1, y, ButtonListenerLayerEdit.Type.SET_TO_PLAYER, layerRange);
        y += 22;

        if (this.addPlayerFollowingOptions)
        {
            String strLabel = "malilib.gui.button.render_layers_gui.follow_player";
            String strHover = "malilib.gui.button.hover.render_layers_gui.follow_player";
            final ButtonOnOff button = new ButtonOnOff(origX, y, -1, false, strLabel, layerRange.shouldFollowPlayer(), strHover);
            this.addButton(button, (btn, mbtn) -> {
                layerRange.toggleShouldFollowPlayer();
                button.updateDisplayString(layerRange.shouldFollowPlayer());
            });
            y += 24;

            String label = StringUtils.translate("malilib.gui.label.render_layers.player_follow_offset") + ":";
            int w = this.addLabel(origX, y + 5, 0xFFFFFF, label).getWidth();

            final WidgetTextFieldInteger textField = new WidgetTextFieldInteger(origX + w + 4, y, 40, 18, layerRange.getPlayerFollowOffset());
            textField.setUpdateListenerAlways(true);
            textField.setListener(new TextFieldListenerInteger((val) -> layerRange.setPlayerFollowOffset(val)));
            this.addWidget(textField);

            int bx = textField.getX() + textField.getWidth() + 3;
            ButtonGeneric button2 = new ButtonGeneric(bx, y + 1, this.getValueAdjustButtonIcon());

            this.addButton(button2, (btn, mbtn) -> {
                int change = mbtn == 1 ? -1 : 1;
                if (GuiBase.isShiftDown()) { change *= 2; }
                if (GuiBase.isCtrlDown())  { change *= 4; }
                layerRange.setPlayerFollowOffset(layerRange.getPlayerFollowOffset() + change);
                this.initGui();
            });
        }

        return y;
    }

    protected void updateTextFieldValues(LayerRange layerRange)
    {
        if (this.textField1 != null)
        {
            this.textField1.setText(String.valueOf(layerRange.getCurrentLayerValue(false)));
        }

        if (this.textField2 != null)
        {
            this.textField2.setText(String.valueOf(layerRange.getCurrentLayerValue(true)));
        }
    }

    protected void createValueAdjustButton(int x, int y, boolean isSecondValue, LayerRange layerRange, IGuiIcon icon)
    {
        LayerMode layerMode = layerRange.getLayerMode();
        ButtonListenerChangeValue listener = new ButtonListenerChangeValue(layerMode, layerRange, isSecondValue, this);
        ButtonGeneric button = new ButtonGeneric(x, y + 2, icon);
        this.addButton(button, listener);
    }

    protected static class ButtonListenerLayerEdit implements IButtonActionListener
    {
        protected final GuiRenderLayerEditBase parent;
        protected final LayerRange layerRange;
        protected final Type type;

        public ButtonListenerLayerEdit(Type type, LayerRange layerRange, GuiRenderLayerEditBase parent)
        {
            this.type = type;
            this.layerRange = layerRange;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == Type.MODE)
            {
                this.layerRange.setLayerMode((LayerMode) this.layerRange.getLayerMode().cycle(mouseButton == 0));
            }
            else if (this.type == Type.AXIS)
            {
                EnumFacing.Axis axis = this.layerRange.getAxis();
                int next = mouseButton == 0 ? ((axis.ordinal() + 1) % 3) : (axis.ordinal() == 0 ? 2 : axis.ordinal() - 1);
                axis = EnumFacing.Axis.values()[next % 3];
                this.layerRange.setAxis(axis);
            }
            else if (this.type == Type.SET_TO_PLAYER)
            {
                this.layerRange.setSingleBoundaryToPosition(this.parent.mc.player);
            }

            this.parent.initGui();
        }

        public enum Type
        {
            MODE            ("malilib.gui.button.render_layers_gui.layers"),
            AXIS            ("malilib.gui.button.render_layers_gui.axis"),
            SET_TO_PLAYER   ("malilib.gui.button.render_layers_gui.set_to_player");

            private final String translationKey;

            Type(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getDisplayName(LayerRange layerRange)
            {
                if (this == SET_TO_PLAYER)
                {
                    return StringUtils.translate(this.translationKey);
                }
                else
                {
                    String valueStr = this == MODE ? layerRange.getLayerMode().getDisplayName() : layerRange.getAxis().name();
                    return StringUtils.translate(this.translationKey, valueStr);
                }
            }
        }
    }

    protected static class ButtonListenerChangeValue implements IButtonActionListener
    {
        protected final GuiRenderLayerEditBase parent;
        protected final LayerRange layerRange;
        protected final LayerMode mode;
        protected final boolean isSecondLimit;

        protected ButtonListenerChangeValue(LayerMode mode, LayerRange layerRange, boolean isSecondLimit, GuiRenderLayerEditBase parent)
        {
            this.mode = mode;
            this.layerRange = layerRange;
            this.isSecondLimit = isSecondLimit;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            int change = mouseButton == 1 ? -1 : 1;

            if (GuiBase.isShiftDown())
            {
                change *= 16;
            }

            if (GuiBase.isCtrlDown())
            {
                change *= 64;
            }

            if (this.mode == LayerMode.LAYER_RANGE)
            {
                if (this.isSecondLimit)
                {
                    this.layerRange.setLayerRangeMax(this.layerRange.getLayerRangeMax() + change);
                }
                else
                {
                    this.layerRange.setLayerRangeMin(this.layerRange.getLayerRangeMin() + change);
                }
            }
            else
            {
                this.layerRange.moveLayer(change);
            }

            this.parent.updateTextFieldValues(this.layerRange);
        }
    }

    protected static class TextFieldListener implements ITextFieldListener
    {
        protected final LayerRange layerRange;
        protected final LayerMode mode;
        protected final boolean isSecondLimit;

        protected TextFieldListener(LayerMode mode, LayerRange layerRange, boolean isSecondLimit)
        {
            this.mode = mode;
            this.layerRange = layerRange;
            this.isSecondLimit = isSecondLimit;
        }

        @Override
        public void onTextChange(String newText)
        {
            int value = 0;

            try
            {
                value = Integer.parseInt(newText);
            }
            catch (NumberFormatException e)
            {
                return;
            }

            switch (this.mode)
            {
                case ALL_ABOVE:
                    this.layerRange.setLayerAbove(value);
                    break;

                case ALL_BELOW:
                    this.layerRange.setLayerBelow(value);
                    break;

                case SINGLE_LAYER:
                    this.layerRange.setLayerSingle(value);
                    break;

                case LAYER_RANGE:
                    if (this.isSecondLimit)
                    {
                        this.layerRange.setLayerRangeMax(value);
                    }
                    else
                    {
                        this.layerRange.setLayerRangeMin(value);
                    }
                    break;

                default:
            }
        }
    }

    public static class RangeHotkeyListener implements ISelectionListener<WidgetCheckBox>
    {
        protected final LayerRange layerRange;
        protected final boolean isMax;

        public RangeHotkeyListener(LayerRange layerRange, boolean isMax)
        {
            this.layerRange = layerRange;
            this.isMax = isMax;
        }

        @Override
        public void onSelectionChange(WidgetCheckBox entry)
        {
            if (this.isMax)
            {
                this.layerRange.toggleHotkeyMoveRangeMax();
            }
            else
            {
                this.layerRange.toggleHotkeyMoveRangeMin();
            }
        }
    }
}
