package adams.infrastructure.datasource;

import adams.common.IllegalParameterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class ConnectionPoolRepository {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    private Map<DataSourceDefinitionId, BasicDataSource> pool = new ConcurrentHashMap<>();

    public Connection get(DataSourceDefinitionId id) {
        try {
            if (this.pool.containsKey(id)) {
                return this.pool.get(id).getConnection();
            }

            DataSourceDefinition ds = this.dataSourceRepository.find(id).orElseThrow(() -> new IllegalParameterException("指定したデータソースは存在しません(id=" + id + ")"));

            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            dataSource.setUrl(ds.getUrl());
            dataSource.setUsername(ds.getUser());
            dataSource.setPassword(ds.getPass());

            this.pool.put(id, dataSource);
            log.info("add connection pool (id={})", id);

            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("failed to connect database (id=" + id + ")", e);
        }
    }

    public void releasePool(DataSourceDefinitionId id) {
        if (!this.pool.containsKey(id)) {
            return;
        }

        BasicDataSource dataSource = this.pool.get(id);
        try {
            dataSource.close();
            this.pool.remove(id);
            log.info("release connection pool (id={})", id);
        } catch (SQLException e) {
            log.warn("failed to release connection pool (id=" + id + ")", e);
        }
    }

    @PreDestroy
    private void destroy() {
        this.pool.keySet().forEach(this::releasePool);
        log.info("released all connection pools.");
    }
}
