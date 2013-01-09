/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.map;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.WaitSupport;

public abstract class LockAwareOperation extends TTLAwareOperation implements WaitSupport{

    protected LockAwareOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    protected LockAwareOperation(String name, Data dataKey, long ttl) {
        super(name, dataKey, ttl);
    }

    protected LockAwareOperation(String name, Data dataKey, Data dataValue, long ttl) {
        super(name, dataKey, dataValue, ttl);
    }

    protected LockAwareOperation() {
    }

    public boolean shouldWait() {
        MapService mapService = (MapService) getService();
        int partitionId = getPartitionId();
        PartitionContainer pc = mapService.getPartitionContainer(partitionId);
        RecordStore mapPartition = pc.getRecordStore(name);
        boolean shouldWait = !mapPartition.canRun(this);
        return shouldWait;
    }

    public long getWaitTimeoutMillis() {
        return -1;
    }

    public abstract void onWaitExpire();

    public Object getWaitKey() {
        if (keyObject == null) {
            keyObject = getNodeEngine().toObject(dataKey);
        }
        return new MapWaitKey(getName(), keyObject,"lock");
    }


}