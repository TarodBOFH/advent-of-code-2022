package org.tarodbofh.advent

import kotlin.coroutines.coroutineContext

private fun String.asInt() = this.toIntOrNull() ?: 0

fun main() {
    day1()
    day2()
}


fun day1() {
    val lines = getResourceAsStream("/input1.txt").lines()
    var elf = 0
    val group = lines.groupingBy { if (it.isEmpty()) { elf++ } else elf }.fold(0) { acc, it -> acc + it.asInt() }

    println(group.maxBy { it.value })
    println(group.values.sortedDescending().filterIndexed { index, _ -> index < 3  }.sum())
}

fun day2() {

    // compute all possible results for first problem (second column is what to play)
    val resultMatrix1 = RockPaperScissors.values().map { opponent ->
        RockPaperScissors.values().map { play ->
            opponent.opponent + " " + play.play to play.score(opponent)
        }
    }.flatten().toMap()

    // compute all possible results for first problem (second column is how to play)
    val resultMatrix2 = RockPaperScissors.values().map { opponent ->
        Results.values().map { result ->
            result.findPlay(opponent).let { opponent.opponent + " " + result.string to it.score(opponent) }
        }
    }.flatten().toMap()

    val lines = getResourceAsStream("/input2.txt").lines()
    listOf(resultMatrix1, resultMatrix2).forEach { resultMatrix ->
        // find the score for the given result in the precomputed, accumulate it
        println(lines.fold(0) { acc, it -> resultMatrix[it]!! + acc })
    }
}


enum class RockPaperScissors(private val score: Int, val opponent: String, val play: String) {

    ROCK(1,"A", "X"), PAPER(2, "B", "Y"), SCISSORS(3, "C", "Z");

    fun score(target: RockPaperScissors):Int = when {
        this.wins(target) -> this.score + 6
        this.loses(target) -> this.score
        else -> this.score + 3
    }

    fun wins(target: RockPaperScissors) = when(this) {
        ROCK -> target == SCISSORS
        PAPER -> target == ROCK
        SCISSORS -> target == PAPER
    }

    fun loses(target: RockPaperScissors) = this != target && this.wins(target).not()
}

enum class Results(val string: String) {
    LOSE("X"), DRAW("Y"), WIN("Z");

    fun findPlay(opponent: RockPaperScissors) = when(this) {
        LOSE -> RockPaperScissors.values().first { it.loses(opponent)}
        DRAW -> opponent
        WIN -> RockPaperScissors.values().first { it.wins(opponent)}
    }
}


private val singleton = object {}
fun getResourceAsStream(path: String): String = singleton.javaClass.getResource(path)?.readText()!!
