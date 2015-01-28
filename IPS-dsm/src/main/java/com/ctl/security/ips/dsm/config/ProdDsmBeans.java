package com.ctl.security.ips.dsm.config;

import manager.Manager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

import javax.net.ssl.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@Profile({"ts", "qa", "prod"})
public class ProdDsmBeans {

    private static final Logger logger = Logger.getLogger(ProdDsmBeans.class);


    @Bean
    public Manager manager() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            Map<String, Object> customProperties = new HashMap<>();
            customProperties.put("com.sun.xml.ws.request.timeout", 60000);
            customProperties.put("com.sun.xml.ws.connect.timeout", 60000);

            JaxWsPortProxyFactoryBean proxyFactoryBean = new JaxWsPortProxyFactoryBean();
            proxyFactoryBean.setServiceInterface(Manager.class);
            proxyFactoryBean.setWsdlDocumentUrl(new URL("https://10.126.155.12:4119/webservice/Manager?WSDL"));
            proxyFactoryBean.setNamespaceUri("urn:Manager");
            proxyFactoryBean.setServiceName("ManagerService");
            proxyFactoryBean.setLookupServiceOnStartup(false);
            proxyFactoryBean.setCustomProperties(customProperties);
            proxyFactoryBean.afterPropertiesSet();
            return (Manager) proxyFactoryBean.getObject();
        } catch (NoSuchAlgorithmException | KeyManagementException | MalformedURLException e) {
            logger.error(e);
        }
        return null;
    }
}
