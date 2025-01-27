@file:JvmName("TableUtil")
@file:Suppress("unused")

package io.github.hiiragi283.material.common.util

import com.google.common.collect.Table

//    Table    //

fun <R, C, V> Table<R, C, V>.toList(): List<Triple<R, C, V>> {
    if (size() == 0) return emptyList()
    val iterator = cellSet().iterator()
    if (!iterator.hasNext()) return emptyList()
    val first = iterator.next()
    if (!iterator.hasNext()) return listOf(first.toTriple())
    val result = ArrayList<Triple<R, C, V>>(size())
    result.add(first.toTriple())
    do {
        result.add(iterator.next().toTriple())
    } while (iterator.hasNext())
    return result
}

inline fun <R, C, V, T> Table<out R, out C, V>.mapNotNull(transform: (Table.Cell<out R, out C, V>) -> T): List<T> {
    return mapNotNullTo(ArrayList(size()), transform)
}

inline fun <R, C, V, T> Table<out R, out C, V>.map(transform: (Table.Cell<out R, out C, V>) -> T): List<T> {
    return mapTo(ArrayList(size()), transform)
}

inline fun <R, C, V, T, U : MutableCollection<in T>> Table<out R, out C, V>.mapNotNullTo(
    destination: U,
    transform: (Table.Cell<out R, out C, V>) -> T
): U {
    forEach { cell -> transform(cell)?.let { destination.add(it) } }
    return destination
}

inline fun <R, C, V, T, U : MutableCollection<in T>> Table<out R, out C, V>.mapTo(
    destination: U,
    transform: (Table.Cell<out R, out C, V>) -> T
): U {
    for (cell in cellSet()) destination.add(transform(cell))
    return destination
}

inline fun <R, C, V> Table<R, C, V>.forEach(action: (Table.Cell<R, C, V>) -> Unit) {
    for (cell in this.cellSet()) action(cell)
}

inline fun <R, C, V> Table<R, C, V>.computeIfAbsent(row: R, column: C, mapping: (R, C) -> V): V {
    var result: V? = this.get(row, column)
    if (result == null) {
        this.put(row, column, mapping(row, column))
        result = this.get(row, column)!!
    }
    return result
}

//    Cell    //

fun <R, C, V> Table.Cell<R, C, V>.toTriple(): Triple<R, C, V> = Triple(rowKey, columnKey, value)

operator fun <R, C, V> Table.Cell<R, C, V>.component1(): R = this.rowKey

operator fun <R, C, V> Table.Cell<R, C, V>.component2(): C = this.columnKey

operator fun <R, C, V> Table.Cell<R, C, V>.component3(): V = this.value