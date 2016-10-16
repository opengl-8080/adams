package adams.domain.definition;

import adams.common.IllegalParameterException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchDefinition {
    private long id;
    private String name;
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private List<TableSearchDefinition> tableSearchDefinition = new ArrayList<>();

    public TableSearchDefinition getTableSearchDefinition(int tableIndex) {
        if (this.tableSearchDefinition.size() <= tableIndex) {
            throw new IllegalParameterException("テーブル検索定義が存在しません(tableIndex=" + tableIndex + ")");
        }

        return this.tableSearchDefinition.get(tableIndex);
    }

    public boolean hasTable(int tableIndex) {
        return tableIndex < this.tableSearchDefinition.size();
    }
}
