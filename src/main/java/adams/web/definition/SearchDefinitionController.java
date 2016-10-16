package adams.web.definition;

import adams.domain.definition.SearchDefinition;
import adams.infrastructure.definition.SearchDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search-definitions")
public class SearchDefinitionController {

    @Autowired
    private SearchDefinitionRepository repository;

    @GetMapping("/index")
    public List<SearchDefinitionIndexDto> findAllForIndex() {
        return this.repository.findAll().stream().map(it -> {
                    SearchDefinitionIndexDto dto = new SearchDefinitionIndexDto();
                    dto.id = it.getId();
                    dto.name = it.getName();
                    return dto;
                }).collect(Collectors.toList());
    }

    @PostMapping
    public void register(@RequestBody SearchDefinition searchDefinition) {
        this.repository.register(searchDefinition);
    }

    @PutMapping("/{id}")
    public void modify(@PathVariable long id, @RequestBody SearchDefinition searchDefinition) {
        searchDefinition.setId(id);
        this.repository.modify(searchDefinition);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        this.repository.remove(id);
    }
}
