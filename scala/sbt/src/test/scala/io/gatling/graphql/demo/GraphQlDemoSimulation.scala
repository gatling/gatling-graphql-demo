package io.gatling.graphql.demo

import io.gatling.core.Predef._
import io.gatling.graphql.Predef._
import io.gatling.http.Predef._

class GraphQlDemoSimulation extends Simulation {

  // Example using the free public Countries GraphQL API: https://countries.trevorblades.com/
  // Please be kind with this service and keep the load low.
  private val httpProtocol = http.baseUrl("https://countries.trevorblades.com")

  private val graphQlProtocol = graphql.endpoint("/graphql")

  private val countryCodes = Array("FR", "DE", "JP", "BR", "ZA").map(code => Map("code" -> code)).circular

  private val scn = scenario("Countries")
    .feed(countryCodes)
    .exec(
      graphql
        .query("query Country($code: ID!) { country(code: $code) { name capital currency } }")
        .variable("code", "#{code}")
        .check(
          graphqlData.jsonPath("$.country.name").saveAs("name"),
          graphqlData.jsonPath("$.country.capital").exists
        )
    )
    .pause(1)
    .exec(
      graphql
        .query("query Continents { continents { code name } }")
        .check(graphqlData.jsonPath("$.continents[*].name").findAll.exists)
    )

  // sbt "Gatling / testOnly io.gatling.graphql.demo.GraphQlDemoSimulation"

  setUp(
    scn.inject(atOnceUsers(5))
  ).protocols(httpProtocol, graphQlProtocol)
}
