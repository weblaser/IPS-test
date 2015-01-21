package com.ctl.security.dsm;

import com.ctl.security.dsm.domain.LocalWebConfig;
import com.ctl.security.dsm.domain.ProdWebConfig;
import manager.Manager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import java.net.MalformedURLException;

@PropertySources({@PropertySource("classpath:/dsm.client.properties")})
@Configuration
@Import({LocalWebConfig.class, ProdWebConfig.class})
public class WebConfig {
    private static final Logger logger = Logger.getLogger(WebConfig.class);



//    @Bean
//    @Autowired
//    public DsmLogInClient logInClient(Manager manager) throws MalformedURLException {
//        return new DsmLogInClient(manager);
//    }


}
//        Private IP Address 10.126.155.12:4119
//        Public IP Address 206.128.154.197:4119