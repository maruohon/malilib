package malilib.input.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.util.math.MathHelper;

import malilib.MaLiLibConfigs;
import malilib.action.Action;
import malilib.action.ActionContext;
import malilib.config.option.BooleanContainingConfig;
import malilib.config.option.OptionListConfig;
import malilib.input.ActionResult;
import malilib.input.KeyAction;
import malilib.input.KeyBind;
import malilib.listener.EventListener;
import malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import malilib.overlay.message.MessageOutput;
import malilib.overlay.message.MessageUtils;
import malilib.util.data.DoubleStorage;
import malilib.util.data.IntegerStorage;
import malilib.util.data.RangedDoubleStorage;
import malilib.util.data.RangedIntegerStorage;

public class AdjustableValueHotkeyCallback implements HotkeyCallback
{
    protected static final List<AdjustableValueHotkeyCallback> ACTIVE_CALLBACKS = new ArrayList<>(2);

    protected final List<EventListener> adjustListeners = new ArrayList<>(2);
    @Nullable protected final BooleanContainingConfig<?> toggleConfig;
    @Nullable protected final IntConsumer valueAdjuster;
    @Nullable protected BooleanSupplier enabledCondition;
    @Nullable protected HotkeyCallback callback;
    @Nullable protected Action keyAction;
    @Nullable protected BooleanConfigMessageFactory toggleMessageFactory;
    protected boolean reverseDirection;
    protected boolean triggerAlwaysOnRelease;
    protected boolean valueAdjusted;

    /**
     * Creates a wrapper callback, which has special behavior for KeyAction.BOTH, such that
     * it will only call the provided callback on the following KeyAction.RELEASE,
     * if there was no config value adjusted while the keybind was active.
     * If the KeyAction value is PRESS or RELEASE,
     * then there is no special behavior and the provided callback is called directly.
     * The hotkey callback has priority over the boolean callback, if both are provided.
     * So it only makes sense to provide one. 
     */
    public AdjustableValueHotkeyCallback(@Nullable BooleanContainingConfig<?> toggleConfig,
                                         @Nullable IntConsumer valueAdjuster)
    {
        this.toggleConfig = toggleConfig;
        this.valueAdjuster = valueAdjuster;
    }

    public AdjustableValueHotkeyCallback setKeyAction(@Nullable Action keyAction)
    {
        this.keyAction = keyAction;
        return this;
    }

    public AdjustableValueHotkeyCallback setHotkeyCallback(@Nullable HotkeyCallback callback)
    {
        this.callback = callback;
        return this;
    }

    public AdjustableValueHotkeyCallback setAdjustmentEnabledCondition(@Nullable BooleanSupplier enabledCondition)
    {
        this.enabledCondition = enabledCondition;
        return this;
    }

    public AdjustableValueHotkeyCallback addAdjustListener(EventListener listener)
    {
        this.adjustListeners.add(listener);
        return this;
    }

    public AdjustableValueHotkeyCallback setToggleMessageFactory(@Nullable BooleanConfigMessageFactory toggleMessageFactory)
    {
        this.toggleMessageFactory = toggleMessageFactory;
        return this;
    }

    public AdjustableValueHotkeyCallback setReverseDirection(boolean reverseDirection)
    {
        this.reverseDirection = reverseDirection;
        return this;
    }

