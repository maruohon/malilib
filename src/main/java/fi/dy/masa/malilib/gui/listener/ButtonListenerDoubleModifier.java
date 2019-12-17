package fi.dy.masa.malilib.gui.listener;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ButtonListenerDoubleModifier implements IButtonActionListener
{
    protected final DoubleSupplier supplier;
    protected final DoubleConsumer consumer;
    protected final int modifierShift;
    protected final int modifierControl;
    protected final int modifierAlt;

    public ButtonListenerDoubleModifier(DoubleSupplier supplier, DoubleConsumer consumer)
    {
        this(supplier, consumer, 8, 1, 4);
    }

    public ButtonListenerDoubleModifier(DoubleSupplier supplier, DoubleConsumer consumer, int modifierShift, int modifierControl, int modifierAlt)
    {
        this.supplier = supplier;
        this.consumer = consumer;
        this.modifierShift = modifierShift;
        this.modifierControl = modifierControl;
        this.modifierAlt = modifierAlt;
    }

    @Override
    public void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {
        int amount = mouseButton == 1 ? -1 : 1;

        if (GuiBase.isShiftDown()) { amount *= this.modifierShift; }
        if (GuiBase.isCtrlDown())  { amount *= this.modifierControl; }
        if (GuiBase.isAltDown())   { amount *= this.modifierAlt; }

        this.consumer.accept(this.supplier.getAsDouble() + amount);
    }
}
