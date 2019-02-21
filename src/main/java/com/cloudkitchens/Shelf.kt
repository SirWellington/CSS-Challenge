/*
 * Copyright 2019 SirWellington
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudkitchens


import com.cloudkitchens.ShelfType.OVERFLOW
import com.cloudkitchens.Temperature.COLD
import com.cloudkitchens.Temperature.FROZEN
import com.cloudkitchens.Temperature.HOT
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern.Role.FACTORY_METHOD
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern.Role.PRODUCT
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE
import java.util.concurrent.LinkedBlockingDeque

/**
 * Where orders are placed once they are ready for delivery.
 *
 * @author SirWellington
 */
//===========================================
// SHELVES
//===========================================
@StrategyPattern(role = INTERFACE)
@FactoryMethodPattern(role = PRODUCT)
interface Shelf
{
    val capacity: Int
    val type: ShelfType
    val items: List<Order>
    val size: Int get() = items.size
    val isAtCapacity: Boolean get() = size >= capacity
    val notFull get() = !isAtCapacity

    fun addOrder(order: Order)
    fun display(): List<OrderDetail>
    fun pickupOrder(orderId: String): Order?
    fun removeWasteItems()
    fun removeItemWithTemperature(temperature: Temperature): Order?

    companion object Factory
    {

        @FactoryMethodPattern(role = FACTORY_METHOD)
        fun ofType(type: ShelfType): Shelf
        {
            val capacity = if (type == OVERFLOW) 20 else 15
            return ShelfImpl(type = type, capacity = capacity)
        }
    }
}

enum class ShelfType
{
    HOT,
    COLD,
    FROZEN,
    OVERFLOW
}

data class OrderDetail(val order: Order,
                       val normalizedValue: Double)

//===========================================
// IMPLEMENTATION
//===========================================
@StrategyPattern(role = CONCRETE_BEHAVIOR)
internal class ShelfImpl(override val type: ShelfType,
                         override val capacity: Int = 15): Shelf
{

    private val orders = LinkedBlockingDeque<Order>()

    override val items: List<Order>
        get() =  orders.toList()

    override val size: Int
        get() = orders.size

    override fun addOrder(order: Order)
    {
        orders.add(order)
    }

    override fun pickupOrder(orderId: String): Order?
    {
        val order = orders.firstOrNull { it.id == orderId } ?: return null
        orders.remove(order)

        return order
    }

    override fun display(): List<OrderDetail>
    {
        val details = orders.map()
        {
            OrderDetail(order = it,
                        normalizedValue = it.normalizedValue)
        }

        return details
    }

    override fun removeWasteItems()
    {
        orders.removeIf { it.isWaste }
    }

    override fun removeItemWithTemperature(temperature: Temperature): Order?
    {
        val first = orders.firstOrNull { it.request.temp == temperature } ?: return null
        orders.remove(first)
        return first
    }
}

//===========================================
// SHELF SET
//===========================================
@FactoryMethodPattern(role = PRODUCT)
interface ShelfSet
{

    val hot: Shelf
    val cold: Shelf
    val frozen: Shelf
    val overflow: Shelf

    val shelves get() = listOf(hot, cold, frozen, overflow)

    fun addOrder(order: Order)

    fun pickupOrder(orderId: String): Order?

    fun removeWaste()
    {
        shelves.forEach { it.removeWasteItems() }
    }

    companion object Factory
    {

        @FactoryMethodPattern(role = FACTORY_METHOD)
        fun newDefaultShelfSet(events: GlobalEvents): ShelfSet
        {
            val hot = Shelf.ofType(ShelfType.HOT)
            val cold = Shelf.ofType(ShelfType.COLD)
            val frozen = Shelf.ofType(ShelfType.FROZEN)
            val overflow = Shelf.ofType(ShelfType.OVERFLOW)

            return ShelfSetImpl(events = events,
                                hot = hot,
                                cold = cold,
                                frozen = frozen,
                                overflow = overflow)
        }
    }

}

//===========================================
// SHELF SET IMPL
//===========================================
internal class ShelfSetImpl(private val events: GlobalEvents,
                            override val hot: Shelf,
                            override val cold: Shelf,
                            override val frozen: Shelf,
                            override val overflow: Shelf): ShelfSet
{
    private val LOG = getLogger()

    override fun addOrder(order: Order)
    {
        LOG.info("Adding order [${order.id}] to shelf set")

        _addOrder(order)
    }

    private fun _addOrder(order: Order, didOverflow: Boolean = false)
    {
        val shelf = when (order.request.temp)
        {
            COLD   -> cold
            HOT    -> hot
            FROZEN -> frozen
        }

        when
        {
            shelf.notFull    ->
            {
                shelf.addOrder(order)
                events.onOrderAddedToShelf(order, this, shelf)
            }
            overflow.notFull ->
            {
                overflow.addOrder(order)
                events.onOrderAddedToShelf(order, this, overflow)
            }
            else             ->
            {
                LOG.warn("Both the [${shelf.type}] and the Overflow shelves are full! Clearing inventory.")
                removeWaste()

                if (!didOverflow)
                {
                    _addOrder(order, didOverflow = true)
                }
                else
                {
                    dispose(order)
                }
            }
        }
    }

    override fun pickupOrder(orderId: String): Order?
    {
        return shelves.map { it.pickupOrder(orderId) }.firstOrNull()
    }

    private fun dispose(order: Order)
    {
        LOG.info("Disposed of order [${order.id}]")
        events.onOrderDiscarded(order)
    }

}