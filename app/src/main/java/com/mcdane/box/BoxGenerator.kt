package com.mcdane.box

import kotlin.random.Random

class BoxGenerator(maxLevel: Int) {
    private val boxTypes: List<Int> =
        ArrayList<Int>().also {
            for ((type, config) in Box.configs.withIndex()) {
                if(config.level <= maxLevel) it.add(type)
            }
        }

    private val totalWeight: Int =
        boxTypes.sumOf { Box.configs[it].weight }

    fun generate(box: Box) {
        box.type = randomType()
        box.index = randomIndex()
    }

    private fun randomType(): Int {
        val weightSum = Random.nextInt(0, totalWeight + 1)
        var s = 0
        for (type in boxTypes) {
            s += Box.configs[type].weight
            if (weightSum <= s) return type
        }
        return boxTypes.last()
    }

    private fun randomIndex(): Int =
        Random.nextInt(0, Box.INDEX_COUNT)
}