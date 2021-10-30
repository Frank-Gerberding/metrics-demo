package de.smartsteuer.metricsdemo.metrics.fake

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

@Component
@Profile("generate-prometheus-test-data")
class PrometheusTestDataGenerator(private val taxComputerCounter: Counter) {

  private val shortPeriodicFunctionBuilder = PeriodicFunctionBuilder(Duration.ofMinutes(60))
  //private val longPeriodicFunctionBuilder  = PeriodicFunctionBuilder(Duration.ofDays(30))

  val fakeTaxComputerCacheHitsSupplier: Supplier<Number> = Supplier { 0.0 }

  // create counter functions by specifying minimum and maximum counts per minute
  private val taxComputerCounterFunction          = shortPeriodicFunctionBuilder.build(10.perMinute,  20.perMinute)
  //private val timeBetweenApiRequestsTimerFunction = shortPeriodicFunctionBuilder.build( 5.seconds,     2.minutes)
  //private val requestDurationFunction             = shortPeriodicFunctionBuilder.build(20.millis,    500.millis)

  fun generateNextState(meterRegistry: MeterRegistry) {
    println("generate next state using $meterRegistry...")
    val time = Clock.SYSTEM.wallTime()
    taxComputerCounter.increment(taxComputerCounterFunction(time).toDouble())
  }
}
