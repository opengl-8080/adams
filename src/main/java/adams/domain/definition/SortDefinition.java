package adams.domain.definition;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SortDefinition {
    private String columnName;
    private String direction;

    public String toSql() {
        return this.columnName + " " + StringUtils.defaultString(this.direction, "ASC");
    }
}
