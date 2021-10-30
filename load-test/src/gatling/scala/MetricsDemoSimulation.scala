import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class MetricsDemoSimulation extends Simulation {
  private val randomIncomeFeeder =
    Iterator.continually(Map("income" -> Random.between(5_000, 150_000)))

  private val httpProtocol = http
    .baseUrl("http://localhost:8100")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de-DE,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  private val scn = scenario("Compute Tax")
    .feed(randomIncomeFeeder)
    .exec(http("start page")
      .get("/")
      .check(
        status.is(200),
        header("Content-Type").is("text/html;charset=UTF-8"),
        regex("""<title>Steuertarifrechner</title>""").find.exists
      ))
    .pause(1 seconds, 2 seconds)
    .repeat(4) {
      exec(http("compute tax")
        .get("/computeTaxRate")
        .queryParam("taxableIncome", "${income}")
        .check(
          status.is(200),
          header("Content-Type").is("application/json"),
          jsonPath("$.taxableIncome").find.exists.saveAs("taxableIncome"),
          jsonPath("$.taxRate").find.exists.saveAs("taxRate")
        ))
        .pause(1 seconds, 2 seconds)
    }

  //setUp(scn.inject(atOnceUsers(20)).protocols(httpProtocol))
  setUp(
    scn.inject(
      incrementConcurrentUsers(100)
        .times(20)
        .eachLevelLasting(5 seconds)
        .separatedByRampsLasting(5 seconds)
        .startingFrom(100)
    ).protocols(httpProtocol)
  ).maxDuration(5 minutes)
}