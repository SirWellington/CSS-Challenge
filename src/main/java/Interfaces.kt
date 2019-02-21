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
    val temparature: Temperature?

    fun addOrder(order: Order)
    fun pickupOrder(): Order?
    fun display(): List<OrderDetail>
    fun removeWasteItems()

}

data class OrderDetail(val order: Order,
                       val normalizedValue: Double)