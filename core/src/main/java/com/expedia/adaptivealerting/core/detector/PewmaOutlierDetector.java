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
package com.expedia.adaptivealerting.core.detector;

import com.expedia.adaptivealerting.core.OutlierDetector;
import com.expedia.adaptivealerting.core.OutlierLevel;
import com.expedia.www.haystack.commons.entities.MetricPoint;

import static com.expedia.adaptivealerting.core.util.AssertUtil.isBetween;
import static com.expedia.adaptivealerting.core.util.AssertUtil.notNull;
import static java.lang.Math.*;

/**
 * <p>
 * Anomaly detector based on the probabilistic exponential weighted moving average. This is an online algorithm, meaning
 * that it updates the thresholds incrementally as new data comes in, but is less influenced by outliers than EWMA.
 * </p>
 * <p>
 * It takes a little while before the internal mean and variance estimates converge to something that makes sense. As a
 * rule of thumb, feed the detector 30 data points or so before using it for actual anomaly detection.
 * </p>
 * <p>
 * See https://www.ll.mit.edu/mission/cybersec/publications/publication-files/full_papers/2012_08_05_Carter_IEEESSP_FP.pdf.
 * Implementation based off code from here https://aws.amazon.com/blogs/iot/anomaly-detection-using-aws-iot-and-aws-lambda/.
 *
 * </p>
 *
 * @author David Sutherland
 */
public class PewmaOutlierDetector implements OutlierDetector {
    static final int DEFAULT_TRAINING_LENGTH = 30;

    /**
     * Smoothing param.
     */
    private final double alpha0;


    /**
     * Outlier weighting param.
     */
    private final double beta;

    /**
     * How many iterations to train for.
     */
    private final double trainingLength;

    /**
     * Smoothing param.
     */
    private double trainingCount;

    /**
     * Internal state for PEWMA algorithm.
     */
    private double s1;

    /**
     * Internal state for PEWMA algorithm.
     */
    private double s2;

    /**
     * Weak outlier threshold, in sigmas.
     */
    private final double weakThreshold;

    /**
     * Strong outlier threshold, in sigmas.
     */
    private final double strongThreshold;

    /**
     * Local mean estimate.
     */
    private double mean;

    /**
     * Local standard deviation estimate.
     */
    private double stdDev;
    
    /**
     * Creates a new PEWMA outlier detector with initialAlpha = 0, beta = 1.0, weakThreshold = 2.0,
     * strongThreshold = 3.0 and initValue = 0.0.
     */
    public PewmaOutlierDetector() {
        this(0.5, 1.0, 2.0, 3.0, 0.0);
    }
    
    /**
     * Creates a new PEWMA outlier detector. Initial mean is given by initValue and initial standard deviation is 0.
     *
     * @param initialAlpha    Smoothing parameter.
     * @param beta            Outlier weighting parameter.
     * @param weakThreshold   Weak outlier threshold, in sigmas.
     * @param strongThreshold Strong outlier threshold, in sigmas.
     * @param initValue       Initial observation, used to set the first mean estimate.
     */
    public PewmaOutlierDetector(
            double initialAlpha,
            double beta,
            double weakThreshold,
            double strongThreshold,
            double initValue
    ) {
        this(initialAlpha, beta, DEFAULT_TRAINING_LENGTH, weakThreshold, strongThreshold, initValue);
    }

    /**
     * Creates a new PEWMA outlier detector. Initial mean is given by initValue and initial standard deviation is 0.
     *
     * @param initialAlpha    Smoothing parameter.
     * @param beta            Outlier weighting parameter.
     * @param trainingLength  How many iterations to train for.
     * @param weakThreshold   Weak outlier threshold, in sigmas.
     * @param strongThreshold Strong outlier threshold, in sigmas.
     * @param initValue       Initial observation, used to set the first mean estimate.
     */
    public PewmaOutlierDetector(
            double initialAlpha,
            double beta,
            int trainingLength,
            double weakThreshold,
            double strongThreshold,
            double initValue
    ) {
        isBetween(initialAlpha, 0.0, 1.0, "initialAlpha must be in the range [0, 1]");

        this.alpha0 = 1 - initialAlpha;  // To standardise with the EWMA implementation we use the complement value.
        this.beta = beta;
        this.trainingLength = trainingLength;
        this.trainingCount = 1;
        this.weakThreshold = weakThreshold;
        this.strongThreshold = strongThreshold;
        this.s1 = initValue;
        this.s2 = initValue*initValue;
        updateMeanAndStdDev();
    }

    private void updateMeanAndStdDev() {
        this.mean = this.s1;
        this.stdDev = sqrt(this.s2 - this.s1*this.s1);
    }
    
    @Override
    public OutlierLevel classify(MetricPoint metricPoint) {
        notNull(metricPoint, "metricPoint can't be null");
        
        final float value = metricPoint.value();
        final double dist = abs(value - mean);
        final double largeThreshold = strongThreshold * stdDev;
        final double smallThreshold = weakThreshold * stdDev;
    
        OutlierLevel level = OutlierLevel.NORMAL;
        if (dist > largeThreshold) {
            level = OutlierLevel.STRONG;
        } else if (dist > smallThreshold) {
            level = OutlierLevel.WEAK;
        }
    
        updateEstimates(value);
    
        return level;
    }

    private void updateEstimates(double value) {
        double zt = 0;
        if (this.stdDev != 0) {
            zt = (value - this.mean)/this.stdDev;
        }
        double pt = (1/sqrt(2*Math.PI)) * exp(-0.5*zt*zt);
        double alpha = calculateAlpha(pt);

        this.s1 = alpha*this.s1 + (1 - alpha)*value;
        this.s2 = alpha*this.s2 + (1 - alpha)*value*value;

        updateMeanAndStdDev();
    }

    private double calculateAlpha(double pt) {
        if (this.trainingCount < this.trainingLength) {
            this.trainingCount++;
            return 1 - 1.0/this.trainingCount;
        }
        return (1 - this.beta*pt)*this.alpha0;
    }

    public double getMean() {
        return mean;
    }

    public double getStdDev() {
        return stdDev;
    }
}