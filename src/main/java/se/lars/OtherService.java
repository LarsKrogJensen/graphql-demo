package se.lars;

import rx.Observable;


public class OtherService
{
    private IMyService _service;

    public OtherService(IMyService service)
    {
        _service = service;
    }

    public Observable<String> doSomething()
    {
        return Observable.create(subscriber -> {
            _service.getData()
                    .thenAccept(stringStream -> {
                        stringStream.peek(System.out::println)
                                    .forEach(subscriber::onNext);
                        subscriber.onCompleted();
                    });
        });
    }
}
