/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package reactivefilereadingjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReadStreamTest {
    @Mock
    FileParser fileParser;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test public void testSubscriberCanReadFileContents() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenReturn(0L);
        when(fileParser.readContents()).thenReturn("myfilecontents");


        TestSubscriber<String> testSubscriber = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler).test();

        fileModifiedScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        testSubscriber.assertValue("myfilecontents");
        verify(fileParser, times(1)).readContents();
        testSubscriber.assertNotComplete();
    }

    @Test public void testSecondSubscriberDoesNotCreateFileRead() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenReturn(0L);
        when(fileParser.readContents()).thenReturn("myfilecontents");

        Flowable<String> fileContentsObservable = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler);

        TestSubscriber<String> testSubscriber = fileContentsObservable.test();
        fileModifiedScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testSubscriber.assertValue("myfilecontents");

        TestSubscriber<String> lateSubscriber = fileContentsObservable.test();
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        verify(fileParser, times(1)).readContents();
        testSubscriber.assertNotComplete();
    }


    @Test public void testLateSubscriberCanReadFileContents() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenReturn(0L);
        when(fileParser.readContents()).thenReturn("myfilecontents");

        Flowable<String> fileContentsObservable = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler);

        TestSubscriber<String> testSubscriber = fileContentsObservable.test();
        fileModifiedScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testSubscriber.assertValue("myfilecontents");

        TestSubscriber<String> lateSubscriber = fileContentsObservable.test();
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        lateSubscriber.assertValue("myfilecontents");
        testSubscriber.assertNotComplete();
    }

    @Test public void testSubscribersReceiveUpdates() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenReturn(0L).thenReturn(1L);
        when(fileParser.readContents()).thenReturn("myfilecontents").thenReturn("newmyfilecontents");

        Flowable<String> fileContentsObservable = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler);

        TestSubscriber<String> testSubscriber = fileContentsObservable.test();
        TestSubscriber<String> lateSubscriber = fileContentsObservable.test();

        fileModifiedScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testSubscriber.assertValue("myfilecontents");
        lateSubscriber.assertValue("myfilecontents");

        fileModifiedScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testSubscriber.assertValueAt(1, "newmyfilecontents");
        lateSubscriber.assertValueAt(1, "newmyfilecontents");
        testSubscriber.assertNotComplete();
    }

    @Test public void testFileUpdateOnlyLeadsToOneRead() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenReturn(0L).thenReturn(1L);
        when(fileParser.readContents()).thenReturn("myfilecontents").thenReturn("newmyfilecontents");

        Flowable<String> fileContentsObservable = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler);

        TestSubscriber<String> testSubscriber = fileContentsObservable.test();
        TestSubscriber<String> lateSubscriber = fileContentsObservable.test();

        fileModifiedScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        verify(fileParser, times(2)).readContents();
        testSubscriber.assertNotComplete();
    }


    @Test public void testReadContentsErrorDoesNotKillStream() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenReturn(0L).thenReturn(1L);
        when(fileParser.readContents()).thenThrow(FileNotFoundException.class).thenReturn("newmyfilecontents");

        Flowable<String> fileContentsObservable = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler);

        TestSubscriber<String> testSubscriber = fileContentsObservable.test();
        TestSubscriber<String> lateSubscriber = fileContentsObservable.test();

        fileModifiedScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        testSubscriber.assertValue("newmyfilecontents");
        lateSubscriber.assertValue("newmyfilecontents");

        verify(fileParser, times(2)).readContents();
        testSubscriber.assertNotComplete();
    }


    @Test public void testModifiedTimestampErrorDoesNotKillStream() throws InterruptedException, IOException {
        TestScheduler fileModifiedScheduler = new TestScheduler();
        TestScheduler downstreamScheduler = new TestScheduler();

        when(fileParser.getLastModifiedTimestamp()).thenThrow(NullPointerException.class).thenReturn(1L);
        when(fileParser.readContents()).thenReturn("myfilecontents");

        Flowable<String> fileContentsObservable = ReadStream.file_contents_observable(fileParser, fileModifiedScheduler, downstreamScheduler);

        TestSubscriber<String> testSubscriber = fileContentsObservable.test();
        TestSubscriber<String> lateSubscriber = fileContentsObservable.test();

        fileModifiedScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        downstreamScheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        testSubscriber.assertValue("myfilecontents");
        lateSubscriber.assertValue("myfilecontents");

        verify(fileParser, times(1)).readContents();
        testSubscriber.assertNotComplete();
    }

}
