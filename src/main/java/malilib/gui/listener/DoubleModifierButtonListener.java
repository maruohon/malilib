package malilib.gui.listener;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import malilib.gui.BaseScreen;
import malilib.util.data.Int2BooleanFunction;

public class DoubleModifierButtonListener implements Int2BooleanFunction
{
    protected final DoubleSupplier supplier;
    protected final DoubleConsumer consumer;
    protected final int modifierShift;
    protected final int modifierControl;
    protected final int modifierAlt;

    public DoubleModifierButtonListener(DoubleSupplier supplier, DoubleConsumer consumer)
    {
        this(supplier, consumer, 8, 1, 4);
    }

    public DoubleModifierButtonListener(DoubleSupplier supplier, DoubleConsumer consumer, int modifierShift, int modifierControl, int modifierAlt)
    {
        this.supplier = supplier;
        this.consumer = consumer;
        this.modifierShift = modifierShift;
        this.modifierControl = modifierControl;
        this.modifierAlt = modifierAlt;
    }

    @Override
    public boolean apply(int mouseButton)
    {
        int amount = mouseButton == 1 ? -1 : 1;

        if (BaseScreen.isShiftDown()) { amount *= this.modifierShift; }
        if (BaseScreen.isCtrlDown())  { amount *= this.modifierControl; }
        if (BaseScreen.isAltDown())   { amount *= this.modifierAlt; }

        this.consumer.accept(this.supplier.getAsDouble() + amount);

        return true;
    }
}
