package se.six.lars


class Publisher {

    List<Subscriber> subscribers = []

    def send(String message) {
        subscribers*.receive(message)
    }
}

interface Subscriber {
    def receive(String message)
}