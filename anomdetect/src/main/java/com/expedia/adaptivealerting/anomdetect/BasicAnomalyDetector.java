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
package com.expedia.adaptivealerting.anomdetect;

import com.expedia.adaptivealerting.anomdetect.util.ModelResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Basic anomaly detector interface. A basic anomaly detector is one that generally has a single fixed model.
 *
 * @author Willie Wheeler
 */
public interface BasicAnomalyDetector extends AnomalyDetector {
    
    /**
     * Initializes this detector with the given model.
     *
     * @param anomalyDetectorModel Anomaly detector model.
     */
    void init(AnomalyDetectorModel anomalyDetectorModel);

    default <T> T extractParams(ModelResource modelResource, Class<T> clazz) {
        if (modelResource == null) {
            return null;
        }
        Map<String, Object> params = modelResource.getParams();
        return new ObjectMapper().convertValue(params, clazz);
    }

    default ModelResource extractModelResource(AnomalyDetectorModel anomalyDetectorModel) {
        if (anomalyDetectorModel instanceof ModelResource) {
            return (ModelResource) anomalyDetectorModel;
        }
        return null;
    }
}
