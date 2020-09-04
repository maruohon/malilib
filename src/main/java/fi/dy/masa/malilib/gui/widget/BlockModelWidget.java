package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import fi.dy.masa.malilib.render.RenderUtils;

public class BlockModelWidget extends BaseModelWidget
{
    @Nullable protected IBlockState state;
    @Nullable protected IBakedModel model;

    public BlockModelWidget(int x, int y, @Nullable IBlockState state)
    {
        this(x, y, 16, state);
    }

    public BlockModelWidget(int x, int y, int dimensions, @Nullable Block block)
    {
        this(x, y, dimensions, block != null ? block.getDefaultState() : null);
    }

    public BlockModelWidget(int x, int y, int dimensions, @Nullable IBlockState state)
    {
        super(x, y, dimensions);

        this.setState(state);
    }

    public BlockModelWidget setState(@Nullable IBlockState state)
    {
        this.state = state;

        if (state != null)
        {
            this.model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        }
        else
        {
            this.model = null;
        }

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    @Override
    protected void renderModel(int x, int y, int z, float scale)
    {
        if (this.model != null)
        {
            RenderUtils.renderModelInGui(x, y, z, this.model, this.state);
        }
    }
}
