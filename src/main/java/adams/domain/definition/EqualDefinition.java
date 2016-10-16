package adams.domain.definition;

import adams.domain.search.SearchResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class EqualDefinition {
    private String parameter;
    private String prefix;
    private String table;
    private String property;
    private SelectDefinition select;

    public String toSql(SearchResult searchResult) {
        if (StringUtils.isNotEmpty(this.parameter)) {
            return "IN (?)";
        }

        int count = searchResult.count(this.table, this.property, this.select);
        if (count == 0) {
            throw new RuntimeException("条件の取得元となる検索結果が存在しません(table=" + this.table + ", property=" + this.property + ")");
        }

        StrBuilder sb = new StrBuilder("IN (");

        for (int i=0; i<count; i++) {
            if (i != 0) {
                sb.append(", ");
            }

            sb.append("?");
        }

        return sb.append(")").toString();
    }

    public List<Object> parameter(Map<String, String> queryParameters, SearchResult searchResult) {
        if (StringUtils.isNotEmpty(this.parameter)) {
            return Arrays.asList(queryParameters.get(this.parameter));
        }

        List<Object> parameters = searchResult.get(this.table, this.property, this.select);

        if (this.prefix != null) {
            parameters = parameters.stream()
                            .map(it -> this.prefix + it)
                            .collect(Collectors.toList());
        }

        return parameters;
    }
}
