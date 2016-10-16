package adams.domain.definition;

import adams.common.BiThrowableConsumer;
import adams.domain.search.SearchResult;
import adams.infrastructure.datasource.DataSourceDefinitionId;
import lombok.Data;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class TableSearchDefinition {
    private String name;
    private String dataSourceId;
    private List<FilterDefinition> filterDefinition = new ArrayList<>();
    private List<SortDefinition> sortDefinition = new ArrayList<>();

    public DataSourceDefinitionId dataSourceDefinitionId() {
        return DataSourceDefinitionId.of(this.dataSourceId);
    }

    public String toSql(SearchResult searchResult) {
        StrBuilder sb = new StrBuilder();

        sb.append("SELECT * FROM ").append(this.name);

        String where = this.filterDefinition.stream()
                                .map(fd -> fd.toSql(searchResult))
                                .collect(Collectors.joining(" AND "));

        if (!where.isEmpty()) {
            sb.append(" WHERE ").append(where);
        }

        String orderBy = this.sortDefinition.stream().map(SortDefinition::toSql).collect(Collectors.joining(", "));

        if (!orderBy.isEmpty()) {
            sb.append(" ORDER BY ").append(orderBy);
        }

        return sb.toString();
    }

    public void eachParameter(Map<String, String> queryParameters, SearchResult searchResult, BiThrowableConsumer<Integer, Object> consumer) {
        try {
            int count = 1;

            for (FilterDefinition fd : this.filterDefinition) {
                List<Object> values = fd.parameter(queryParameters, searchResult);

                for (Object value : values) {
                    consumer.accept(count, value);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
