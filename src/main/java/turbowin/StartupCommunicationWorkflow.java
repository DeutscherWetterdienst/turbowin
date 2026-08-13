package turbowin;

/** Creates the communication components required during application startup. */
final class StartupCommunicationWorkflow {

  private StartupCommunicationWorkflow() {}

  static CommunicationComponents initialize() {
    return new CommunicationComponents(
        new main_RS232_RS422(), new RS232_mintaka(), new RS232_vaisala());
  }

  static final class CommunicationComponents {
    final main_RS232_RS422 serial;
    final RS232_mintaka mintaka;
    final RS232_vaisala vaisala;

    private CommunicationComponents(
        main_RS232_RS422 serial, RS232_mintaka mintaka, RS232_vaisala vaisala) {
      this.serial = serial;
      this.mintaka = mintaka;
      this.vaisala = vaisala;
    }
  }
}
