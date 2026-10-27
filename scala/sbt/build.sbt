val gatlingVersion = "3.16.0"
val gatlingGraphqlVersion = "3.16.0.4"

lazy val gatlingSbtPluginDemo = rootProject
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "gatling-graphql-demo-sbt-scala",
    scalaVersion := "2.13.18",
    scalacOptions := Seq(
      "-encoding", "UTF-8", "-release:8", "-deprecation",
      "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps"),
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test",
      "io.gatling"            % "gatling-graphql"         % gatlingGraphqlVersion % "test",
      "io.gatling"            % "gatling-test-framework"  % gatlingVersion % "test")
  )
