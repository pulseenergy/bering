package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.dialect.DialectFactory;
import edu.northwestern.bioinformatics.bering.runtime.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;

/**
 * @author rsutphin
 */
public class DatabaseAdapter implements Adapter {
    private static final Log log = LogFactory.getLog(DatabaseAdapter.class);

    public static final String VERSION_TABLE_NAME = "bering_version";
    public static final String RELEASE_COLUMN_NAME = "release_num";
    public static final String MIGRATION_COLUMN_NAME = "migration";

    static {
        // TODO: these columns should be non-null
    }

    private Connection connection;
    private boolean defaultAutocommit;
    private JdbcTemplate jdbc;
    private SingleConnectionDataSource dataSource;
    private Dialect dialect;
	private TableDefinition versionTable;

	public DatabaseAdapter(Connection connection, Dialect dialect) {
	    this(connection, dialect, VERSION_TABLE_NAME);
    }

	public DatabaseAdapter(Connection connection, Dialect dialect, String versionTable) {
		this.connection = connection;
		this.dataSource = new SingleConnectionDataSource(connection, true);
		this.jdbc = new JdbcTemplate(dataSource);
		this.dialect = dialect == null ? guessDialect() : dialect;
		this.versionTable = createVersionTableDefinition(versionTable == null ? VERSION_TABLE_NAME : versionTable);
	}

	private TableDefinition createVersionTableDefinition(final String versionTableName) {
		TableDefinition versionTable = new TableDefinition(versionTableName);
		versionTable.addColumn(RELEASE_COLUMN_NAME, "integer");
		versionTable.addColumn(MIGRATION_COLUMN_NAME, "integer");
		versionTable.setIncludePrimaryKey(false);
		return versionTable;
	}

	private Dialect guessDialect() {
        return (Dialect) jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                return DialectFactory.guessDialect(connection.getMetaData().getDatabaseProductName());
            }
        });
    }

    public void close() {
        dataSource.destroy();
    }

    public void beginTransaction() {
        jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                defaultAutocommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                return null;
            }
        });
    }

    public void commit() {
        jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                connection.commit();
                connection.setAutoCommit(defaultAutocommit);
                return null;
            }
        });
    }

    public void rollback() {
        jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                connection.rollback();
                connection.setAutoCommit(defaultAutocommit);
                return null;
            }
        });
    }

    public void createTable(TableDefinition def) {
        execute(getDialect().createTable(def));
    }

    public void renameTable(String tableName, String newName, boolean hasPrimaryKey) {
        execute(getDialect().renameTable(tableName, newName, hasPrimaryKey));
    }

    public void dropTable(String name, boolean hasPrimaryKey) {
        execute(getDialect().dropTable(name, hasPrimaryKey));
    }

    public void addColumn(String tableName, Column column) {
        execute(getDialect().addColumn(tableName, column));
    }

    public void dropColumn(String tableName, String columnName) {
        execute(getDialect().dropColumn(tableName, columnName));
    }

    public void renameColumn(String tableName, String columnName, String newColumnName) {
        execute(getDialect().renameColumn(tableName, columnName, newColumnName));
    }

    public void setDefaultValue(String tableName, String columnName, String newDefault) {
        execute(getDialect().setDefaultValue(tableName, columnName, newDefault));
    }

    public void setNullable(String tableName, String columnName, boolean nullable) {
        execute(getDialect().setNullable(tableName, columnName, nullable));
    }

    public void execute(String sql) {
        execute(getDialect().separateStatements(sql));
    }

    public void insert(String tableName, List<String> columnNames, List<Object> values, boolean hasPrimaryKey) {
        execute(getDialect().insert(tableName, columnNames, values, hasPrimaryKey));
    }

    private void execute(final List<String> statements) {
        jdbc.execute(new StatementCallback() {
            public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
                for (String sql : statements) {
                    if (sql.trim().length() == 0) continue;
                    log.info("SQL: " + sql.replaceAll("\n", "\n     ") + ';');
                    stmt.execute(sql);
                }
                return null;
            }
        });
    }

    public String getDatabaseName() {
        return getDialect().getDialectName();
    }

    Dialect getDialect() {
        return dialect;
    }

    public Version loadVersions() {
        Savepoint savepoint = null;
        final Version version = new Version();
        try {
            beginTransaction();
            savepoint = connection.setSavepoint("versiontabledetect");
            jdbc.query(String.format(
                "SELECT %s, %s FROM %s", RELEASE_COLUMN_NAME, MIGRATION_COLUMN_NAME, versionTable.getName()),
                (Object[]) null, new ResultSetExtractor() {
                    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                        log.debug(versionTable.getName() + " table exists; reading current rows");
                        while (rs.next()) {
                            version.updateRelease(
                                rs.getInt(RELEASE_COLUMN_NAME),
                                rs.getInt(MIGRATION_COLUMN_NAME));
                        }
                        return null;
                    }
                }
            );
            return version;
        } catch (Exception e) {
            log.debug("Assuming " + versionTable.getName() + " does not exist due to exception", e);
            try {
                if (savepoint != null) connection.rollback(savepoint);
            } catch (SQLException rollbackE) {
                throw new RuntimeException(versionTable.getName() + " does not exist and an attempt to create it has failed", rollbackE);
            }
            log.info("Creating " + versionTable.getName() + " table");
            createTable(versionTable);
            commit();
            return new Version();
        } finally {
            rollback();
        }
    }

    public void updateVersion(Integer release, Integer migration) {
        execute(String.format("DELETE FROM %s WHERE %s = %d",
            versionTable.getName(), RELEASE_COLUMN_NAME, release));

        if (migration < 1) return;

        execute(String.format("INSERT INTO %s (%s, %s) VALUES (%d, %d)",
            versionTable.getName(), RELEASE_COLUMN_NAME, MIGRATION_COLUMN_NAME, release, migration));
    }

	public JdbcTemplate getJdbcTemplate() {
		return jdbc;
	}
}
