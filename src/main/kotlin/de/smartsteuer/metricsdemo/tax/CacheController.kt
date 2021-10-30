package de.smartsteuer.metricsdemo.tax

import com.google.common.cache.LoadingCache
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CacheController(private val taxComputationCache: LoadingCache<Euro, TaxComputationResult>) {

  @PostMapping("cache/clear")
  fun clearTaxComputationCache() {
    taxComputationCache.invalidateAll()
  }
}