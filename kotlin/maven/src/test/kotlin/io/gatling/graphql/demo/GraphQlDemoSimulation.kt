package io.gatling.graphql.demo

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.graphql.*
import io.gatling.javaapi.graphql.GraphQlDsl.*
import io.gatling.javaapi.http.*
import io.gatling.javaapi.http.HttpDsl.*

class GraphQlDemoSimulation : Simulation() {

  // Example using the free public Countries GraphQL API: https://countries.trevorblades.com/
  // Please be kind with this service and keep the load low.
  private val httpProtocol = http.baseUrl("https://countries.trevorblades.com")

  private val graphQlProtocol = graphql.endpoint("/graphql")

  private val countryCodes =
    listFeeder(
        listOf(
          mapOf<String, Any>("code" to "FR"),
          mapOf<String, Any>("code" to "DE"),
          mapOf<String, Any>("code" to "JP"),
          mapOf<String, Any>("code" to "BR"),
          mapOf<String, Any>("code" to "ZA"),
        )
      )
      .circular()

  private val scn =
    scenario("Countries")
      .feed(countryCodes)
      .exec(
        graphql
          .query("query Country(\$code: ID!) { country(code: \$code) { name capital currency } }")
          .variable("code", "#{code}")
          .check(
            graphqlData.jsonPath("$.country.name").saveAs("name"),
            graphqlData.jsonPath("$.country.capital").exists(),
          )
      )
      .pause(1)
      .exec(
        graphql
          .query("query Continents { continents { code name } }")
          .check(graphqlData.jsonPath("$.continents[*].name").findAll().exists())
      )

  // ./mvnw gatling:test -Dgatling.simulationClass=io.gatling.graphql.demo.GraphQlDemoSimulation

  init {
    setUp(scn.injectOpen(atOnceUsers(5))).protocols(httpProtocol, graphQlProtocol)
  }
}
