package se.lars.kutil

import io.vertx.core.buffer.Buffer
import org.joox.Context
import org.joox.JOOX
import org.joox.Match
import org.w3c.dom.Document
import java.nio.charset.Charset

fun documentOf(text: String) : Document {
    return JOOX.`$`(text.reader()).document()
}

fun documentOf(buffer: Buffer) : Match {
    return JOOX.`$`(documentOf(buffer.toString(Charset.forName("UTF-8"))))
}

fun matchOf(context: Context) = JOOX.`$`(context)