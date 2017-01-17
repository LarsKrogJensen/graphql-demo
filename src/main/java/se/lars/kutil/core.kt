package se.lars.kutil

import ch.qos.logback.core.joran.conditional.ElseAction

inline fun <reified T: Any> Any.cast() = this as T

inline fun <reified T: Any> Any.cast(default: T): T {
    if (this is T)
        return this
    else
        return default
}