package org.interledger.spsp.server.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.interledger.spsp.StreamConnectionDetails;
import org.interledger.stream.receiver.StreamReceiver;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.spring.common.MediaTypes;

import java.util.Objects;

@RestController
public class SpspController {
  //private final PaymentEndpointsService paymentEndpointsService;
  private final StreamReceiver streamReceiver;
//  private final ArtemisIldcpService artemisIldcpService;


  public SpspController(
    //final PaymentEndpointsService paymentEndpointsService,
    final StreamReceiver streamReceiver
    //ArtemisIldcpService artemisIldcpService
  ) {
   // this.paymentEndpointsService = Objects.requireNonNull(paymentEndpointsService);
    this.streamReceiver = Objects.requireNonNull(streamReceiver);
    //this.artemisIldcpService = Objects.requireNonNull(artemisIldcpService);
  }

  @RequestMapping(
    path = "/{spsp_path}", method = RequestMethod.GET,
    produces = {APPLICATION_JSON_VALUE, MediaTypes.PROBLEM_VALUE}
  )
  public ResponseEntity<StreamConnectionDetails> getSpspResponse(
    @PathVariable("spsp_path") final String spspPath
  ) {
    Objects.requireNonNull(spspPath);

//    // Throws a PaymentEndpointNotFoundProblem if no PaymentEndpoint is found...
//    final PaymentEndpoint paymentEndpoint = paymentEndpointsService.getEndpointBySpspPath(spspPath);
//    // `g.artemis` --> `g.artemis.bob` --> `g.artemis.bob.ab12b3b....`
//    final InterledgerAddress paymentEndpointIlpAddress = artemisIldcpService.artemisInterledgerAddress()
//      .with(paymentEndpoint.interledgerAddressSuffix());
//    final StreamConnectionDetails connectionDetails = streamReceiver.setupStream(paymentEndpointIlpAddress);

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // TODO: Add Caching Headers.
    //return new ResponseEntity(connectionDetails, headers, HttpStatus.OK);
    return null;
  }
}
