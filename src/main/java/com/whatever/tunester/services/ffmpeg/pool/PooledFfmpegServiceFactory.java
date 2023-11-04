package com.whatever.tunester.services.ffmpeg.pool;

import com.whatever.tunester.services.ffmpeg.FfmpegService;
import com.whatever.tunester.services.ffmpeg.FfmpegServiceFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

class PooledFfmpegServiceFactory extends BasePooledObjectFactory<FfmpegService> {
    @Override
    public FfmpegService create() {
        return FfmpegServiceFactory.newFfmpegService();
    }

    @Override
    public PooledObject<FfmpegService> wrap(FfmpegService ffmpegService) {
        return new DefaultPooledObject<>(ffmpegService);
    }

    @Override
    public void destroyObject(PooledObject<FfmpegService> p, DestroyMode destroyMode) throws Exception {
        super.destroyObject(p, destroyMode);
        p.getObject().close();
    }
}
