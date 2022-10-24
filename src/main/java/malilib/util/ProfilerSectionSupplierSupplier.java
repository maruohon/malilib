package malilib.util;

import java.util.function.Supplier;

public interface ProfilerSectionSupplierSupplier
{
    /**
     * Returns a supplier for the profiler section name that should be used
     * @return
     */
    default Supplier<String> getProfilerSectionSupplier()
    {
        return () -> this.getClass().getName().replace(".", "_");
    }
}
