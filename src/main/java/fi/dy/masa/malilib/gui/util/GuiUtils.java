package fi.dy.masa.malilib.gui.util;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.config.values.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetTextFieldBase;
import fi.dy.masa.malilib.interfaces.ICoordinateValueModifier;
import fi.dy.masa.malilib.util.PositionUtils.CoordinateType;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiUtils
{
    public static int getScaledWindowWidth()
    {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return sr.getScaledWidth();
    }

    public static int getScaledWindowHeight()
    {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return sr.getScaledHeight();
    }

    public static int getDisplayWidth()
    {
        return Minecraft.getMinecraft().displayWidth;
    }

    public static int getDisplayHeight()
    {
        return Minecraft.getMinecraft().displayHeight;
    }

    @Nullable
    public static GuiScreen getCurrentScreen()
    {
        return Minecraft.getMinecraft().currentScreen;
    }

    public static void createBlockPosInputsVertical(int x, int y, int textFieldWidth, BlockPos pos,
            ICoordinateValueModifier modifier, boolean addButton, GuiBase gui)
    {
        createBlockPosInput(x, y     , textFieldWidth, CoordinateType.X, pos, modifier, addButton, gui);
        createBlockPosInput(x, y + 17, textFieldWidth, CoordinateType.Y, pos, modifier, addButton, gui);
        createBlockPosInput(x, y + 34, textFieldWidth, CoordinateType.Z, pos, modifier, addButton, gui);
    }

    public static void createVec3dInputsVertical(int x, int y, int textFieldWidth, Vec3d pos,
            ICoordinateValueModifier modifier, boolean addButton, GuiBase gui)
    {
        createVec3dInput(x, y     , textFieldWidth, CoordinateType.X, pos, modifier, addButton, gui);
        createVec3dInput(x, y + 17, textFieldWidth, CoordinateType.Y, pos, modifier, addButton, gui);
        createVec3dInput(x, y + 34, textFieldWidth, CoordinateType.Z, pos, modifier, addButton, gui);
    }

    public static void createBlockPosInput(int x, int y, int textFieldWidth, CoordinateType type, BlockPos pos,
            ICoordinateValueModifier modifier, boolean addButton, GuiBase gui)
    {
        x = addLabel(x, y, type, gui);

        WidgetTextFieldBase textField = new WidgetTextFieldBase(x, y + 1, textFieldWidth, 14, getCoordinateValueString(type, pos));
        textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_INTEGER);
        textField.setUpdateListenerAlways(true);
        addTextFieldAndButton(x + textFieldWidth + 4, y, type, modifier, textField, addButton, gui);
    }

    public static void createVec3dInput(int x, int y, int textFieldWidth, CoordinateType type, Vec3d pos,
            ICoordinateValueModifier modifier, boolean addButton, GuiBase gui)
    {
        x = addLabel(x, y, type, gui);

        WidgetTextFieldBase textField = new WidgetTextFieldBase(x, y + 1, textFieldWidth, 14, getCoordinateValueString(type, pos));
        textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_DOUBLE);
        textField.setUpdateListenerAlways(true);
        addTextFieldAndButton(x + textFieldWidth + 4, y, type, modifier, textField, addButton, gui);
    }

    protected static void addTextFieldAndButton(int x, int y, CoordinateType type, ICoordinateValueModifier modifier,
            WidgetTextFieldBase textField, boolean addButton, GuiBase gui)
    {
        textField.setListener((newText) -> modifier.setValueFromString(type, newText));
        gui.addWidget(textField);

        if (addButton)
        {
            String hover = StringUtils.translate("malilib.gui.button.hover.plus_minus_tip");
            ButtonGeneric button = new ButtonGeneric(x, y, GuiIconBase.BTN_PLUSMINUS_16, hover);
            gui.addButton(button, new ButtonListenerCoordinateInput(type, modifier));
        }
    }

    public static String getCoordinateValueString(CoordinateType type, BlockPos pos)
    {
        switch (type)
        {
            case X:
                return String.valueOf(pos.getX());
            case Y:
                return String.valueOf(pos.getY());
            case Z:
                return String.valueOf(pos.getZ());
        }

        return "";
    }

    public static String getCoordinateValueString(CoordinateType type, Vec3d pos)
    {
        switch (type)
        {
            case X:
                return String.valueOf(pos.x);
            case Y:
                return String.valueOf(pos.y);
            case Z:
                return String.valueOf(pos.z);
        }

        return "";
    }

    protected static int addLabel(int x, int y, CoordinateType type, GuiBase gui)
    {
        x += gui.addLabel(x, y + 4, 0xFFFFFFFF, type.name() + ":").getWidth() + 4;
        return x;
    }

    public static int getHudOffsetForPotions(HudAlignment alignment, double scale, EntityPlayer player)
    {
        if (alignment == HudAlignment.TOP_RIGHT)
        {
            // Only Chuck Norris can divide by zero
            if (scale == 0d)
            {
                return 0;
            }

            Collection<PotionEffect> effects = player.getActivePotionEffects();

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (PotionEffect effect : effects)
                {
                    Potion potion = effect.getPotion();

                    if (effect.doesShowParticles() && potion.hasStatusIcon())
                    {
                        if (potion.isBeneficial())
                        {
                            y1 = 26;
                        }
                        else
                        {
                            y2 = 52;
                            break;
                        }
                    }
                }

                return (int) (Math.max(y1, y2) / scale);
            }
        }

        return 0;
    }

    public static int getHudPosY(int yOrig, int yOffset, int contentHeight, double scale, HudAlignment alignment)
    {
        int scaledHeight = GuiUtils.getScaledWindowHeight();
        int posY = yOrig;

        switch (alignment)
        {
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                posY = (int) ((scaledHeight / scale) - contentHeight - yOffset);
                break;
            case CENTER:
                posY = (int) ((scaledHeight / scale / 2.0d) - (contentHeight / 2.0d) + yOffset);
                break;
            default:
        }

        return posY;
    }

    public static class ButtonListenerCoordinateInput implements IButtonActionListener
    {
        protected final ICoordinateValueModifier modifier;
        protected final CoordinateType type;

        public ButtonListenerCoordinateInput(CoordinateType type, ICoordinateValueModifier modifier)
        {
            this.modifier = modifier;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            int amount = mouseButton == 1 ? -1 : 1;
            if (GuiBase.isShiftDown()) { amount *= 8; }
            if (GuiBase.isAltDown())   { amount *= 4; }

            this.modifier.modifyValue(this.type, amount);
        }

        public enum Type
        {
            NUDGE_COORD_X,
            NUDGE_COORD_Y,
            NUDGE_COORD_Z;
        }
    }
}
