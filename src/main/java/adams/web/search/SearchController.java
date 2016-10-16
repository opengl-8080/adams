package adams.web.search;

import adams.common.IllegalParameterException;
import adams.domain.definition.SearchDefinition;
import adams.domain.definition.TableSearchDefinition;
import adams.domain.search.SearchResult;
import adams.infrastructure.datasource.ConnectionPoolRepository;
import adams.infrastructure.definition.SearchDefinitionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/search")
@RestController
public class SearchController {

    @Autowired
    private SearchDefinitionRepository searchDefinitionRepository;
    @Autowired
    private ConnectionPoolRepository connectionPoolRepository;

    private Map<String, SearchResult> searchResultMap = new ConcurrentHashMap<>();

    @GetMapping
    public SearchResultDto search(
            @RequestParam long id,
            @RequestParam int tableIndex,
            @RequestParam(required=false) String requestNo,
            @RequestParam Map<String, String> queryParameters) {

        SearchDefinition sd = this.searchDefinitionRepository.find(id).orElseThrow(() -> new IllegalParameterException("指定された検索定義は存在しません"));

        TableSearchDefinition tsd = sd.getTableSearchDefinition(tableIndex);

        // リクエストNO 発行
        requestNo = StringUtils.defaultString(requestNo, UUID.randomUUID().toString());

        // 前回の検索結果の取得（なければ新規作成）
        SearchResult searchResult = this.searchResultMap.computeIfAbsent(requestNo, (key) -> new SearchResult());

        try (Connection con = this.connectionPoolRepository.get(tsd.dataSourceDefinitionId())) {
            // SQL 生成
            String sql = tsd.toSql(searchResult);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                // パラメータを設定
                tsd.eachParameter(queryParameters, searchResult, ps::setObject);

                // SQL 実行
                try (ResultSet rs = ps.executeQuery()) {
                    // 検索結果を List<Map> に変換
                    List<Map<String, Object>> tableSearchResult = this.executeSearch(rs);

                    // 検索結果を保存
                    searchResult.put(tsd.getName(), tableSearchResult);

                    // レスポンスを生成
                    SearchResultDto dto = new SearchResultDto();
                    dto.data = tableSearchResult;
                    dto.removeSearchResultCacheUrl = "/search/" + requestNo;

                    // 次のテーブルが存在する場合は、次の URL を作って DTO にセット
                    int nextTableIndex = tableIndex + 1;
                    if (sd.hasTable(nextTableIndex)) {
                        dto.nextUrl = "/search?" + this.makeNextQueryString(requestNo, queryParameters, nextTableIndex);
                    }

                    return dto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to execute sql", e);
        }
    }

    private String makeNextQueryString(String requestNo, Map<String, String> queryParameters, int nextTableIndex) {
        HashMap<String, String> nextQueryParameters = new HashMap<>(queryParameters);
        nextQueryParameters.put("tableIndex", String.valueOf(nextTableIndex));
        nextQueryParameters.put("requestNo", requestNo);

        return nextQueryParameters
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private List<Map<String, Object>> executeSearch(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<Map<String, Object>> searchResult = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> record = this.mapRecord(rs, metaData, columnCount);
            searchResult.add(record);
        }

        return searchResult;
    }

    private Map<String, Object> mapRecord(ResultSet rs, ResultSetMetaData metaData, int columnCount) throws SQLException {
        Map<String, Object> record = new HashMap<>();

        for (int i=1; i<=columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = rs.getObject(columnName);
            record.put(columnName, value);
        }

        return record;
    }

    @DeleteMapping("/{requestNo}")
    public void removeSearchResult(@PathVariable String requestNo) {
        if (this.searchResultMap.containsKey(requestNo)) {
            this.searchResultMap.remove(requestNo);
            log.info("remove search result cache (requestNo={})", requestNo);
        }
    }

    @PostConstruct
    private void startCacheClearThread() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        service.scheduleWithFixedDelay(
            () -> {
                // １０分以上アクセスがない検索結果は削除
                LocalDateTime limit = LocalDateTime.now().minusMinutes(10);

                List<String> removeKeys = this.searchResultMap.keySet().stream().filter(requestNo -> {
                                                SearchResult searchResult = this.searchResultMap.get(requestNo);
                                                return searchResult.isBefore(limit);
                                            }).collect(Collectors.toList());

                for (String key : removeKeys) {
                    this.searchResultMap.remove(key);
                }

                log.debug("remove search result caches (requestNo = {})", removeKeys);
            },
            10,
            10,
            TimeUnit.MINUTES
        );

        log.info("start search result cache clean thread.");
    }
}
