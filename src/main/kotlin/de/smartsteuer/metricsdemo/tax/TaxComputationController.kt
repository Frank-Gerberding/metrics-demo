package de.smartsteuer.metricsdemo.tax

import de.smartsteuer.metricsdemo.logger
import org.slf4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TaxComputationController(private val cachingTaxComputer: CachingTaxComputer) {
  private val log: Logger by logger()

  @GetMapping("/computeTaxRate")
  fun computeTaxRate(@RequestParam taxableIncome: Euro): ResponseEntity<TaxComputationResult> {
    log.debug("compute tax rate for taxable income {}", taxableIncome)
    return ResponseEntity.ok(cachingTaxComputer.computeTax(taxableIncome))
  }
}
