package adams.infrastructure.datasource;

import adams.common.IllegalParameterException;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ToString
@XmlRootElement(name="data-sources")
public class DataSourceDefinitions {
    @XmlElement(name="data-source")
    List<DataSourceDefinition> dataSourceDefinitionList = new ArrayList<>();

    public Optional<DataSourceDefinition> find(DataSourceDefinitionId id) {
        return this.dataSourceDefinitionList.stream().filter(it -> it.isEquals(id)).findFirst();
    }

    public Optional<DataSourceDefinition> find(String name) {
        return this.dataSourceDefinitionList.stream().filter(it -> it.hasSameName(name)).findFirst();
    }

    public boolean contains(String name) {
        return this.find(name).isPresent();
    }

    public void add(DataSourceDefinition ds) {
        if (this.contains(ds.getName())) {
            throw new IllegalParameterException("同じ名前のデータソースが既に存在します");
        }

        this.dataSourceDefinitionList.add(ds);
    }

    public void remove(DataSourceDefinitionId id) {
        this.dataSourceDefinitionList.removeIf(it -> it.isEquals(id));
    }

    public DataSourceDefinitions copy() {
        DataSourceDefinitions cp = new DataSourceDefinitions();
        List<DataSourceDefinition> copyList = this.dataSourceDefinitionList.stream().map(DataSourceDefinition::copy).collect(Collectors.toList());
        cp.dataSourceDefinitionList = copyList;
        return cp;
    }

    public boolean contains(DataSourceDefinitionId id) {
        return this.dataSourceDefinitionList.stream().anyMatch(it -> it.isEquals(id));
    }
}
