package de.smartsteuer.metricsdemo.tax

typealias Euro = Int

data class TaxComputationResult(val taxableIncome: Euro, val taxRate: Euro)
