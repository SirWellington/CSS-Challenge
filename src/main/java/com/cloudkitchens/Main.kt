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

package com.cloudkitchens/*
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

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


/**
 *
 * @author SirWellington
 */
object Main
{
    private val LOG = getLogger()


    var gson = GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()

    @JvmStatic
    fun main(args: Array<String>)
    {
        LOG.info("Parsing orders")
        val orders = parseOrders()
        LOG.info("Parsed [${orders.size}] orders.")
    }

    internal fun parseOrders(): List<Order>
    {
        val file = this.javaClass.classLoader.getResource("data/sample-data.json") ?: return emptyList()
        val json = file.readText(Charsets.UTF_8)
        val type = object: TypeToken<List<Order>>() {}

        return try
        {
            gson.fromJson<List<Order>>(json, type.type)
        }
        catch (ex: Exception)
        {
            LOG.error("Failed to parse json from file", ex)
            return emptyList()
        }
    }

}
