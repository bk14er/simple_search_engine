package search

import java.io.File

val TEST_DATA = """
    Dwight Joseph djo@gmail.com
    Rene Webb webb@gmail.com
    Katie Jacobs
    Erick Harrington harrington@gmail.com
    Myrtle Medina
    Erick Burgess
""".trimIndent()

val MENU = """
    === Menu ===
    1. Find a person
    2. Print all people
    0. Exit
""".trimIndent()

enum class SearchStrategy {
    ALL, ANY, NONE
}

fun main(args: Array<String>) {
    if (args.size != 2 || args[0] != "--data") {
        println("Usage: --data <filename>.txt")
        return
    }
    val persons = readPersons(args.last())

    while (true) {
        println(MENU)
        when (readln().toInt()) {
            1 -> findPerson(persons)
            2 -> printAllPeople(persons)
            0 -> {
                println("Bye!")
                break
            }

            else -> println("Incorrect option! Try again.")
        }
    }
}


data class Person(val firstName: String, val lastName: String?, val email: String?) {

    override fun toString(): String = "$firstName ${lastName ?: ""} ${email ?: ""}".trim()

    operator fun contains(query: String): Boolean =
        listOfNotNull(firstName, lastName, email).any { it.contains(query, ignoreCase = true) }

    companion object {
        fun createPerson(index: Int, input: String): Pair<Int, Person> {
            // val (index, value) = input.split(": ")
            val person = input.split(" ")
            return index to Person(person[0], person.getOrNull(1), person.getOrNull(2))
        }
    }
}

class Persons(private val persons: Map<Int, Person>) {

    private val index = persons.flatMap { (index, person) ->
        listOfNotNull(person.firstName, person.lastName, person.email).flatMap { word ->
            word.split(" ").map { it.lowercase() to index }
        }
    }.groupBy({ it.first }, { it.second })

    fun search(query: String, strategy: SearchStrategy): List<Person> {
        val words =
            query.lowercase().split(" ")
                .map { index.getOrDefault(it, listOf()) }

        return when (strategy) {
            SearchStrategy.ALL -> words
                .reduce { acc, list ->
                    ArrayList(acc).intersect(list).toList()
                }.mapNotNull { persons[it] }

            SearchStrategy.ANY -> words
                .reduce { acc, list -> val copy = ArrayList(acc); copy.addAll(list); copy }
                .mapNotNull { persons[it] }

            SearchStrategy.NONE -> words.map { persons.values.indices.toList() - it }
                .reduce { acc, list -> ArrayList(acc).intersect(list).toList() }
                .mapNotNull { persons[it] }
        }
    }

    fun print() = persons.values.joinToString("\n")
}

private fun findPerson(persons: Persons) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = SearchStrategy.valueOf(readln().uppercase())

    println("Enter a name or email to search all matching people.")
    println(persons.search(readln(), strategy).print())
}

fun List<Person>.print(): String =
    if (isEmpty()) "No matching people found. \n" else "$size persons found: \n${joinToString("\n")}"

private fun printAllPeople(persons: Persons) {
    println("=== List of people ===")
    println(persons.print())
}

fun readPersons(file: String): Persons {
    val file = File(file)
    println("Reading data from file ${file.absoluteFile}")
    val peoples =
        //TEST_DATA .split("\n")
        file.readLines()
            .mapIndexed { index, line -> Person.createPerson(index, line) }.toMap()
    return Persons(peoples)
}