    public AdjustableValueHotkeyCallback setTriggerAlwaysOnRelease(boolean triggerAlwaysOnRelease)
    {
        this.triggerAlwaysOnRelease = triggerAlwaysOnRelease;
        return this;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        // For keybinds that activate on both edges, the press action activates the
        // "adjust mode", and we just cancel further processing of the key presses here.
        if (action == KeyAction.PRESS &&
            key.getSettings().getActivateOn() == KeyAction.BOTH &&
            this.isAdjustmentEnabled())
        {
            ACTIVE_CALLBACKS.add(this);
            return ActionResult.SUCCESS;
        }

        ACTIVE_CALLBACKS.clear();

        // Don't toggle the state if a value was adjusted
        if (this.valueAdjusted && this.triggerAlwaysOnRelease == false)
        {
            this.valueAdjusted = false;
            return ActionResult.SUCCESS;
        }

        this.valueAdjusted = false;

        if (this.callback != null)
        {
            return this.callback.onKeyAction(action, key);
        }
        else if (this.keyAction != null)
        {
            return this.keyAction.execute(new ActionContext(key.getSettings().getMessageType()));
        }
        else if (this.toggleConfig != null)
        {
            this.toggleConfig.toggleBooleanValue();
            this.printToggleMessage(key);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    protected boolean isAdjustmentEnabled()
    {
        return this.enabledCondition == null || this.enabledCondition.getAsBoolean();
    }

    protected void printToggleMessage(KeyBind key)
    {
        MessageOutput messageOutput = key.getSettings().getMessageType();
        MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.toggleConfig, this.toggleMessageFactory);
    }

    protected ActionResult adjustValue(int amount)
    {
        if (this.valueAdjuster != null && this.isAdjustmentEnabled())
        {
            if (this.reverseDirection)
            {
                amount = -amount;
            }

            this.valueAdjuster.accept(amount);
            this.valueAdjusted = true;

            for (EventListener listener : this.adjustListeners)
            {
                listener.onEvent();
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static ActionResult onScrollAdjust(int amount)
    {
        if (ACTIVE_CALLBACKS.isEmpty() == false &&
            MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_MODIFIER.isHeld())
        {
            for (AdjustableValueHotkeyCallback callback : ACTIVE_CALLBACKS)
            {
                // This is a bit silly, but multiple simultaneous callbacks
                // doesn't really make sense or work sanely anyway...
                return callback.adjustValue(amount);
            }
        }

        return ActionResult.PASS;
    }

    public static ActionResult scrollAdjustDecrease(ActionContext ctx)
    {
        return onScrollAdjust(-1);
    }

    public static ActionResult scrollAdjustIncrease(ActionContext ctx)
    {
        return onScrollAdjust(1);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       OptionListConfig<?> config)
    {
        IntConsumer adjuster = (v) -> config.cycleValue(v > 0);
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       IntegerStorage intConfig)
    {
        IntConsumer adjuster = (v) -> intConfig.setIntegerValue(intConfig.getIntegerValue() + v);
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       IntegerStorage intConfig, int multiplier)
    {
        IntConsumer adjuster = (v) -> intConfig.setIntegerValue(intConfig.getIntegerValue() + v * multiplier);
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       IntegerStorage intConfig, IntSupplier multiplier)
    {
        IntConsumer adjuster = (v) -> intConfig.setIntegerValue(intConfig.getIntegerValue() + v * multiplier.getAsInt());
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createWrapping(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                               IntegerStorage intConfig, int minValue, int maxValue)
    {
        IntConsumer adjuster = (v) -> {
            int currentValue = intConfig.getIntegerValue();
            int newValue = currentValue + v;
            if (newValue < minValue) { newValue = maxValue; }
            else if (newValue > maxValue) { newValue = minValue; }
            intConfig.setIntegerValue(newValue);
        };
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createClamped(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                              RangedIntegerStorage intConfig)
    {
        return createClamped(toggleConfig, intConfig, intConfig.getMinIntegerValue(), intConfig.getMaxIntegerValue());
    }

    public static AdjustableValueHotkeyCallback createClamped(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                              IntegerStorage intConfig, int minValue, int maxValue)
    {
        IntConsumer adjuster = (v) -> intConfig.setIntegerValue(MathHelper.clamp(intConfig.getIntegerValue() + v, minValue, maxValue));
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createBitShifter(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                                 IntegerStorage intConfig)
    {
        IntBinaryOperator op = (value, amount) -> {
            if (value == 0) return amount > 0 ? 1 : -1;
            else if (amount > 0) value <<= 1;
            else value >>= 1;
            return value;
        };
        IntConsumer adjuster = (v) -> intConfig.setIntegerValue(op.applyAsInt(intConfig.getIntegerValue(), v));
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createScaled(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                             IntegerStorage intConfig, int intervalMs, int multiplier)
    {
        TimeIntervalValueScaler scaler = new TimeIntervalValueScaler(intervalMs, multiplier);
        IntConsumer adjuster = (v) -> intConfig.setIntegerValue(intConfig.getIntegerValue() + scaler.getScaledValue(v));
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       DoubleStorage doubleConfig)
    {
        IntConsumer adjuster = (v) -> doubleConfig.setDoubleValue(doubleConfig.getDoubleValue() + v);
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       DoubleStorage doubleConfig, double multiplier)
    {
        IntConsumer adjuster = (v) -> doubleConfig.setDoubleValue(doubleConfig.getDoubleValue() + (double) v * multiplier);
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback create(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                       DoubleStorage doubleConfig, DoubleSupplier multiplier)
    {
        IntConsumer adjuster = (v) -> doubleConfig.setDoubleValue(doubleConfig.getDoubleValue() + (double) v * multiplier.getAsDouble());
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createScaled(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                             DoubleStorage doubleConfig, int intervalMs, int multiplier)
    {
        TimeIntervalValueScaler scaler = new TimeIntervalValueScaler(intervalMs, multiplier);
        IntConsumer adjuster = (v) -> doubleConfig.setDoubleValue(doubleConfig.getDoubleValue() + scaler.getScaledValue(v));
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createClamped(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                              RangedDoubleStorage doubleConfig, DoubleSupplier multiplier)
    {
        return createClamped(toggleConfig, doubleConfig, doubleConfig.getMinDoubleValue(), doubleConfig.getMaxDoubleValue(), multiplier);
    }

    public static AdjustableValueHotkeyCallback createClamped(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                              DoubleStorage doubleConfig,
                                                              double minValue, double maxValue, DoubleSupplier multiplier)
    {
        IntConsumer adjuster = (v) -> doubleConfig.setDoubleValue(MathHelper.clamp(doubleConfig.getDoubleValue() +
                                                                                   (double) v * multiplier.getAsDouble(), minValue, maxValue));
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createClampedDoubleDelegate(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                                            Supplier<DoubleStorage> doubleDelegate,
                                                                            double minValue, double maxValue,
                                                                            DoubleSupplier multiplier)
    {
        IntConsumer adjuster = (v) -> {
            DoubleStorage doubleConfig = doubleDelegate.get();
            doubleConfig.setDoubleValue(MathHelper.clamp(doubleConfig.getDoubleValue() + (double) v * multiplier.getAsDouble(), minValue, maxValue));
        };
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }

    public static AdjustableValueHotkeyCallback createClampedDoubleDelegate(@Nullable BooleanContainingConfig<?> toggleConfig,
                                                                            Supplier<DoubleStorage> doubleDelegate,
                                                                            double minValue, double maxValue,
                                                                            Function<Integer, Double> multiplier)
    {
        IntConsumer adjuster = (v) -> {
            DoubleStorage doubleConfig = doubleDelegate.get();
            doubleConfig.setDoubleValue(MathHelper.clamp(doubleConfig.getDoubleValue() + (double) v * multiplier.apply(v), minValue, maxValue));
        };
        return new AdjustableValueHotkeyCallback(toggleConfig, adjuster);
    }
}
