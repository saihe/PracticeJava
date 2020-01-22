package ksaito.superCsvAnnotation;

import com.github.mygreen.supercsv.annotation.*;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.annotation.conversion.CsvNullConvert;
import com.github.mygreen.supercsv.builder.FixedSizeHeaderMapper;
import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanReader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@CsvBean
@CsvBean(
    header = true,
    validateHeader = true
)
@CsvPartial(
    columnSize = 3,
    headers = {
        @CsvPartial.Header(number = 4, label = "suffix")
    }
)
public class Csv {
  @CsvColumn(number = 1, label = "id")
  private String id;
  @CsvColumn(number = 2, label = "name")
  private String name;
  @CsvColumn(number = 3, label = "mail_address")
  private String mailAddress;
//  private String suffix;
}
