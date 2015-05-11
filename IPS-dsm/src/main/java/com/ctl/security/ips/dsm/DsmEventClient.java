package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.domain.Event.Event;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.dsm.domain.DpiEventTransportMarshaller;
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

    @Autowired
    private DpiEventTransportMarshaller dpiEventTransportMarshaller;

    public List<Event> gatherEvents(String accountId, Date fromTime, Date toTime) throws DsmEventClientException {
        String sessionId = null;
        String tenantSessionId = null;
        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            tenantSessionId = dsmLogInClient.connectTenantToDSMClient(accountId, sessionId);

            List<Event> events = new ArrayList<>();

            events.addAll(marshallToDpiEvents(getDPIEventTransports(fromTime, toTime, tenantSessionId)));


            logger.info("Gathered " + events.size() + " events");

            return events;
        } catch (ManagerSecurityException_Exception | ManagerAuthenticationException_Exception |
                ManagerLockoutException_Exception | ManagerCommunicationException_Exception |
                ManagerMaxSessionsException_Exception | ManagerException_Exception |
                ManagerTimeoutException_Exception e) {
            logger.error("Exception caught connecting to the dsm: " + e.getMessage());
            throw new DsmEventClientException(e);
        } finally {
            dsmLogInClient.endSession(tenantSessionId);
            dsmLogInClient.endSession(sessionId);
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
            TimeFilterTransport timeFilterTransport = getTimeFilterTransport(fromTime, toTime);

            logger.info("Gathering Events for From: " + fromTime + " To: " + toTime);

            List<FirewallEventTransport> firewallEventTransportList = manager
                    .firewallEventRetrieve(
                            timeFilterTransport,
                            getHostFilterTransport(),
                            getIdFilterTransport(),
                            sessionId)
                    .getFirewallEvents()
                    .getItem();

            logger.info("Gathered " + firewallEventTransportList.size() + "Firewall events");

            return firewallEventTransportList;

        } catch (ManagerException_Exception | ManagerAuthenticationException_Exception |
                ManagerTimeoutException_Exception | ManagerValidationException_Exception |
                DatatypeConfigurationException e) {
            logger.error("Exception caught gathering events: " + e.getMessage());
            throw new DsmEventClientException(e);
        }
    }

    private List<DpiEvent> marshallToDpiEvents(List<DPIEventTransport> dpiEventTransportList) {
        List<DpiEvent> firewallEvents = new ArrayList<>();
        for (DPIEventTransport dpiEventTransport : dpiEventTransportList) {
            DpiEvent firewallEvent = dpiEventTransportMarshaller.convert(dpiEventTransport);
            firewallEvents.add(firewallEvent);
        }
        return firewallEvents;
    }

    private List<DPIEventTransport> getDPIEventTransports(Date fromTime, Date toTime, String sessionId) throws DsmEventClientException {
        try {

            TimeFilterTransport timeFilterTransport = getTimeFilterTransport(fromTime, toTime);

            logger.info("Gathering Events for From: " + fromTime + " To: " + toTime);

            List<DPIEventTransport> dpiEventTransportList = manager
                    .dpiEventRetrieve(
                            timeFilterTransport,
                            getHostFilterTransport(),
                            getIdFilterTransport(),
                            sessionId).getDPIEvents().getItem();

            logger.info("Gathered " + dpiEventTransportList.size() + " DPI events");

            return dpiEventTransportList;
        } catch (ManagerException_Exception | ManagerAuthenticationException_Exception |
                ManagerTimeoutException_Exception | ManagerValidationException_Exception |
                DatatypeConfigurationException e) {
            logger.error("Exception caught gathering DPI events: " + e.getMessage());
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
