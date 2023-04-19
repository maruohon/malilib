package malilib.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

import malilib.listener.EventListener;

public class DataIteratingTask<TYPE>
{
    protected final List<TYPE> data;
    protected final Iterator<TYPE> iterator;
    protected final BiConsumer<TYPE, DataIteratingTask<TYPE>> dataProcessingTask;
    protected final int dataSize;
    protected int index;
    @Nullable protected final EventListener endTask;

    public DataIteratingTask(List<TYPE> data,
                             BiConsumer<TYPE, DataIteratingTask<TYPE>> dataProcessingTask,
                             @Nullable EventListener endTask)
    {
        this.data = data;
        this.dataProcessingTask = dataProcessingTask;
        this.endTask = endTask;
        this.iterator = data.iterator();
        this.dataSize = data.size();
    }

    public void advance()
    {
        if (this.iterator.hasNext())
        {
            ++this.index;
            this.dataProcessingTask.accept(this.iterator.next(), this);
        }
        else
        {
            this.cancel();
        }
    }

    public void cancel()
    {
        if (this.endTask != null)
        {
            this.endTask.onEvent();
        }
    }

    public String getProgressString()
    {
        return StringUtils.translate("malilib.label.misc.data_iterating_task.progress", this.index, this.dataSize);
    }
}
