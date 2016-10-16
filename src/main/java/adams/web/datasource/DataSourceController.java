package adams.web.datasource;

import adams.infrastructure.datasource.DataSourceDefinition;
import adams.infrastructure.datasource.DataSourceDefinitionId;
import adams.infrastructure.datasource.DataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/data-sources")
public class DataSourceController {

    @Autowired
    private DataSourceRepository repository;

    @PostMapping
    public void register(@RequestBody DataSourceDefinitionDto dto) throws IOException {
        DataSourceDefinition dataSourceDefinition = new DataSourceDefinition(dto.name, dto.url, dto.user, dto.pass);
        this.repository.add(dataSourceDefinition);
    }

    @PutMapping("/{id}")
    public void modify(@PathVariable String id, @RequestBody DataSourceDefinitionDto dto) {
        this.repository.modify(DataSourceDefinitionId.of(id), dto.url, dto.user, dto.pass);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable String id) {
        this.repository.remove(DataSourceDefinitionId.of(id));
    }
}
