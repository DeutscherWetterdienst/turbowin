package turbowin;

import java.util.function.Function;

/** Resolves the encrypted SMTP password while preserving the legacy null marker. */
final class ObservationEmailPasswordResolver {

  private ObservationEmailPasswordResolver() {}

  static String resolve(String password, Function<String, String> decryptor) {
    if ("null".equals(password)) {
      return "null";
    }
    return decryptor.apply(password);
  }
}
