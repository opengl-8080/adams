package adams.infrastructure.datasource;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@ToString
@Setter
@NoArgsConstructor
public class DataSourceDefinition {
    private String id;
    private String name;
    private String url;
    private String user;
    private String pass;

    public DataSourceDefinition(String name, String url, String user, String pass) {
        this(null, name, url, user, pass);
    }

    private DataSourceDefinition(String id, String name, String url, String user, String pass) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public DataSourceDefinition copy() {
        DataSourceDefinition cp = new DataSourceDefinition();
        cp.id = this.id;
        cp.name = this.name;
        cp.url = this.url;
        cp.user = this.user;
        cp.pass = this.pass;
        return cp;
    }

    public boolean isEquals(DataSourceDefinitionId id) {
        if (id == null) {
            return false;
        }
        return Objects.equals(this.id, id.asString());
    }

    public boolean hasSameName(String name) {
        return Objects.equals(this.name, name);
    }

    public void assignNewId(DataSourceDefinitionId id) {
        this.id = id.asString();
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    @XmlElement
    public String getUser() {
        return user;
    }

    @XmlElement
    public String getPass() {
        return pass;
    }
}
