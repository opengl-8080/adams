package adams.infrastructure.datasource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode
public class DataSourceDefinitionId {
    private final String id;

    public static DataSourceDefinitionId of(String value) {
        return new DataSourceDefinitionId(value);
    }

    public static DataSourceDefinitionId newId() {
        return new DataSourceDefinitionId(UUID.randomUUID().toString());
    }

    private DataSourceDefinitionId(String id) {
        this.id = id;
    }

    public String asString() {
        return this.id;
    }
}
