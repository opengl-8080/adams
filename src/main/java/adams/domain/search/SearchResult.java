package adams.domain.search;

import adams.domain.definition.SelectDefinition;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SearchResult {
    private LocalDateTime lastAccessedDateTime = LocalDateTime.now();
    private Map<String, List<Map<String, Object>>> tableResultMap = new ConcurrentHashMap<>();

    public void put(String tableName, List<Map<String, Object>> tableSearchResult) {
        this.updateAccessDateTime();
        this.tableResultMap.put(tableName, tableSearchResult);
    }

    public List<Object> get(String table, String property, SelectDefinition select) {
        this.updateAccessDateTime();

        if (!this.tableResultMap.containsKey(table)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> tableSearchResult = this.tableResultMap.get(table);

        return tableSearchResult.stream()
                .filter(record -> {
                    if (select == null) {
                        return true;
                    }

                    Object value = record.get(select.getColumnName());
                    return Objects.equals(value, select.getValue());
                })
                .map(record -> record.get(property))
                .collect(Collectors.toList());
    }

    public int count(String table, String property, SelectDefinition select) {
        return this.get(table, property, select).size();
    }

    private void updateAccessDateTime() {
        this.lastAccessedDateTime = LocalDateTime.now();
    }

    public boolean isBefore(LocalDateTime other) {
        return this.lastAccessedDateTime.isBefore(other);
    }
}
