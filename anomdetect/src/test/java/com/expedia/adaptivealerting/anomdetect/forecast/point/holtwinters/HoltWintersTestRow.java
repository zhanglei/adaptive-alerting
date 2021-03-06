/*
 * Copyright 2019 Expedia Group, Inc.
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
package com.expedia.adaptivealerting.anomdetect.forecast.point.holtwinters;

import com.expedia.adaptivealerting.core.anomaly.AnomalyLevel;
import com.expedia.adaptivealerting.core.anomaly.StringToAnomalyLevelConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;

@Data
public class HoltWintersTestRow {

    @CsvBindByName
    private String date;

    @CsvBindByName
    private int observed;

    @CsvBindByName
    private double expectedMean;

    @CsvCustomBindByName(converter = StringToAnomalyLevelConverter.class)
    private AnomalyLevel expectedLevel;

    @CsvBindByName
    private double expectedForecast;

}
