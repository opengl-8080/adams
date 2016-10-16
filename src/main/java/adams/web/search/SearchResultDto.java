package adams.web.search;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResultDto {
    public List<Map<String, Object>> data;
    public String nextUrl;
    public String removeSearchResultCacheUrl;
}
