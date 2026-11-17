package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.junit.After;
import org.junit.Test;

public class CommunicationReconfigurationTest {

  @After
  public void clearConnectionState() {
    main.defaultPort = null;
    main.defaultPort_II = null;
    main.GPS_serialPort = null;
    main.serialPort = null;
    main.serialPort_II = null;
    main_RS232_RS422.GPS_defaultPort = null;
  }

  @Test
  public void cancelsCommunicationWorkerBeforeReconfiguration() throws Exception {
    CountDownLatch started = new CountDownLatch(1);
    SwingWorker<Void, Void> worker =
        new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() {
            started.countDown();
            try {
              new CountDownLatch(1).await();
            } catch (InterruptedException interrupted) {
              Thread.currentThread().interrupt();
            }
            return null;
          }
        };
    worker.execute();
    assertTrue(started.await(5, TimeUnit.SECONDS));

    Method cancelWorker =
        main_RS232_RS422.class.getDeclaredMethod("cancelWorker", SwingWorker.class);
    cancelWorker.setAccessible(true);
    cancelWorker.invoke(null, worker);

    assertTrue(worker.isCancelled());
  }

  @Test
  public void stopsCommunicationTimersAndClearsSelectedPorts() throws Exception {
    main.defaultPort = "COM-test";
    main.defaultPort_II = "COM-test-2";
    main_RS232_RS422.GPS_defaultPort = "COM-gps";

    main_RS232_RS422 communication = new main_RS232_RS422();
    Timer sensorTimer = new Timer(1000, null);
    Timer secondSensorTimer = new Timer(1000, null);
    Timer gpsTimer = new Timer(1000, null);
    sensorTimer.start();
    secondSensorTimer.start();
    gpsTimer.start();
    setField(communication, "check_new_data_timer", sensorTimer);
    setField(communication, "check_new_data_timer_II", secondSensorTimer);
    setField(communication, "check_new_GPS_data_timer", gpsTimer);

    communication.stopCommunication();

    assertFalse(sensorTimer.isRunning());
    assertFalse(secondSensorTimer.isRunning());
    assertFalse(gpsTimer.isRunning());
    assertNull(main.defaultPort);
    assertNull(main.defaultPort_II);
    assertNull(main_RS232_RS422.GPS_defaultPort);
  }

  private static void setField(Object target, String name, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }
}
