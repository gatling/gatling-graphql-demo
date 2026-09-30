package io.gatling.graphql.demo;

import java.util.List;
import java.util.Map;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.graphql.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.graphql.GraphQlDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class GraphQlDemoSimulation extends Simulation {

  // Example using the free public Countries GraphQL API: https://countries.trevorblades.com/
  // Please be kind with this service and keep the load low.
  HttpProtocolBuilder httpProtocol = http.baseUrl("https://countries.trevorblades.com");

  GraphQlProtocolBuilder graphQlProtocol = graphql.endpoint("/graphql");

  FeederBuilder<Object> countryCodes = listFeeder(List.of(
    Map.of("code", "FR"),
    Map.of("code", "DE"),
    Map.of("code", "JP"),
    Map.of("code", "BR"),
    Map.of("code", "ZA")
  )).circular();

  ScenarioBuilder scn = scenario("Countries")
    .feed(countryCodes)
    .exec(
      graphql
        .query("query Country($code: ID!) { country(code: $code) { name capital currency } }")
        .variable("code", "#{code}")
        .check(
          graphqlData.jsonPath("$.country.name").saveAs("name"),
          graphqlData.jsonPath("$.country.capital").exists()
        )
    )
    .pause(1)
    .exec(
      graphql
        .query("query Continents { continents { code name } }")
        .check(graphqlData.jsonPath("$.continents[*].name").findAll().exists())
    );

  // ./mvnw gatling:test -Dgatling.simulationClass=io.gatling.graphql.demo.GraphQlDemoSimulation

  {
    setUp(
      scn.injectOpen(atOnceUsers(5))
    ).protocols(httpProtocol, graphQlProtocol);
  }
}
