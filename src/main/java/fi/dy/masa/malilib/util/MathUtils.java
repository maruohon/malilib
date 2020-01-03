package fi.dy.masa.malilib.util;

public class MathUtils
{
    /**
     * Returns the average value of the elements in the given array
     * @param arr
     * @return
     */
    public static double average(int[] arr)
    {
        final int size = arr.length;

        if (size == 0)
        {
            return 0;
        }

        long sum = 0;

        for (int i = 0; i < size; ++i)
        {
            sum += arr[i];
        }

        return (double) sum / (double) size;
    }

    /**
     * Returns the average value of the elements in the given array
     * @param arr
     * @return
     */
    public static double average(long[] arr)
    {
        final int size = arr.length;

        if (size == 0)
        {
            return 0;
        }

        long sum = 0;

        for (int i = 0; i < size; ++i)
        {
            sum += arr[i];
        }

        return (double) sum / (double) size;
    }

    /**
     * Returns the average value of the elements in the given array
     * @param arr
     * @return
     */
    public static double average(double[] arr)
    {
        final int size = arr.length;

        if (size == 0)
        {
            return 0;
        }

        double sum = 0;

        for (int i = 0; i < size; ++i)
        {
            sum += arr[i];
        }

        return sum / (double) size;
    }

    /**
     * Returns the minimum value from the given array
     * @param arr
     * @return
     */
    public static int getMinValue(int[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        int minValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] < minValue)
            {
                minValue = arr[i];
            }
        }

        return minValue;
    }

    /**
     * Returns the maximum value from the given array
     * @param arr
     * @return
     */
    public static int getMaxValue(int[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        int maxValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] > maxValue)
            {
                maxValue = arr[i];
            }
        }

        return maxValue;
    }

    /**
     * Returns the minimum value from the given array
     * @param arr
     * @return
     */
    public static long getMinValue(long[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        long minValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] < minValue)
            {
                minValue = arr[i];
            }
        }

        return minValue;
    }

    /**
     * Returns the maximum value from the given array
     * @param arr
     * @return
     */
    public static long getMaxValue(long[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        long maxValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] > maxValue)
            {
                maxValue = arr[i];
            }
        }

        return maxValue;
    }
}
