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

package com.cloudkitchens.driver

import com.cloudkitchens.*
import tech.sirwellington.alchemy.generator.PeopleGenerators
import java.util.concurrent.ScheduledExecutorService

/**
 * Responsible for dispatching drivers in response to [new orders][GlobalEvents.onOrderPrepared].
 *
 * @author SirWellington
 */
class Dispatcher(private val events: GlobalEvents,
                 private val scheduler: ScheduledExecutorService,
                 private val trafficDelayRange: IntRange): EventListener
{

    private val LOG = getLogger()

    init
    {
        events.subscribe(this)
    }

    private fun driverForOrder(order: Order): Driver
    {
        val name = PeopleGenerators.names().get()

        return Driver(driverId = name,
                      events = events,
                      scheduler = scheduler,
                      trafficDelayRange = trafficDelayRange)
    }

    override fun onOrderAddedToShelf(order: Order, shelfSet: ShelfSet, shelf: Shelf)
    {
        val driver = driverForOrder(order)
        LOG.info("Dispatching driver [${driver.driverId}] to pickup order [${order.id}]")
        driver.respondToOrder(order.id, shelfSet)
    }

}
