package adams.infrastructure.datasource;

import adams.common.IllegalParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXB;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

@Slf4j
@Repository
public class DataSourceRepository {
    private static final File CONFIG = new File("./config/data-source.xml");

    private DataSourceDefinitions dataSourceDefinitions;

    public DataSourceDefinitions findAll() {
        return this.dataSourceDefinitions.copy();
    }

    synchronized public void remove(DataSourceDefinitionId id) {
        this.dataSourceDefinitions.remove(id);
        this.saveFile();
        log.info("remove datasource (id={})", id);
    }

    synchronized public void modify(DataSourceDefinitionId id, String url, String user, String pass) {
        DataSourceDefinition def =  this.dataSourceDefinitions.find(id)
                                            .orElseThrow(() -> new IllegalParameterException("指定したデータソースは存在しません(id=" + id + ")"));
        def.setUrl(url);
        def.setUser(user);
        def.setPass(pass);

        this.saveFile();
        log.info("modify datasource (id={})", id);
    }

    synchronized public void add(DataSourceDefinition def) {
        this.dataSourceDefinitions.add(def);
        def.assignNewId(DataSourceDefinitionId.newId());
        this.saveFile();
        log.info("add datasource (id={}, name={})", def.getId(), def.getName());
    }

    private void saveFile() {
        JAXB.marshal(this.dataSourceDefinitions, CONFIG);
    }

    @PostConstruct
    private void initialize() {
        if (!CONFIG.getParentFile().exists()) {
            CONFIG.getParentFile().mkdirs();
        }

        if (!CONFIG.exists()) {
            try {
                CONFIG.createNewFile();
                JAXB.marshal(new DataSourceDefinitions(), CONFIG);
                log.info("data-source.xml is created.");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        this.dataSourceDefinitions = JAXB.unmarshal(CONFIG, DataSourceDefinitions.class);
        log.info("data-source.xml is loaded.");
    }

    public boolean exists(DataSourceDefinitionId id) {
        return this.dataSourceDefinitions.contains(id);
    }

    public Optional<DataSourceDefinition> find(DataSourceDefinitionId id) {
        return this.dataSourceDefinitions.find(id);
    }
}
