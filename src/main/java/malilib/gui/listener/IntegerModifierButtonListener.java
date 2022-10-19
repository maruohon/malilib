package malilib.gui.listener;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import malilib.gui.BaseScreen;
import malilib.util.data.Int2BooleanFunction;

public class IntegerModifierButtonListener implements Int2BooleanFunction
{
    protected final IntSupplier supplier;
    protected final IntConsumer consumer;
    protected final int modifierShift;
    protected final int modifierControl;
    protected final int modifierAlt;

    public IntegerModifierButtonListener(IntSupplier supplier, IntConsumer consumer)
    {
        this(supplier, consumer, 8, 1, 4);
    }

    public IntegerModifierButtonListener(IntSupplier supplier, IntConsumer consumer, int modifierShift, int modifierControl, int modifierAlt)
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

        this.consumer.accept(this.supplier.getAsInt() + amount);

        return true;
    }
}
