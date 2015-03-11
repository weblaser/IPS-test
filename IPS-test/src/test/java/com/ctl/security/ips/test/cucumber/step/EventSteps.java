package com.ctl.security.ips.test.cucumber.step;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.junit.Stubbing;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertThat;

//import com.github.tomakehurst.wiremock.testsupport.WireMockResponse;


/**
 * Created by sean.robb on 3/10/2015.
 */
public class EventSteps {

//    @Autowired
//    private NotificationClient notificationClient;
//
//    @Autowired
//    private EventClient eventClient;
//
//    @Autowired
//    private ConfigurationItemClient configurationItemClient;
//
//    @Autowired
//    private ClcAuthenticationComponent clcAuthenticationComponent;
//
//    private EventBean eventBean;
//    private String bearerToken;
//    public static final int MAX_ATTEMPTS = 30;

    WireMockServer wireMockServer;

    @Given("^an event occurs$")
    public void an_event_occurs() throws Throwable{

        wireMockServer=new WireMockServer(9090);

        configureFor("localhost", 9090);
        wireMockServer.start();
        MappingBuilder mappingBuilder = get(urlEqualTo("/some/thing"))
                .willReturn(aResponse()
                        .withBody("Hello world!"));
        stubFor(mappingBuilder);


//
//        bearerToken = clcAuthenticationComponent.authenticate();
//
//        String hostName = "server.host.name." + System.currentTimeMillis();
//        String accountId = ClcAuthenticationComponent.VALID_AA;
//        NotificationDestination notificationDestination = new NotificationDestination();
//        NotificationDestinationBean notificationDestinationBean;
//        Event event=new Event();
//        event.setMessage("An Event Has Happened");
//
//        notificationDestination.setEmailAddress("My.Test.Email@Testing.Test");
//        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);
//        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
//        notificationDestination.setUrl("my.test.url/testing");
//
//        Account account = new Account()
//                .setCustomerAccountId(accountId);
//
//        ConfigurationItem configurationItem = new ConfigurationItem()
//                .setAccount(account)
//                .setHostName(hostName);
//
//        configurationItemClient.createConfigurationItem(configurationItem);
//
//        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);
//        notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, notificationDestinations);
//
//        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);
//
//        eventBean.setAccountId(accountId);
//        eventBean.setHostName(hostName);
//        eventBean.setEvent(event);
//
//
//        int currentAttempts = 0;
//        int maxAttempts = MAX_ATTEMPTS;
//
//        //Waits for Notification Destinations to be set (Since Active MQ)
//        while(currentAttempts < maxAttempts && notificationDestinations == null){
//            notificationDestinations = configurationItem.getAccount().getNotificationDestinations();
//            Thread.sleep(1000);
//            currentAttempts++;
//        }
    }

    @When("^the event notification is posted to the events endpoint$")
    public void the_event_notification_is_posted_to_the_events_endpoint(){
       // eventClient.notify(eventBean,bearerToken);
    }

    @Then("^the event information is sent to the correct URL$")
    public void the_event_information_is_sent_to_the_correct_URL(){
//        ConfigurationItemResource configurationItemResource=null;
//        List<NotificationDestination> notificationDestinations=null;
//
//        configurationItemResource = configurationItemClient.getConfigurationItem(eventBean.getHostName(), eventBean.getAccountId());
//        notificationDestinations = configurationItemResource.getContent().getAccount().getNotificationDestinations();

        RequestPatternBuilder requestPatternBuilder = getRequestedFor(urlEqualTo("some/thing"));
        //verify(postRequestedFor(urlEqualTo("some/thing")));
      //assertThat(testClient.get("/some/thing").statusCode(), is(200));
        //assertThat(get("/some/thing/else").statusCode(), is(404));
        wireMockServer.stop();
    }
}
