package de.smartsteuer.metricsdemo.tax

import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class CachingTaxComputerTest {
  private val taxComputer:                 TaxComputer   = mockk()
  private val taxComputerCounter:          Counter       = mockk(relaxed = true)
  private val taxComputerCacheMissCounter: Counter       = mockk(relaxed = true)
  private val tenThousandsCounters:        Map<Int, Counter> = (0..300_000 step 10_000).associateWith { mockk(relaxed = true) }
  private val meterRegistry:               MeterRegistry = object: SimpleMeterRegistry() {
    override fun newCounter(id: Meter.Id): Counter = tenThousandsCounters[id.tags.first().value.toInt()] ?: throw IllegalArgumentException("illegal tag")
  }

  private val cachingTaxComputer = CachingTaxComputer(taxComputer, taxComputerCounter,
                                                      taxComputerCacheMissCounter, 100, meterRegistry)

  @Test
  fun `computeTax() increments counters and caches computed values`() {
    every { taxComputer.computeTax(10_000) } returns TaxComputationResult(10_000, 1_346)
    cachingTaxComputer.computeTax(10_000) shouldBe TaxComputationResult(10_000, 1_346)
    verify(exactly = 1) { taxComputerCounter.increment() }
    verify(exactly = 1) { taxComputerCacheMissCounter.increment() }
    verify(exactly = 1) { taxComputer.computeTax(10_000) }
    verify(exactly = 1) { tenThousandsCounters[10_000]?.increment() }

    cachingTaxComputer.computeTax(10_000) shouldBe TaxComputationResult(10_000, 1_346)
    verify(exactly = 2) { taxComputerCounter.increment() }
    verify(exactly = 1) { taxComputerCacheMissCounter.increment() }
    verify(exactly = 1) { taxComputer.computeTax(10_000) }
    verify(exactly = 2) { tenThousandsCounters[10_000]?.increment() }
  }

  @Test
  fun `computeTax() increments counters for incomes`() {
    every { taxComputer.computeTax(any()) } returns TaxComputationResult(0, 0)
    cachingTaxComputer.computeTax(10_000)
    cachingTaxComputer.computeTax(15_000)
    cachingTaxComputer.computeTax(20_000)
    cachingTaxComputer.computeTax(25_000)
    cachingTaxComputer.computeTax(29_999)
    cachingTaxComputer.computeTax(30_000)
    verify(exactly = 2) { tenThousandsCounters[10_000]?.increment() }
    verify(exactly = 3) { tenThousandsCounters[20_000]?.increment() }
    verify(exactly = 1) { tenThousandsCounters[30_000]?.increment() }
  }
}