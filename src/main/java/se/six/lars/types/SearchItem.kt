package se.six.lars.types

data class SearchItem(val id: String,
                      val score: Float,
                      val name: String,
                      val longName: String)