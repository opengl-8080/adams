package adams.domain.definition;

import adams.common.IllegalParameterException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class BetweenDefinition {
    private String from;
    private String to;

    public String toSql() {
        return "BETWEEN ? AND ?";
    }

    public List<Object> parameter() {
        return Arrays.asList(this.date(this.from), this.date(this.to));
    }

    private java.util.Date date(String value) {
        LocalDate today = LocalDate.now();

        if (StringUtils.equalsIgnoreCase("today()", value)) {
            return Date.valueOf(today);
        }

        if (StringUtils.equalsIgnoreCase("lastMonth()", value)) {
            return Date.valueOf(today.minusMonths(1));
        }

        throw new IllegalParameterException("サポートされていない関数です(value=" + value + ")");
    }
}
