package de.smartsteuer.metricsdemo.tax

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.BaseUnits
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
@Configuration
class CachingTaxComputer(private val taxComputer:                 TaxComputer,
                         private val taxComputerCounter:          Counter,
                         private val taxComputerCacheMissCounter: Counter,
                         @Value("\${metrics-demo.tax-computer.cache.max-size:100}")
                         private val cacheSize:                   Long,
                         meterRegistry: MeterRegistry) {

  private val maximumIncome     = 300_000
  private val incomeGranularity = 10_000
  private val taxableIncomeTenThousands: List<Counter> = (0..maximumIncome step incomeGranularity).map {
    Counter
      .builder("metrics_demo.taxable_income.ten_thousands")
      .description("distribution of taxable incomes in steps of ten-thousand")
      .tag("ten_thousands", it.toString())
      .register(meterRegistry)
  }

  private val cache: LoadingCache<Euro, TaxComputationResult> = CacheBuilder.newBuilder()
    .maximumSize(cacheSize)
    .build(object: CacheLoader<Euro, TaxComputationResult>() {
      override fun load(taxableIncome: Euro): TaxComputationResult {
        taxComputerCacheMissCounter.increment()
        return taxComputer.computeTax(taxableIncome)
      }
    })

  init {
    val taxComputerCacheUtilizationSupplier = Supplier<Number> { cache.size().toDouble() / cacheSize * 100 }
    Gauge
      .builder("metrics_demo.tax_computer_cache.cache_utilization", taxComputerCacheUtilizationSupplier)
      .description("shows tax computer cache utilization in percent")
      .baseUnit(BaseUnits.PERCENT)
      .register(meterRegistry)
  }

  fun computeTax(taxableIncome: Euro): TaxComputationResult {
    taxComputerCounter.increment()
    val bucketIndex = (taxableIncome / incomeGranularity).coerceAtMost(taxableIncomeTenThousands.size - 1)
    taxableIncomeTenThousands[bucketIndex].increment()
    return cache[taxableIncome]
  }

  @Bean
  fun taxComputationCache() = cache
}
