package adams.domain.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SearchResultRepository {
    private Map<String, SearchResult> searchResultMap = new ConcurrentHashMap<>();



}
