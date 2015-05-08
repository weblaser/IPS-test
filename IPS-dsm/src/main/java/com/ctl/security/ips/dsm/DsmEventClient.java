package com.ctl.security.ips.dsm;

import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.dsm.domain.FirewallEventTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Gary Hebel on 3/19/15.
 */
@Component
public class DsmEventClient {

    private static final Logger logger = LogManager.getLogger(DsmEventClient.class);

    @Autowired
    private Manager manager;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    @Autowired
    private FirewallEventTransportMarshaller firewallEventTransportMarshaller;

    public List<FirewallEvent> gatherEvents(String accountId, Date fromTime, Date toTime) throws DsmEventClientException {
        String tenantSessionId = null;
        String sessionId = null;
        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            tenantSessionId=dsmLogInClient.connectTenantToDSMClient(accountId, sessionId);
            //TODO remove this
//            tenantSessionId = dsmLogInClient.connectTenantToDSMClient(accountId, username, password);

            logger.info("session created with id " + tenantSessionId);

            List<FirewallEventTransport> firewallEventTransportList;
            firewallEventTransportList = getFirewallEventTransports(fromTime, toTime, tenantSessionId);

            List<FirewallEvent> firewallEvents = marshallToFirewallEvents(firewallEventTransportList);

            logger.info("gathered " + firewallEvents.size() + " events");
            return firewallEvents;
        } catch (ManagerSecurityException_Exception | ManagerAuthenticationException_Exception |
                ManagerLockoutException_Exception | ManagerCommunicationException_Exception |
                ManagerMaxSessionsException_Exception | ManagerException_Exception |
                ManagerTimeoutException_Exception e) {
            logger.error("Exception caught connecting to the dsm: " + e.getMessage());
            throw new DsmEventClientException(e);
        } finally {
            dsmLogInClient.endSession(tenantSessionId);
            dsmLogInClient.endSession(sessionId);
            logger.info("session " + tenantSessionId + " closed");
        }
    }

    private List<FirewallEvent> marshallToFirewallEvents(List<FirewallEventTransport> firewallEventTransportList) {
        List<FirewallEvent> firewallEvents = new ArrayList<>();
        for (FirewallEventTransport firewallEventTransport : firewallEventTransportList) {
            FirewallEvent firewallEvent = firewallEventTransportMarshaller.convert(firewallEventTransport);
            firewallEvents.add(firewallEvent);
        }
        return firewallEvents;
    }

    private List<FirewallEventTransport> getFirewallEventTransports(Date fromTime, Date toTime, String sessionId) throws DsmEventClientException {
        try {
            List<FirewallEventTransport> firewallEventTransportList;

            IDFilterTransport idFilterTransport = getIdFilterTransport();

            TimeFilterTransport timeFilterTransport = getTimeFilterTransport(fromTime, toTime);

            HostFilterTransport hostFilterTransport = getHostFilterTransport();

            firewallEventTransportList = manager
                    .firewallEventRetrieve(timeFilterTransport, hostFilterTransport, idFilterTransport, sessionId)
                    .getFirewallEvents()
                    .getItem();

            return firewallEventTransportList;

        } catch (ManagerException_Exception | ManagerAuthenticationException_Exception |
                ManagerTimeoutException_Exception | ManagerValidationException_Exception |
                DatatypeConfigurationException e) {
            logger.error("Exception caught gathering events: " + e.getMessage());
            throw new DsmEventClientException(e);
        }
    }

    private HostFilterTransport getHostFilterTransport() {
        HostFilterTransport hostFilterTransport = new HostFilterTransport();
        hostFilterTransport.setType(EnumHostFilterType.ALL_HOSTS);
        return hostFilterTransport;
    }

    private TimeFilterTransport getTimeFilterTransport(Date fromTime, Date toTime) throws DatatypeConfigurationException {
        TimeFilterTransport timeFilterTransport = new TimeFilterTransport();
        timeFilterTransport.setType(EnumTimeFilterType.CUSTOM_RANGE);
        timeFilterTransport.setRangeFrom(dateToCalendar(fromTime));
        timeFilterTransport.setRangeTo(dateToCalendar(toTime));
        return timeFilterTransport;
    }

    private IDFilterTransport getIdFilterTransport() {
        IDFilterTransport idFilterTransport = new IDFilterTransport();
        idFilterTransport.setOperator(EnumOperator.GREATER_THAN);
        idFilterTransport.setId(0);
        return idFilterTransport;
    }

    private XMLGregorianCalendar dateToCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(date);
        XMLGregorianCalendar calendar = null;
        calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        return calendar;
    }

}
