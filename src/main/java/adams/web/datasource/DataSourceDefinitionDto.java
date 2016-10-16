package adams.web.datasource;

import lombok.Data;

@Data
public class DataSourceDefinitionDto {
    public String name;
    public String url;
    public String user;
    public String pass;
}
