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

import com.cloudkitchens.Temperature.COLD
import com.cloudkitchens.Temperature.FROZEN
import com.cloudkitchens.Temperature.HOT
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
interface Shelf
{
    val size: Int
    val capacity: Int
    val temperature: Temperature?
    val isAtCapacity: Boolean get() = size >= capacity

    fun addOrder(order: Order)
    fun pickupOrder(): Order?
    fun display(): List<OrderDetail>
    fun removeWasteItems()

    companion object
    {

        @JvmStatic
        fun createDefaultShelfSet(): ShelfSet
        {
            val hotShelf = ShelfImpl(optimalTemperature = HOT, capacity = 15)
            val coldShelf = ShelfImpl(optimalTemperature = COLD, capacity = 15)
            val frozenShelf = ShelfImpl(optimalTemperature = FROZEN, capacity = 15)
            val overflowShelf = ShelfImpl(optimalTemperature = null, capacity = 20)

            return object: ShelfSet
            {
                override val hotShelf: Shelf = hotShelf
                override val coldShelf: Shelf = coldShelf
                override val frozenShelf: Shelf = frozenShelf
                override val overflowShelf: Shelf = overflowShelf
            }
        }
    }

}

interface ShelfSet
{
    val hotShelf: Shelf
    val coldShelf: Shelf
    val frozenShelf: Shelf
    val overflowShelf: Shelf
}

data class OrderDetail(val order: Order,
                       val normalizedValue: Double)

//===========================================
// IMPLEMENTATION
//===========================================
internal class ShelfImpl(private val optimalTemperature: Temperature?,
                         override val capacity: Int = 15): Shelf
{

    private val orders = LinkedBlockingDeque<Order>()

    override val size: Int
        get() = orders.size

    override val temperature: Temperature?
        get() = optimalTemperature

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
        val details = orders.map {
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

