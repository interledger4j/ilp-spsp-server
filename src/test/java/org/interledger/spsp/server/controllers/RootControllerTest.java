package org.interledger.spsp.server.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.interledger.spsp.server.SpspServerApplication;
import org.interledger.spsp.server.controllers.IlpOverHttpStreamReceiverTest.TestConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = {SpspServerApplication.class, TestConfiguration.class},
  properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RootControllerTest {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private TestRestTemplate restTemplate;

  private BasicJsonTester jsonTester = new BasicJsonTester(getClass());

  @Test
  public void getConnectorMetaData() {
    ResponseEntity<String> metaDataResponse = restTemplate.getForEntity("/", String.class);

    logger.info("metaDataResponse: " + metaDataResponse.getBody());

    JsonContentAssert assertJson = assertThat(jsonTester.from(metaDataResponse.getBody()));
    assertJson.hasJsonPath("version");
  }
}
