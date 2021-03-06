package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.runtime.Version;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Rhett Sutphin
 */
public class StubAdapter implements Adapter {
    private String databaseName;

    public void beginTransaction() {
        throw new UnsupportedOperationException("beginTransaction not implemented");
    }

    public void commit() {
        throw new UnsupportedOperationException("commit not implemented");
    }

    public void rollback() {
        throw new UnsupportedOperationException("rollback not implemented");
    }

    public void close() {
        throw new UnsupportedOperationException("close not implemented");
    }

    public void createTable(TableDefinition def) {
        throw new UnsupportedOperationException("createTable not implemented");
    }

    public void renameTable(String tableName, String newName, boolean hasPrimaryKey) {
        throw new UnsupportedOperationException("renameTable not implemented");
    }

    public void dropTable(String name, boolean hasPrimaryKey) {
        throw new UnsupportedOperationException("dropTable not implemented");
    }

    public void addColumn(String tableName, Column column) {
        throw new UnsupportedOperationException("addColumn not implemented");
    }

    public void dropColumn(String tableName, String columnName) {
        throw new UnsupportedOperationException("dropColumn not implemented");
    }

    public void renameColumn(String tableName, String columnName, String newColumnName) {
        throw new UnsupportedOperationException("renameColumn not implemented");
    }

    public void setDefaultValue(String tableName, String columnName, String newDefault) {
        throw new UnsupportedOperationException("setDefaultValue not implemented");
    }

    public void setNullable(String tableName, String columnName, boolean nullable) {
        throw new UnsupportedOperationException("setNullable not implemented");
    }

    public void execute(String sql) {
        throw new UnsupportedOperationException("execute not implemented");
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Version loadVersions() {
        throw new UnsupportedOperationException("loadVersions not implemented");
    }

    public void updateVersion(Integer release, Integer migration) {
        throw new UnsupportedOperationException("updateVersion not implemented");
    }

	public JdbcTemplate getJdbcTemplate() {
		throw new UnsupportedOperationException("getJdbcTemplate not implemented");
	}

	public void insert(String tableName, List<String> columnNames, List<Object> values, boolean hasPrimaryKey) {
        throw new UnsupportedOperationException("insert not implemented");
    }
}
