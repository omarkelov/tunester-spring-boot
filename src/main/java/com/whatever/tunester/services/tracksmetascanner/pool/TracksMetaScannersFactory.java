package com.whatever.tunester.services.tracksmetascanner.pool;

import com.whatever.tunester.services.tracksmetascanner.TracksMetaScannerService;
import com.whatever.tunester.services.tracksmetascanner.TracksMetaScannerServiceFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

class TracksMetaScannersFactory extends BasePooledObjectFactory<TracksMetaScannerService> {
    @Override
    public TracksMetaScannerService create() {
        return TracksMetaScannerServiceFactory.newTracksMetaScannerService();
    }

    @Override
    public PooledObject<TracksMetaScannerService> wrap(TracksMetaScannerService tracksMetaScannerService) {
        return new DefaultPooledObject<>(tracksMetaScannerService);
    }

    @Override
    public void destroyObject(PooledObject<TracksMetaScannerService> p, DestroyMode destroyMode) throws Exception {
        super.destroyObject(p, destroyMode);
        p.getObject().close();
    }
}
