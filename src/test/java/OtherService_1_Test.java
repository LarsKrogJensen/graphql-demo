import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;
import se.six.lars.IMyService;
import se.six.lars.OtherService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class OtherService_1_Test
{
    @Mock
    private IMyService _service;

    @InjectMocks
    private OtherService _classUnderTest;


    @Test
    public void verifyDoSomehting()
        throws Exception
    {
        given(_service.getData())
            .willReturn(CompletableFuture.completedFuture(Stream.of("lars", "krog-jensen")));

        //when(_service.getData())
        //       .thenReturn(CompletableFuture.completedFuture(Stream.of("lars", "krog-jensen")));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        _classUnderTest.doSomething().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Arrays.asList("lars", "krog-jensen"));

        verify(_service, never() ).getData2();
        verify(_service, never() ).getData3();
        verify(_service, times(1) ).getData();





    }


}
