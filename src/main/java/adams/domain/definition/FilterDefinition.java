package adams.domain.definition;

import adams.domain.search.SearchResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FilterDefinition {
    private String columnName;
    private EqualDefinition equal;
    private BetweenDefinition between;

    public String toSql(SearchResult searchResult) {
        if (this.equal != null) {
            return this.columnName + " " + this.equal.toSql(searchResult);
        }

        return this.columnName + " " + this.between.toSql();
    }

    public List<Object> parameter(Map<String, String> queryParameters, SearchResult searchResult) {
        if (this.equal != null) {
            return this.equal.parameter(queryParameters, searchResult);
        }

        return this.between.parameter();
    }
}
