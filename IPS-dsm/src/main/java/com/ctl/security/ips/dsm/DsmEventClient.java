package com.ctl.security.ips.dsm;

import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.domain.FirewallEventTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import manager.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

/**
 * Created by Gary Hebel on 3/19/15.
 */
@Component
public class DsmEventClient {

    private static final Logger logger = Logger.getLogger(DsmEventClient.class);

    @Autowired
    private Manager manager;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    @Autowired
    private EventClient eventClient;

    @Autowired
    private FirewallEventTransportMarshaller firewallEventTransportMarshaller;

    public List<FirewallEvent> gatherEvents(Date fromTime, Date toTime) throws DsmEventClientException {
        String sessionId = null;
        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            logger.info("session created with id " + sessionId);

            IDFilterTransport idFilterTransport = new IDFilterTransport();
            idFilterTransport.setOperator(EnumOperator.GREATER_THAN);
            idFilterTransport.setId(0);

            TimeFilterTransport timeFilterTransport = new TimeFilterTransport();
            timeFilterTransport.setType(EnumTimeFilterType.CUSTOM_RANGE);
            timeFilterTransport.setRangeFrom(dateToCalendar(fromTime));
            timeFilterTransport.setRangeTo(dateToCalendar(toTime));

            HostFilterTransport hostFilterTransport = new HostFilterTransport();
            hostFilterTransport.setType(EnumHostFilterType.ALL_HOSTS);

            List<FirewallEventTransport> firewallEventTransportList;
            firewallEventTransportList =  manager
                    .firewallEventRetrieve(timeFilterTransport, hostFilterTransport, idFilterTransport, sessionId)
                    .getFirewallEvents()
                    .getItem();
            List<FirewallEvent> firewallEvents = new ArrayList<>();
            for(FirewallEventTransport firewallEventTransport: firewallEventTransportList ){
                FirewallEvent firewallEvent = firewallEventTransportMarshaller.convert(firewallEventTransport);
                firewallEvents.add(firewallEvent);
            }

            logger.info("gathered " + firewallEvents.size() + " events");
            return firewallEvents;

        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception |
                ManagerCommunicationException_Exception | ManagerMaxSessionsException_Exception |
                ManagerException_Exception | ManagerAuthenticationException_Exception |
                ManagerTimeoutException_Exception | ManagerValidationException_Exception |
                DatatypeConfigurationException e) {
            logger.error("exception caught gathering events: " + e.getMessage());
            throw new DsmEventClientException(e);
        } finally {
            dsmLogInClient.endSession(sessionId);
            logger.info("session " + sessionId + " closed");
        }
    }

    public void sendEvents(List<FirewallEvent> events,String bearerToken) {

        for (FirewallEvent event : events){
            //TODO retrieve Tenant Name from DSM
            String tenantName="TCCD";
            EventBean eventBean = new EventBean(event.getHostName(),tenantName,event);
            eventClient.notify(eventBean,bearerToken);
        }

    }



    private XMLGregorianCalendar dateToCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(date);
        XMLGregorianCalendar calendar = null;
        calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        return calendar;
    }

}
