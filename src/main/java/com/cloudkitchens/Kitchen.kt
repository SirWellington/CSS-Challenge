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

import com.cloudkitchens.Temperature.COLD
import com.cloudkitchens.Temperature.FROZEN
import com.cloudkitchens.Temperature.HOT
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern.Role.FACTORY_METHOD
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern.Role.PRODUCT
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE

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
 * A [Kitchen] is responsible for receiving an fulfilling
 * orders.
 *
 * @author SirWellington
 */
@FactoryMethodPattern(role = PRODUCT)
@StrategyPattern(role = INTERFACE)
interface Kitchen
{

    /**
     * Receives an order request, prepares it, and places it on a shelf.
     * @return The [Order] that was just prepared.
     */
    fun receiveOrder(request: OrderRequest): Order

    companion object Factory
    {

        @FactoryMethodPattern(role = FACTORY_METHOD)
        fun newCaliforniaKitchen(shelfSet: ShelfSet = ShelfSet.createDefaultShelfSet()): Kitchen
        {
            return CaliforniaKitchen(shelfSet)
        }
    }

}

//===========================================
// IMPLEMENTATION
//===========================================
@StrategyPattern(role = CONCRETE_BEHAVIOR)
internal class CaliforniaKitchen(private val shelves: ShelfSet): Kitchen
{
    private val LOG = getLogger()

    override fun receiveOrder(request: OrderRequest): Order
    {
        val order = prepare(request)

        val shelf = when (request.temp)
        {
            COLD   -> shelves.cold
            HOT    -> shelves.hot
            FROZEN -> shelves.frozen
        }
        if (shelf.isAtCapacity)
        {
            shelves.overflow.addOrder(order)
        }
        else
        {
            shelf.addOrder(order)
        }

        return order
    }

    private fun prepare(request: OrderRequest): Order
    {
        LOG.info("Preparing order for [${request.name}]")
        return Order(request)
    }

}