package pro.taskana.camunda.camundasystemconnector.acceptance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import pro.taskana.adapter.systemconnector.api.GeneralTask;
import pro.taskana.adapter.systemconnector.camunda.api.impl.CamundaSystemConnectorImpl;
import pro.taskana.adapter.systemconnector.camunda.api.impl.CamundaTaskRetriever;
import pro.taskana.camunda.camundasystemconnector.configuration.CamundaConnectorTestConfiguration;

@RunWith(SpringRunner.class) //SpringRunner is an alias for the SpringJUnit4ClassRunner
@ContextConfiguration(classes = {CamundaConnectorTestConfiguration.class})
@SpringBootTest
public class RetrieveCamundaTaskAccTest {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CamundaTaskRetriever taskRetriever;
    
    private MockRestServiceServer mockServer;
    
    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    public void testGetCamundaTasks() throws ParseException {
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = formatter.parse("2019-01-14T15:22:30.811+0100");
        Instant createdAfter = date.toInstant();
        
        String expectedBody = "{\"createdAfter\": \"" + formatter.format(date) + "\"}";

        GeneralTask expectedTask = new GeneralTask();
        expectedTask.setId("801aca2e-1b25-11e9-b283-94819a5b525c");
        expectedTask.setName("modify Request");
        expectedTask.setCreated(formatter.format(date));
        expectedTask.setPriority("50");
        expectedTask.setSuspended("false");
        
//        GeneralTask[] expectedResultBody = new GeneralTask[] {expectedTask};
//        ResponseEntity<GeneralTask[]> expectedResult = new ResponseEntity<GeneralTask[]>(expectedResultBody, HttpStatus.OK);
        
        String expectedReplyBody = "[{" +
            "        \"id\": \"801aca2e-1b25-11e9-b283-94819a5b525c\",\r\n" + 
            "        \"name\": \"modify Request\",\r\n" + 
            "        \"assignee\": null,\r\n" + 
            "        \"created\": \"2019-01-14T15:22:30.811+0100\",\r\n" + 
            "        \"due\": null,\r\n" + 
            "        \"followUp\": null,\r\n" + 
            "        \"delegationState\": null,\r\n" + 
            "        \"description\": null,\r\n" + 
            "        \"executionId\": \"7df99ab8-1b0f-11e9-b283-94819a5b525c\",\r\n" + 
            "        \"owner\": null,\r\n" + 
            "        \"parentTaskId\": null,\r\n" + 
            "        \"priority\": 50,\r\n" + 
            "        \"processDefinitionId\": \"generatedFormsQuickstart:1:2454fb85-1b0b-11e9-b283-94819a5b525c\",\r\n" + 
            "        \"processInstanceId\": \"7df99ab8-1b0f-11e9-b283-94819a5b525c\",\r\n" + 
            "        \"taskDefinitionKey\": \"Task_0yogl0i\",\r\n" + 
            "        \"caseExecutionId\": null,\r\n" + 
            "        \"caseInstanceId\": null,\r\n" + 
            "        \"caseDefinitionId\": null,\r\n" + 
            "        \"suspended\": false,\r\n" + 
            "        \"formKey\": null,\r\n" + 
            "        \"tenantId\": null\r\n" + 
            "    }]";
        
        
        String camundaSystemUrl = "http://localhost:8080/engine-rest";
        mockServer.expect(requestTo(camundaSystemUrl + "/task/" ))
        .andExpect(method(HttpMethod.POST)) 
        .andExpect(content().contentType( org.springframework.http.MediaType.APPLICATION_JSON))                
        .andExpect(content().string( expectedBody))
        .andRespond(withSuccess(expectedReplyBody, MediaType.APPLICATION_JSON));

        List<GeneralTask> actualResult = taskRetriever.retrieveCamundaTasks(camundaSystemUrl, createdAfter);
        
        assertNotNull(actualResult);
        assertEquals(expectedTask, actualResult.get(0));
    }
    
}
