package fi.dy.masa.malilib.util.consumer;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;

public class DataIteratingTask<TYPE>
{
    protected final List<TYPE> data;
    protected final Iterator<TYPE> iterator;
    protected final BiConsumer<TYPE, DataIteratingTask<TYPE>> dataProcessingTask;
    @Nullable protected final EventListener endTask;

    public DataIteratingTask(List<TYPE> data,
                             BiConsumer<TYPE, DataIteratingTask<TYPE>> dataProcessingTask,
                             @Nullable EventListener endTask)
    {
        this.data = data;
        this.dataProcessingTask = dataProcessingTask;
        this.endTask = endTask;
        this.iterator = data.iterator();
    }

    public void advance()
    {
        if (this.iterator.hasNext())
        {
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
}
