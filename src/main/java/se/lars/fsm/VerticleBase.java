package se.lars.fsm;


import io.vertx.core.AbstractVerticle;

import static javaslang.API.*;
import static javaslang.Predicates.instanceOf;
import static javaslang.Predicates.is;
//import static org.immutables.value.internal.$generator$.$Intrinsics.$;

public class VerticleBase
    extends AbstractVerticle
{

    public void receive(String address)
    {

    }

}

class MyVerticle
    extends VerticleBase
{

    enum State
    {
        Init, Started
    }

    @Override
    public void start()
        throws Exception
    {


    }


    public static void main(String[] args)
    {
        Object msg = "2";

        State newState = Match(msg).of(
            Case(instanceOf(Integer.class), (value) -> State.Init),
            Case(is("2"), State.Started),
            Case($(), State.Init)
        );
        System.out.println(newState);
    }
}

