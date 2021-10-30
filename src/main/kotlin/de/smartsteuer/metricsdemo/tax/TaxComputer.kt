package de.smartsteuer.metricsdemo.tax

import io.micrometer.core.annotation.Timed
import org.springframework.stereotype.Service


@Service
class TaxComputer {
  @Timed("metrics_demo.tax_computer.timer", histogram = true)
  fun computeTax(taxableIncome: Euro): TaxComputationResult {
    val sanitizedTaxableIncome = taxableIncome.coerceIn(0..1_000_000_000)
    val taxRate = when {
      sanitizedTaxableIncome <=   9_408 -> 0
      sanitizedTaxableIncome <=  14_532 -> (972.87 * (sanitizedTaxableIncome -  9_408) / 10_000 + 1_400) * (sanitizedTaxableIncome -  9_408) / 10_000
      sanitizedTaxableIncome <=  57_051 -> (212.02 * (sanitizedTaxableIncome - 14_532) / 10_000 + 2_397) * (sanitizedTaxableIncome - 14_532) / 10_000 + 972.79
      sanitizedTaxableIncome <= 270_500 -> 0.42 * sanitizedTaxableIncome -  8_963.74
      else                              -> 0.45 * sanitizedTaxableIncome - 17_078.74
    }.toInt()
    return TaxComputationResult(sanitizedTaxableIncome, taxRate)
  }
}