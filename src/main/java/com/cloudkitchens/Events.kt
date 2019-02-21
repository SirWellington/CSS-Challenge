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

import tech.sirwellington.alchemy.annotations.designs.patterns.SingletonPattern
import tech.sirwellington.alchemy.kotlin.extensions.asWeak
import tech.sirwellington.alchemy.kotlin.extensions.removeWhere
import java.lang.ref.WeakReference

/**
 * Listens for events across the system.
 */
interface EventListener
{
    fun onOrderReceived(request: OrderRequest)
    fun onOrderPrepared(order: Order)
    fun onOrderPickedUp(order: Order, driver: Driver)
    fun onOrderDelivered(order: Order, driver: Driver)
}

/**
 *
 * @author SirWellington
 */
@SingletonPattern
object GlobalEvents: EventListener
{

    private val listeners: MutableList<WeakReference<EventListener>> = mutableListOf()

    fun subscribe(listener: EventListener)
    {
        val weakRef = listener.asWeak()
        listeners.add(weakRef)
    }

    fun unsubscribe(listener: EventListener)
    {
        listeners.removeWhere { it.get() === listener }
    }

    override fun onOrderReceived(request: OrderRequest)
    {
        listeners.forEach{ it.get()?.onOrderReceived(request) }
    }

    override fun onOrderPrepared(order: Order)
    {
        listeners.forEach{ it.get()?.onOrderPrepared(order) }
    }

    override fun onOrderPickedUp(order: Order, driver: Driver)
    {
        listeners.forEach{ it.get()?.onOrderPickedUp(order, driver) }
    }

    override fun onOrderDelivered(order: Order, driver: Driver)
    {
        listeners.forEach{ it.get()?.onOrderDelivered(order, driver) }
    }

}

