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

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThanOrEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tech.sirwellington.alchemy.generator.NumberGenerators
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.Repeat


//===========================================
// LINEAR TEST
//===========================================
class LinearPoissonGeneratorTest: PoissonGeneratorTest()
{

    override fun createInstance(): PoissonGenerator
    {
        return PoissonGenerator.KNUTH
    }

}

//===========================================
// KNUTH TEST
//===========================================
class KnuthPoissonGeneratorTest: PoissonGeneratorTest()
{

    override fun createInstance(): PoissonGenerator
    {
        return PoissonGenerator.KNUTH
    }
}

//===========================================
// BASE TEST CLASS
//===========================================
@RunWith(AlchemyTestRunner::class)
@Repeat
abstract class PoissonGeneratorTest
{

    protected lateinit var instance: PoissonGenerator

    @Before
    fun setup()
    {
        instance = createInstance()
    }

    abstract fun createInstance(): PoissonGenerator

    @Test
    fun testGetPoisson()
    {
        val λ = NumberGenerators.doubles(0.0, 100.0).get()
        val result = instance.getPoisson(λ)
        assertThat(result, greaterThanOrEqualTo(0))
    }

}