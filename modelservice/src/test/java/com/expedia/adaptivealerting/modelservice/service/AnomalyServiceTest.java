package com.expedia.adaptivealerting.modelservice.service;

import com.expedia.adaptivealerting.core.anomaly.AnomalyResult;
import com.expedia.adaptivealerting.modelservice.entity.Metric;
import com.expedia.adaptivealerting.modelservice.providers.graphite.*;
import com.expedia.adaptivealerting.modelservice.repo.MetricRepository;
import com.expedia.adaptivealerting.modelservice.spi.MetricSource;
import com.expedia.adaptivealerting.modelservice.spi.MetricSourceResult;
import com.expedia.adaptivealerting.modelservice.test.ObjectMother;
import com.expedia.adaptivealerting.modelservice.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AnomalyServiceTest {

    @InjectMocks
    private AnomalyService anomalyService = new AnomalyServiceImpl();

    @Mock
    @Qualifier("metricSourceServiceListFactoryBean")
    private List<MetricSource> metricSources;

    @Mock
    private MetricRepository metricRepository;

    private Metric metric;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initTestObjects();
        initDependencies();
    }

    //TODO Add anomaly service tests here. [KS]
    @Test
    public void testGetAnomalies() {
        ObjectMother mom = ObjectMother.instance();
        AnomalyRequest request = mom.getAnomalyRequest();
        List<AnomalyResult> actualResults = anomalyService.getAnomalies(request);
        assertNotNull(actualResults);
    }

    private void initTestObjects() {
        ObjectMother mom = ObjectMother.instance();
        this.metric = mom.getMetric();
    }

    private void initDependencies() {
        when(metricRepository.findByHash(anyString())).thenReturn(metric);
    }

}
