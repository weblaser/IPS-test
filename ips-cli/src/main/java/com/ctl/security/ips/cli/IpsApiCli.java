package com.ctl.security.ips.cli;

import com.ctl.security.ips.cli.config.IpsApiCliConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by kevin on 3/19/15.
 */
public class IpsApiCli {

    public static void main(String[] args){

        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(IpsApiCliConfig.class);
        IpsApiCliDelegator ipsApiCliDelegator = annotationConfigApplicationContext.getBean(IpsApiCliDelegator.class);

        ipsApiCliDelegator.execute(args);

    }


}
