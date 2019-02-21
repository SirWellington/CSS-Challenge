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

import tech.sirwellington.alchemy.kotlin.extensions.anyElement
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue


//===========================================
// ORDER
//===========================================

/**
 * Represents an incoming order to the system.
 *
 * @param name The name of the order
 * @param temp The temperature of the food, as measured in [Temperature]
 * @param shelfLife Read in seconds
 * @param decayRate The rate at which this order loses its value.
 *                  Once the value of an order reaches 0, it is defined as waste and should be removed from the shelf.
 * @author SirWellington
 */
data class OrderRequest(val name: String,
                        val temp: Temperature,
                        val shelfLife: Int,
                        val decayRate: Double)

/**
 * Represents an order that has been fulfilled by the [Kitchen].
 *
 * @param request The incoming request that generated this order.
 * @param id The unique ID of this order.
 * @param timeOfOrder The time this order was created.
 * @author SirWellington
 */
data class Order(val request: OrderRequest,
                 val id: String,
                 val timeOfOrder: ZonedDateTime = ZonedDateTime.now())
{
    val shelfLife get() = request.shelfLife
    val decayRate get() = request.decayRate

    fun decayRateIn(shelf: Shelf): Double
    {
        return when (shelf.type)
        {
            ShelfType.OVERFLOW -> decayRate * 2
            else               -> decayRate
        }
    }

    /**
     * Returns how the age of the order, in seconds.
     */
    val orderAge: Long
        get()
        {
            val now = ZonedDateTime.now()
            return timeOfOrder.until(now, ChronoUnit.SECONDS).absoluteValue
        }

    /** Determines the value of the current order.*/
    val value: Double
        get()
        {
            //value = ([shelf life] - [order age]) - ([decay rate] * [order age])
            return (shelfLife - orderAge) - (decayRate * orderAge)
        }

    val normalizedValue: Double
        get() = value / shelfLife.toDouble()

    /** Determines whether this order is now waste and should be removed. */
    val isWaste get() = value <= 0.0
}


enum class Temperature
{

    FROZEN,
    COLD,
    HOT

    ;

    companion object
    {
        @JvmStatic
        val all = values().toList()

        @JvmStatic
        val any get() = all.anyElement ?: HOT

    }
}
