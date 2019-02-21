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

package com.cloudkitchens/*
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
    val size: Int
    val capacity: Int
    val type: ShelfType
    val isAtCapacity: Boolean get() = size >= capacity
    val notFull get() = !isAtCapacity

    fun addOrder(order: Order)
    fun pickupOrder(): Order?
    fun display(): List<OrderDetail>
    fun removeWasteItems()

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

    override val size: Int
        get() = orders.size

    override fun addOrder(order: Order)
    {
        orders.add(order)
    }

    override fun pickupOrder(): Order?
    {
        return orders.poll()
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

    fun removeWaste()
    {
        shelves.forEach { it.removeWasteItems() }
    }

    companion object Factory
    {

        @FactoryMethodPattern(role = FACTORY_METHOD)
        fun newDefaultShelfSet(): ShelfSet
        {
            val hot = Shelf.ofType(ShelfType.HOT)
            val cold = Shelf.ofType(ShelfType.COLD)
            val frozen = Shelf.ofType(ShelfType.FROZEN)
            val overflow = Shelf.ofType(ShelfType.OVERFLOW)

            return ShelfSetImpl(hot = hot, cold = cold, frozen = frozen, overflow = overflow)
        }
    }

}

//===========================================
// SHELF SET IMPL
//===========================================
internal class ShelfSetImpl(override val hot: Shelf,
                            override val cold: Shelf,
                            override val frozen: Shelf,
                            override val overflow: Shelf): ShelfSet
{
    private val LOG = getLogger()

    override fun addOrder(order: Order)
    {
        LOG.info("Adding order [${order.request.name}] to shelf set")

        val shelf = when (order.request.temp)
        {
            COLD   -> cold
            HOT    -> hot
            FROZEN -> frozen
        }

        when
        {
            shelf.notFull    -> shelf.addOrder(order)
            overflow.notFull -> overflow.addOrder(order)
            else             ->
            {
                LOG.warn("Both the [${shelf.type}] and the Overflow shelves are full! Clearing inventory.")
                removeWaste()

                if (shelf.notFull)
                {
                    LOG.info("[${shelf.type}] shelf now has space. Adding order.")
                    shelf.addOrder(order)
                }
                else if (overflow.notFull)
                {
                    LOG.info("overflow shelf now has space. Adding order.")
                    overflow.addOrder(order)
                }
                else
                {
                    LOG.warn("No space available for incoming order [$order]. Disposing of it…")
                    dispose(order)
                }
            }
        }
    }

    private fun dispose(order: Order)
    {
        LOG.info("Disposed of order [${order.request.name}]")
    }
}