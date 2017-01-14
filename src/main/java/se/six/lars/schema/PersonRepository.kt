package se.six.lars.schema

data class Person(val id: Int, val firstName: String, val lastName: String)


object PersonRepository {
    private val repository = mutableMapOf(12 to Person(12, "lars", "krog-jensen"),
                                          13 to Person(14, "kalle", "kula"),
                                          14 to Person(156, "snurre", "skutt"))

    fun addPerson(id: Int, firstName: String, lastName: String): Person {
        val p = Person(id, firstName, lastName)
        repository[id] = p
        return p
    }

    fun removePerson(id: Int): Person? {
        return repository.remove(id)
    }

    fun allPersons() = repository.values.toList()
}