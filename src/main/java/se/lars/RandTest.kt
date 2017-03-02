package se.lars

import java.security.SecureRandom
import java.util.*


fun main(args: Array<String>) {
//    val rand : SecureRandom = SecureRandom();
//
//     for ( i in 1..999) {
//         val seed = rand.generateSeed(i);
//         println("seed ${i}")
//
//     }

    //val rand: SecureRandom = SecureRandom();

    for (i in 1..999) {
        val seed = UUID.randomUUID().toString()
        println("seed ${i} ${seed}")

    }
}