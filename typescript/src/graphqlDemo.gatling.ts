import { atOnceUsers, arrayFeeder, scenario, simulation } from "@gatling.io/core";
import { graphql } from "@gatling.io/graphql";
import { http } from "@gatling.io/http";

export default simulation((setUp) => {
  // Example using the free public Countries GraphQL API: https://countries.trevorblades.com/
  // Please be kind with this service and keep the load low.
  const httpProtocol = http.baseUrl("https://countries.trevorblades.com");

  const graphQlProtocol = graphql.endpoint("/graphql");

  const countryCodes = arrayFeeder([
    { code: "FR" },
    { code: "DE" },
    { code: "JP" },
    { code: "BR" },
    { code: "ZA" }
  ]).circular();

  const scn = scenario("Countries")
    .feed(countryCodes)
    .exec(
      graphql
        .query("query Country($code: ID!) { country(code: $code) { name capital currency } }")
        .variable("code", "#{code}")
        .check(
          graphql.data.jsonPath("$.country.name").saveAs("name"),
          graphql.data.jsonPath("$.country.capital").exists()
        )
    )
    .pause(1)
    .exec(
      graphql
        .query("query Continents { continents { code name } }")
        .check(graphql.data.jsonPath("$.continents[*].name").findAll().exists())
    );

  setUp(scn.injectOpen(atOnceUsers(5))).protocols(httpProtocol, graphQlProtocol);
});
