package se.lars

import spock.lang.Specification


class Publisher2Spec extends Specification {
    def publisher = new Publisher()
    def sub1 = Mock(Subscriber)
    def sub2 = Mock(Subscriber)

    def setup() {
        publisher.subscribers << sub1 << sub2
    }

    def "should send to all subscribers"()
    {

        when:
        publisher.send(data)

        then:
        1* sub2.receive(data)
        1 * sub1.receive(data)

        where:
        data = "hello"
    }
}
