package fi.dy.masa.malilib.gui.listener;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ButtonListenerIntModifier implements IButtonActionListener
{
    protected final IntSupplier supplier;
    protected final IntConsumer consumer;
    protected final int modifierShift;
    protected final int modifierControl;
    protected final int modifierAlt;

    public ButtonListenerIntModifier(IntSupplier supplier, IntConsumer consumer)
    {
        this(supplier, consumer, 8, 1, 4);
    }

    public ButtonListenerIntModifier(IntSupplier supplier, IntConsumer consumer, int modifierShift, int modifierControl, int modifierAlt)
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

        if (BaseScreen.isShiftDown()) { amount *= this.modifierShift; }
        if (BaseScreen.isCtrlDown())  { amount *= this.modifierControl; }
        if (BaseScreen.isAltDown())   { amount *= this.modifierAlt; }

        this.consumer.accept(this.supplier.getAsInt() + amount);
    }
}
