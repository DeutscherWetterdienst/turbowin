package turbowin;

import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** Installs input filters for fields that accept numeric meteorological values. */
final class NumericInputWorkflow {

  private NumericInputWorkflow() {}

  static void decimal(JTextField field) {
    install(field, true);
  }

  static void integer(JTextField field) {
    install(field, false);
  }

  static void decimalOr(JTextField field, String... words) {
    install(field, true, words);
  }

  static void integerOr(JTextField field, String... words) {
    install(field, false, words);
  }

  private static void install(JTextField field, boolean decimal, String... words) {
    ((AbstractDocument) field.getDocument()).setDocumentFilter(new Filter(decimal, words));
  }

  private static final class Filter extends DocumentFilter {
    private final boolean decimal;
    private final String[] words;

    private Filter(boolean decimal, String[] words) {
      this.decimal = decimal;
      this.words = words;
    }

    @Override
    public void insertString(FilterBypass bypass, int offset, String text, AttributeSet attrs)
        throws BadLocationException {
      replace(bypass, offset, 0, text, attrs);
    }

    @Override
    public void replace(
        FilterBypass bypass, int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
      String current = bypass.getDocument().getText(0, bypass.getDocument().getLength());
      String replacement = text == null ? "" : text.replace(',', '.');
      String candidate =
          current.substring(0, offset)
              + replacement
              + current.substring(offset + length);
      if (valid(candidate)) {
        bypass.replace(offset, length, replacement, attrs);
      }
    }

    private boolean valid(String value) {
      if (value.isEmpty() || value.equals("-")) {
        return true;
      }
      String normalized = value.toLowerCase(Locale.ROOT);
      for (String word : words) {
        String allowed = word.toLowerCase(Locale.ROOT);
        if (allowed.startsWith(normalized)) {
          return true;
        }
      }
      String pattern = decimal ? "-?\\d*(?:\\.\\d*)?" : "-?\\d*";
      return normalized.matches(pattern);
    }
  }
}
