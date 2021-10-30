package de.smartsteuer.metricsdemo

import org.slf4j.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

inline fun <reified R: Any> logger() = LoggerDelegate<R>()

/**
 * Provides a logger for the current class by using a delegate which simplifies
 * the creation. A logger should be created by code like this:
 * ```
 * companion object {
 *   val log by LoggerDelegate()
 * }
 * ```
 */
class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
  override fun getValue(thisRef: R, property: KProperty<*>) =
    getLogger(getClassForLogging(thisRef.javaClass))
}

private fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

private fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> =
  javaClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == javaClass } ?: javaClass
