package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.runtime.Version;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public interface Adapter {
    void beginTransaction();

    void commit();

    void rollback();

    void close();

    void createTable(TableDefinition def);

    void renameTable(String tableName, String newName, boolean hasPrimaryKey);

    void dropTable(String name, boolean hasPrimaryKey);

    void addColumn(String tableName, Column column);

    void dropColumn(String tableName, String columnName);

    void renameColumn(String tableName, String columnName, String newColumnName);

    void setDefaultValue(String tableName, String columnName, String newDefault);

    void setNullable(String tableName, String columnName, boolean nullable);

    void execute(String sql);

    void insert(String tableName, List<String> columnNames, List<Object> values, boolean automaticPrimaryKey);

    String getDatabaseName();

    Version loadVersions();

    /**
     * Update note that the database is now at the <code>migration</code> state
     * for a particular release.  If <code>migration</code> is zero, the database has had
     * none of the migrations for that release applied.
     * @param release
     * @param migration
     */
    void updateVersion(Integer release, Integer migration);
    
    JdbcTemplate getJdbcTemplate();
}
