package de.smartsteuer.metricsdemo.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.BaseUnits
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DemoMetrics(private val meterRegistry: MeterRegistry) {
  @Bean
  fun taxComputerCounter() = Counter
    .builder("metrics_demo.tax_computer.count")
    .description("counts number of calls to tax computer")
    .baseUnit(BaseUnits.OPERATIONS)
    .register(meterRegistry)

  @Bean
  fun taxComputerCacheMissCounter() = Counter
    .builder("metrics_demo.tax_computer_cache.miss_count")
    .description("counts number of cache misses in cached tax computer")
    .baseUnit(BaseUnits.OPERATIONS)
    .register(meterRegistry)
}
