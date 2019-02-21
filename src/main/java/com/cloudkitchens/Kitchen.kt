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
interface Kitchen
{

    /**
     * Receives an order request, prepares it, and places it on a shelf.
     * @return The [Order] that was just prepared.
     */
    fun receiveOrder(request: OrderRequest): Order

}

//===========================================
// IMPLEMENTATION
//===========================================
internal class CaliforniaKitchen(private val hotShelf: Shelf,
                                 private val coldShelf: Shelf,
                                 private val frozenShelf: Shelf,
                                 private val overflowShelf: Shelf): Kitchen
{
    private val LOG = getLogger()

    override fun receiveOrder(request: OrderRequest): Order
    {
        val order = prepare(request)

        val shelf = when (request.temp)
        {
            COLD   -> coldShelf
            HOT    -> hotShelf
            FROZEN -> frozenShelf
        }

        if (shelf.isAtCapacity)
        {
            overflowShelf.addOrder(order)
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