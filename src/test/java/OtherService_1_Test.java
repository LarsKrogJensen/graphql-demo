import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.observers.TestSubscriber;
import se.lars.IMyService;
import se.lars.OtherService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;

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
