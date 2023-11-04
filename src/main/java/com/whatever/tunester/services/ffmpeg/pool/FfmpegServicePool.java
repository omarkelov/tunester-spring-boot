package com.whatever.tunester.services.ffmpeg.pool;

import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.services.ffmpeg.FfmpegService;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

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

    public TrackMeta getTrackMeta(String absolutePathName) {
        FfmpegService ffmpegService = null;

        try {
            ffmpegService = objectPool.borrowObject();
            return ffmpegService.getTrackMeta(absolutePathName);
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
