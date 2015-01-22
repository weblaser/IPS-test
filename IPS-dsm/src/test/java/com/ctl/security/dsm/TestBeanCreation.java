package com.ctl.security.dsm;

import com.ctl.security.dsm.config.SpringConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

import static org.junit.Assert.assertNotNull;

/**
 * Created by kevin.wilde on 1/22/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/cucumber.xml")
@ActiveProfiles("local")
public class TestBeanCreation {

    @Autowired
    DsmPolicyClient dsmPolicyClient;

    @Test
    public void testDsmPolicyClientIsNotNull() {
        assertNotNull(dsmPolicyClient);
    }

}
