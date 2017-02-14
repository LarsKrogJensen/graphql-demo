package se.lars;


import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class Monad {

    private static class SomeInt {
        public int value;

        public SomeInt(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "SomeInt{" +
                    "value=" + value +
                    '}';
        }
    }

    public static void main(String[] args) {
        exploreOptional();
        exploreFunctionCompose();




        List<SomeInt> list = Arrays.asList(new SomeInt(1),
                new SomeInt(2),
                new SomeInt(3),
                new SomeInt(4));
        list.stream()
            //.boxed() // Converts Intstream to Stream<Integer>
            .sorted((o2, o1) -> Integer.compare(o1.value, o2.value)) // Method on Stream<Integer>
            .forEach(System.out::println);
        
        List<Optional<SomeInt>> list2 = Arrays.asList(
                Optional.empty(),
                Optional.of(new SomeInt(1)),
                Optional.of(new SomeInt(21)),
                Optional.empty(),
                Optional.of(new SomeInt(2)));


        list2.stream()
             .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
             .forEach(System.out::println);

        Optional<SomeInt> reduce = list2.stream()
                                        .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                                        .sorted(Comparator.comparingInt(o2 -> o2.value))
                                        .reduce((a, b) -> b);  // take last
        System.out.println(reduce);


        IntFunction<IntUnaryOperator> sum = x -> (y -> x + y);
        IntUnaryOperator plus10 = sum.apply(10);

        System.out.println(plus10.applyAsInt(1));
    }

    private static void exploreFunctionCompose() {

        Function<String, Integer> len = String::length;

        Function<String, String> f = len.andThen(x -> "Length " + x);

        String lars = f.apply("lars");
        System.out.println(lars);


    }

    private static void exploreOptional() {
        Optional<Person> lars = Optional.of(new Person(new Address(new City(null))));
//        Optional<Person> lars = Optional.of(new Person(new Address(new City("stockholm"))));

        String sdsdsd = lars.flatMap(Person::getAddress)
                            .flatMap(Address::getCity)
                            //.filter()
                            .flatMap(City::getName)
                            .map(Function.identity())
                            .orElse("sdsdsd");
        //.ifPresent(System.out::println);
        System.out.println(sdsdsd);
    }


    public static class Person {
        private Address adress;

        public Person(Address adress) {
            this.adress = adress;
        }

        public Optional<Address> getAddress() {
            return Optional.of(adress);
        }
    }

    public static class Address {
        private final City city;

        public Address(City city) {
            this.city = city;
        }

        public Optional<City> getCity() {
            return Optional.ofNullable(city);
        }

    }

    public static class City {
        private String name;

        public City(String name) {
            this.name = name;
        }

        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }
    }
}