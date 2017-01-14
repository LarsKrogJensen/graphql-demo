package se.six.lars

import org.spockframework.compiler.model.SetupBlock
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.stream.Stream


class OtherServiceSpockTest extends Specification {
    def "length of Spock's and his friends' names"() {
        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }

    def "service does what i tell it"() {
        given:
        def svc = Mock(IMyService) {
            getData() >> CompletableFuture.completedFuture(Stream.of(data))
        }

        and:
        def testObj = new OtherService(svc)
        def sub = new TestSubscriber<String>()

        when:
        testObj.doSomething().subscribe(sub)

        then:
        interaction {
            sub.onNextEvents == data
        }

        where:
        data = ["lars", "krog-jensen"]

    }

}
