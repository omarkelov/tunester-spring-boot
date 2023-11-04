package com.whatever.tunester.services.ffmpeg.pool;

import com.whatever.tunester.services.ffmpeg.FfmpegService;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.function.Function;

public class FfmpegServicePool implements AutoCloseable {

    private final ObjectPool<FfmpegService> objectPool;

    private FfmpegServicePool(int maxTotal) {
        GenericObjectPool<FfmpegService> genericObjectPool =
            new GenericObjectPool<>(new PooledFfmpegServiceFactory());

        genericObjectPool.setMaxTotal(maxTotal);

        this.objectPool = genericObjectPool;
    }

    public static FfmpegServicePool newGenericPool(int maxTotal) {
        return new FfmpegServicePool(maxTotal);
    }

    public <ResultType> ResultType useFfmpegService(Function<FfmpegService, ResultType> function) {
        FfmpegService ffmpegService = null;

        try {
            ffmpegService = objectPool.borrowObject();
            return function.apply(ffmpegService);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ffmpegService != null) {
                    objectPool.returnObject(ffmpegService);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        objectPool.close();
    }
}
