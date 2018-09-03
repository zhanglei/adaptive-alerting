/*
 * Copyright 2018 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.anomdetect.control;

import com.expedia.adaptivealerting.anomdetect.AnomalyDetectorFactory;
import com.typesafe.config.Config;

import java.util.UUID;

import static com.expedia.adaptivealerting.core.util.AssertUtil.notNull;

/**
 * @author Willie Wheeler
 */
public final class EwmaAnomalyDetectorFactory implements AnomalyDetectorFactory<EwmaAnomalyDetector> {
    
    @Override
    public void init(Config appConfig) {
        notNull(appConfig, "appConfig can't be null");
    }
    
    @Override
    public EwmaAnomalyDetector create(UUID uuid) {
        notNull(uuid, "uuid can't be null");
        // TODO Look up params
        if (UUID.fromString("5159c1b8-94ca-424f-b25c-e9f5bcb2fc51").equals(uuid)) {

            // Super noisy detector
            return new EwmaAnomalyDetector(0.15, 0.5, 1.0, 0.0);
        }
        return null;
    }
}