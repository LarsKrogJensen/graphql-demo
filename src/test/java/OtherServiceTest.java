import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.mockito.Mock;
import rx.observers.TestSubscriber;
import se.six.lars.IMyService;
import se.six.lars.OtherService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

public class OtherServiceTest
{
    @Mock
    private IMyService _service;

    @InjectMocks
    private OtherService _classUnderTest;



    @Before
    public void setUp()
        throws Exception
    {
        initMocks(this);
    }

    @Test
    public void verifyDoSomehting()
        throws Exception
    {
        when(_service.getData())
               .thenReturn(CompletableFuture.completedFuture(Stream.of("lars", "krog-jensen")));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        _classUnderTest.doSomething().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Arrays.asList("lars", "krog-jensen"));

        verify(_service, never() ).getData2();
        verify(_service, never() ).getData3();
        verify(_service, times(1) ).getData();

         validateMockitoUsage();
    }

    @Test
    public void verifyDoSomehting_MoreAsync()
        throws Exception
    {
        when(_service.getData())
               .thenReturn(CompletableFuture.supplyAsync(() -> Stream.of("lars", "krog-jensen")));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        _classUnderTest.doSomething().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Arrays.asList("lars", "krog-jensen"));

    }

}
