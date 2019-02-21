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

import org.checkerframework.checker.units.qual.Temperature
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

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

//===========================================
// ORDER
//===========================================

/**
 *
 * @param name The name of the order
 * @param temp The temperature of the food, as measured in [Temperature]
 * @param shelfLife Read in seconds
 * @param decayRate The rate at which this order loses its value.
 *                  Once the value of an order reaches 0, it is defined as waste and should be removed from the shelf.
 * @author SirWellington
 */
data class Order(val name: String,
                 val temp: Temperature,
                 val shelfLife: Int,
                 var decayRate: Double,
                 val orderTime: ZonedDateTime)
{

    /**
     * Returns how the age of the order, in seconds.
     */
    val orderAge: Long
        get()
        {
            val now = ZonedDateTime.now()
            return orderTime.until(now, ChronoUnit.SECONDS).absoluteValue
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
}
