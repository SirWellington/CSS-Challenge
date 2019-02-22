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

import com.cloudkitchens.driver.Dispatcher
import com.cloudkitchens.driver.InfiniteDispatcher
import tech.sirwellington.alchemy.kotlin.extensions.createListOf
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 *
 * @author SirWellington
 */
class OrderingSystem
{
    private val LOG = getLogger()


    private var scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(4)
    private var orderGenerator: OrderGenerator = OrderGenerator.fromResourceFile
    private var poissonGenerator = PoissonGenerator.KNUTH
    private var λ = 3.25
    private var events: GlobalEvents = GlobalEvents
    private var shelfSet: ShelfSet = ShelfSet.newDefaultShelfSet(events)
    private var kitchen: Kitchen = Kitchen.newCaliforniaKitchen(events = events, shelfSet = shelfSet)
    private var deliveryTimeRange = 2..10

    private var dispatcher: Dispatcher = InfiniteDispatcher(trafficDelayRange = deliveryTimeRange,
                                                            scheduler = scheduler)


    fun begin()
    {
        dispatcher.startListening(events)

        val generateOrders = Runnable { generateNewOrders() }
        scheduler.scheduleAtFixedRate(generateOrders, 0, 1, TimeUnit.SECONDS)
    }

    fun stop()
    {
        scheduler.shutdownNow()
    }

    private fun generateNewOrders()
    {
        val newOrderCount = poissonGenerator.getPoisson(λ)
        val newOrders = createListOf(newOrderCount)
        {
            orderGenerator.generateOrderRequest()
        }

        LOG.info("Generating [$newOrderCount] new orders…")
        val orders = newOrders.forEach { kitchen.receiveOrder(it) }
        LOG.info("Added [$newOrderCount] new orders to the kitchen")
    }

}