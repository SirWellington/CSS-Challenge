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

import com.cloudkitchens.Generators.OrderAlchemyGenerator
import com.cloudkitchens.ShelfType.OVERFLOW
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isEmpty
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import tech.sirwellington.alchemy.test.hamcrest.isNull
import tech.sirwellington.alchemy.test.hamcrest.notEmpty
import tech.sirwellington.alchemy.test.hamcrest.notNullOrEmpty
import tech.sirwellington.alchemy.test.junit.runners.*

@RunWith(AlchemyTestRunner::class)
@Repeat
class ShelfTest
{

    private lateinit var shelf: Shelf

    @Mock
    private lateinit var display: Display

    @GenerateEnum
    private lateinit var type: ShelfType

    @GenerateCustom(Generators.OrderAlchemyGenerator::class)
    private lateinit var order: Order
    private val orderId get() = order.id

    @GenerateList(value = Order::class,size = 25, customGenerator = OrderAlchemyGenerator::class)
    private lateinit var orders: List<Order>

    @Before
    fun setup()
    {
        shelf = Shelf.ofType(type)
    }

    @Test
    fun testGetItemsWhenNone()
    {
        val result = shelf.items
        assertThat(result, isEmpty)
    }

    @Test
    fun testGetItemsWhenOne()
    {
        shelf.addOrder(order)
        val result = shelf.items
        assertThat(result, notEmpty and hasElement(order))
    }

    @Test
    fun testGetItemsWhenMultiple()
    {
        orders.forEach(shelf::addOrder)

        val result = shelf.items
        assertThat(result, notNullOrEmpty)

        orders.forEach()
        {
            assertThat(result, hasElement(it))
        }
    }

    @Test
    fun testAddOrder()
    {
        shelf.addOrder(order)
        val result = shelf.pickupOrder(orderId)
        assertThat(result, equalTo(order))
    }

    @Test
    fun testAddOrderTwice()
    {
        shelf.addOrder(order)
        shelf.addOrder(order)

        val result = shelf.pickupOrder(orderId)
        assertThat(result, equalTo(order))
    }

    @Test
    fun testPickupOrderWhenNone()
    {
        val result = shelf.pickupOrder(orderId)
        assertThat(result, isNull)
    }

    @Test
    fun testPickupOrderWhenOne()
    {
        shelf.addOrder(order)
        val result = shelf.pickupOrder(orderId)
        assertThat(result, equalTo(order))
    }

    @Test
    fun testDisplayWhenEmpty()
    {
        shelf.displayOn(display)
        verify(display).displayOrders(emptyList())
    }

    @Test
    fun testDisplayWhenOne()
    {
        shelf.addOrder(order)
        shelf.displayOn(display)
        verify(display).displayOrders(listOf(order))
    }

    @Test
    fun testDisplayWhenMultiple()
    {
        orders.forEach(shelf::addOrder)

        shelf.displayOn(display)

        verify(display).displayOrders(check { orders ->
            val size = orders.size
            assertThat(size, equalTo(this.orders.size))
            orders.forEach { assertThat(this.orders, hasElement(it)) }
        })
    }

    @Test
    fun testRemoveWasteItemsWhenEmpty()
    {
        shelf.removeWasteItems()
    }

    @Test
    fun testGetType()
    {
        assertThat(shelf.type, equalTo(type))
    }

    @Test
    fun testGetCapacity()
    {
        val capacity = shelf.capacity
        val expected = if (type == OVERFLOW) 20 else 15
        assertThat(capacity, equalTo(expected))
    }

}