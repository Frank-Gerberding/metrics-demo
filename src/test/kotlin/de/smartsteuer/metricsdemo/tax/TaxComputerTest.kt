package de.smartsteuer.metricsdemo.tax

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class TaxComputerTest {
  private val taxComputer = TaxComputer()

  @ParameterizedTest(name = "when computeTax is called with taxable income {0}, it returns {1}, {2}")
  @CsvSource(
    "      -100,          0,         0",
    "         0,          0,         0",
    "      9408,       9408,         0",
    "      9420,       9420,         1",
    "     14532,      14532,       972",
    "     57051,      57051,     14997",
    "    270500,     270500,    104646",
    "1000000000, 1000000000, 449982921",
    "1000000001, 1000000000, 449982921",
  )
  fun `computeTax computes expected tax rate`(taxableIncome: Euro, sanitizedTaxableIncome: Euro, taxRate: Euro) {
    taxComputer.computeTax(taxableIncome) shouldBe TaxComputationResult(sanitizedTaxableIncome, taxRate)
  }
}