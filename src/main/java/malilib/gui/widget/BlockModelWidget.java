package malilib.gui.widget;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import malilib.gui.util.ScreenContext;
import malilib.render.RenderUtils;

public class BlockModelWidget extends BaseModelWidget
{
    @Nullable protected BlockState state;
    @Nullable protected BakedModel model;

    public BlockModelWidget(@Nullable BlockState state)
    {
        this(16, state);
    }

    public BlockModelWidget(@Nullable Block block)
    {
        this(16, block);
    }

    public BlockModelWidget(int dimensions, @Nullable Block block)
    {
        this(dimensions, block != null ? block.defaultBlockState() : null);
    }

    public BlockModelWidget(int dimensions, @Nullable BlockState state)
    {
        super(dimensions);

        this.setState(state);
    }

    public BlockModelWidget setState(@Nullable BlockState state)
    {
        this.state = state;

        if (state != null)
        {
            this.model = this.mc.getBlockRenderer().getBlockModel(state);
        }
        else
        {
            this.model = null;
        }

        this.updateSize();

        return this;
    }

    @Override
    protected void renderModel(int x, int y, float z, float scale, ScreenContext ctx)
    {
        if (this.model != null)
        {
            RenderUtils.renderModelInGui(x, y, z, this.model, this.state, ctx);
        }
    }
}
