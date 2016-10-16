package adams.infrastructure.definition;

import adams.common.IllegalParameterException;
import adams.domain.definition.SearchDefinition;
import adams.infrastructure.common.SequenceNumberGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SearchDefinitionRepository {
    private final File OUTPUT_BASE_DIR = new File("./config/definitions");
    private final ObjectMapper mapper = new ObjectMapper();
    private final SequenceNumberGenerator sequence = new SequenceNumberGenerator("search-definition");

    public List<SearchDefinition> findAll() {
        List<SearchDefinition> list = new ArrayList<>();
        File[] files = OUTPUT_BASE_DIR.listFiles((dir, name) -> name.endsWith(".json"));

        try {
            for (File file : files) {
                SearchDefinition sd = this.mapper.readValue(file, SearchDefinition.class);
                list.add(sd);
            }

            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SearchDefinition> find(long id) {
        File file = this.jsonFile(id);
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            SearchDefinition sd = this.mapper.readValue(file, SearchDefinition.class);
            return Optional.of(sd);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void register(SearchDefinition searchDefinition) {
        long id = this.sequence.nextval();
        searchDefinition.setId(id);

        this.save(searchDefinition, () -> log.info("register search condition (id={})", searchDefinition.getId()));
    }

    public void modify(SearchDefinition searchDefinition) {
        File file = this.jsonFile(searchDefinition.getId());

        if (!file.exists()) {
            throw new IllegalParameterException("指定した検索定義は存在しません(id=" + searchDefinition.getId() + ")");
        }

        this.save(searchDefinition, () -> log.info("modify search condition (id={})", searchDefinition.getId()));
    }

    private void save(SearchDefinition sd, Runnable onCompleted) {
        File file = this.jsonFile(sd.getId());

        try {
            this.mapper.writeValue(file, sd);
            onCompleted.run();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void remove(long id) {
        File file = this.jsonFile(id);
        if (!file.exists()) {
            return;
        }

        file.delete();
        log.info("remove search condition (id={})", id);
    }

    private File jsonFile(long id) {
        return new File(OUTPUT_BASE_DIR, id + ".json");
    }

    @PostConstruct
    private void initialize() {
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);

        if (!OUTPUT_BASE_DIR.exists()) {
            OUTPUT_BASE_DIR.mkdirs();
        }
    }
}
