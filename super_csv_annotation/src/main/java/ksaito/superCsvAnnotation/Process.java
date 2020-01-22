package ksaito.superCsvAnnotation;

import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Process {
  private static final String DIR = "/Users/saito/Documents/sandbox/workspace/PracticeJava/super_csv_annotation/files";
  private static final String[] HEADERS = new String[] {
      "id",
      "name",
      "mail_address",
      "suffix"
  };

  public static Process getInstance() {
    return new Process();
  }

  public void run() {
    map();
  }

  /**
   * .
   */
  public void map() {
    try (
        CsvAnnotationBeanReader<Csv> csvReader = new CsvAnnotationBeanReader<>(
            Csv.class,
            Files.newBufferedReader(
                Paths.get(
                    DIR,
                    "input.csv"
                ),
                StandardCharsets.UTF_8
            ),
            CsvPreference.STANDARD_PREFERENCE
        );
        ICsvMapWriter csvWriter = new CsvMapWriter(
            Files.newBufferedWriter(
                Paths.get(
                    DIR,
                    "output.csv"
                ),
                StandardCharsets.UTF_8
            ),
            new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new AlwaysQuoteMode()).build()
        );
    ) {

      // read
      List<Csv> inputList = csvReader.readAll();

      // write
      csvWriter.writeHeader(HEADERS);
      inputList.stream().map(csv -> {
            Map<String, String> outputMap = new LinkedHashMap<>();
            outputMap.put(HEADERS[0], csv.getId());
            outputMap.put(HEADERS[1], csv.getName());
            outputMap.put(HEADERS[2], csv.getMailAddress());
            outputMap.put(HEADERS[3], "suffix追加"); // XXX: カラム追加
            return outputMap;
          }
      ).forEach(map -> {
        try {
          csvWriter.write(map, HEADERS);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * .
   */
  public void bean() {
    try (
        CsvAnnotationBeanReader<Csv> csvReader = new CsvAnnotationBeanReader<>(
            Csv.class,
            Files.newBufferedReader(
                Paths.get(
                    DIR,
                    "input.csv"
                ),
                StandardCharsets.UTF_8
            ),
            CsvPreference.STANDARD_PREFERENCE
        );
        CsvAnnotationBeanWriter<Csv> csvWriter = new CsvAnnotationBeanWriter<>(
            Csv.class,
            Files.newBufferedWriter(
                Paths.get(
                    DIR,
                    "output.csv"
                ),
                StandardCharsets.UTF_8
            ),
            CsvPreference.STANDARD_PREFERENCE
        );
    ) {

      // read
      List<Csv> inputList = csvReader.readAll();

      // write
      List<Csv> outputList = inputList.stream().map(csv ->
          Csv.builder()
              .id(csv.getId())
              .name(csv.getName())
              .mailAddress(csv.getMailAddress())
              .build()
      ).collect(Collectors.toList());

      csvWriter.writeAll(outputList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
