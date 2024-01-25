package malilib.mixin.render;

public abstract class GuiScreenMixin {}
/*
@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin extends Gui
{
    @Inject(method = "renderToolTip", at = @At("RETURN"))
    private void onRenderToolTip(ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderTooltipPost(stack, x, y);
    }
}
*/
