package art.stalaverse.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Issue #4: the console report must show cents, line up, and reconcile row by row. */
class ReportFormattingTest {

  private static final String COLUMN_SEPARATOR = " | ";

  private static List<String> runApp() {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      System.setOut(capture);
      App.main(new String[0]);
    } finally {
      System.setOut(original);
    }
    List<String> lines = new ArrayList<>();
    for (String raw : buffer.toString(StandardCharsets.UTF_8).split("\n")) {
      String line = raw.endsWith("\r") ? raw.substring(0, raw.length() - 1) : raw;
      if (!line.isBlank()) {
        lines.add(line);
      }
    }
    return lines;
  }

  private static boolean isRule(String line) {
    return !line.isEmpty() && line.chars().allMatch(character -> character == '-');
  }

  private static boolean isTableRow(String line) {
    return line.contains(COLUMN_SEPARATOR);
  }

  private static boolean isDataRow(String line) {
    return isTableRow(line) && Character.isDigit(line.charAt(0));
  }

  @Test
  void everyTableRowMatchesTheRuleWidth() {
    List<String> lines = runApp();
    int ruleWidth = -1;
    for (String line : lines) {
      if (isRule(line) && ruleWidth < 0) {
        ruleWidth = line.length();
      }
    }
    assertTrue(ruleWidth > 0, "no rule was printed");

    for (String line : lines) {
      if (isRule(line)) {
        assertEquals(ruleWidth, line.length(), "inconsistent rule width");
      } else if (isTableRow(line)) {
        assertEquals(ruleWidth, line.length(), "row does not line up with the rule: [" + line + "]");
      }
    }
  }

  @Test
  void amountsArePrintedWithCents() {
    List<String> lines = runApp();
    boolean sawDataRow = false;
    for (String line : lines) {
      if (!isDataRow(line)) {
        continue;
      }
      sawDataRow = true;
      String[] cells = line.split(Pattern.quote(COLUMN_SEPARATOR));
      assertEquals(5, cells.length, "unexpected column count in [" + line + "]");
      for (int i = 1; i < cells.length; i++) {
        assertHasTwoDecimals(cells[i], line);
      }
    }
    assertTrue(sawDataRow, "no data rows were printed");
  }

  @Test
  void printedRowsReconcileAtThePrintedPrecision() {
    List<String> lines = runApp();
    boolean sawDataRow = false;
    for (String line : lines) {
      if (!isDataRow(line)) {
        continue;
      }
      sawDataRow = true;
      String[] cells = line.split(Pattern.quote(COLUMN_SEPARATOR));
      double start = parse(cells[1]);
      double contributions = parse(cells[2]);
      double interest = parse(cells[3]);
      double end = parse(cells[4]);
      // Rounding three addends to cents can move the sum by up to 1.5 cents.
      assertEquals(start + contributions + interest, end, 0.02,
          "row does not reconcile at printed precision: [" + line + "]");
    }
    assertTrue(sawDataRow, "no data rows were printed");
  }

  @Test
  void everyColumnIsRightAligned() {
    List<String> lines = runApp();
    for (String line : lines) {
      if (!isTableRow(line)) {
        continue;
      }
      String[] cells = line.split(Pattern.quote(COLUMN_SEPARATOR));
      for (int i = 1; i < cells.length; i++) {
        String cell = cells[i];
        if (i == cells.length - 1) {
          // The last cell is not followed by a separator, so trailing spaces are a real misalignment.
          assertFalse(cell.endsWith(" "), "last column is not right aligned: [" + line + "]");
        } else {
          assertFalse(cell.endsWith("  "), "column " + i + " is not right aligned: [" + line + "]");
        }
      }
    }
  }

  private static void assertHasTwoDecimals(String cell, String line) {
    String trimmed = cell.trim();
    int dot = trimmed.indexOf('.');
    assertTrue(dot > 0, "cell is not a decimal amount: [" + trimmed + "] in [" + line + "]");
    assertEquals(2, trimmed.length() - dot - 1,
        "cell is not printed with two decimals: [" + trimmed + "] in [" + line + "]");
  }

  private static double parse(String cell) {
    return Double.parseDouble(cell.trim().replace(",", ""));
  }
}
