package de.smartsteuer.metricsdemo

import de.smartsteuer.metricsdemo.RandomIncome.feeder
import io.gatling.core.Predef.{constantConcurrentUsers, _}
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.{postfixOps, reflectiveCalls}

class MetricsDemoSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:9999")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de-DE,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  private val scn = scenario("Compute Tax")
    .feed(feeder)
    .exec(http("start page")
      .get("/")
      .check(
        status.is(200),
        header("Content-Type").is("text/html;charset=UTF-8"),
        regex("""<title>Steuertarifrechner</title>""").find.exists
      ))
    .pause(1 seconds, 5 seconds)
    .repeat(4) {
      exec(http("compute tax")
        .get("/computeTaxRate")
        .queryParam("taxableIncome", "${income}")
        .check(
          status.is(200),
          header("Content-Type").is("application/json"),
          jsonPath("$.taxableIncome").find.exists,
          jsonPath("$.taxRate").find.exists
        ))
      .pause(1 seconds, 2 seconds)
    }

  //  setUp(scn.inject(atOnceUsers(20)).protocols(httpProtocol))
  setUp(
    scn.inject(
      constantConcurrentUsers(10).during(2 minutes),
      rampConcurrentUsers(10).to(200).during(30 minutes),
      rampConcurrentUsers(200).to(800).during(60 minutes),
      rampConcurrentUsers(800).to(1_000).during(60 minutes),
      constantConcurrentUsers(1_000).during(60 minutes),
      rampConcurrentUsers(1_000).to(800).during(60 minutes),
      rampConcurrentUsers(800).to(200).during(60 minutes),
      rampConcurrentUsers(200).to(10).during(30 minutes),
      constantConcurrentUsers(10).during(2 minutes),
      //      incrementConcurrentUsers(100)
      //        .times(20)
      //        .eachLevelLasting(5 seconds)
      //        .separatedByRampsLasting(5 seconds)
      //        .startingFrom(100)
    ).protocols(httpProtocol)
  )
}