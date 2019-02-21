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
     * Receives the order, prepares it, and places it on a shelf.
     */
    fun receiveOrder(order: Order)

}

//===========================================
// IMPLEMENTATION
//===========================================
internal class CaliforniaKitchen(val hotShelf: Shelf,
                                 val coldShelf: Shelf,
                                 val frozenShelf: Shelf,
                                 val overflowShelf: Shelf): Kitchen
{
    private val LOG = getLogger()

    override fun receiveOrder(order: Order)
    {
        prepare(order)

        val shelf = when (order.temp)
        {
            Temperature.COLD   -> coldShelf
            Temperature.HOT    -> hotShelf
            Temperature.FROZEN -> frozenShelf
        }

        if (shelf.isAtCapacity)
        {
            overflowShelf.addOrder(order)
        }
        else
        {
            shelf.addOrder(order)
        }
    }

    private fun prepare(order: Order)
    {
        LOG.info("Preparing order for [${order.name}]")
    }

}