package com.whatever.tunester.services.tracksmetascanner.pool;

import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.services.tracksmetascanner.TracksMetaScannerService;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class TracksMetaScannerServicePool implements AutoCloseable {

    private final ObjectPool<TracksMetaScannerService> objectPool;

    private TracksMetaScannerServicePool(int maxTotal) {
        GenericObjectPool<TracksMetaScannerService> genericObjectPool =
            new GenericObjectPool<>(new TracksMetaScannersFactory());

        genericObjectPool.setMaxTotal(maxTotal);

        this.objectPool = genericObjectPool;
    }

    public static TracksMetaScannerServicePool newGenericPool(int maxTotal) {
        return new TracksMetaScannerServicePool(maxTotal);
    }

    public TrackMeta getTrackMeta(String absolutePathName) {
        TracksMetaScannerService scanner = null;

        try {
            scanner = objectPool.borrowObject();
            return scanner.getTrackMeta(absolutePathName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (scanner != null) {
                    objectPool.returnObject(scanner);
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
