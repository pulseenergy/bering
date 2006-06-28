package edu.northwestern.bioinformatics.bering;

import groovy.lang.Closure;

import java.util.Map;
import java.util.HashMap;
import java.beans.PropertyEditor;
import java.sql.Types;

import org.apache.ddlutils.model.Column;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/**
 * @author Moses Hohman
 */
public abstract class Migration {
    private static final String NULLABLE_KEY  = "null";
    private static final String LIMIT_KEY     = "limit";
    private static final String PRECISION_KEY = "precision";

    private static final Map<String, Integer> NAMES_TO_JDBC_TYPES = new HashMap<String, Integer>();
    static {
        NAMES_TO_JDBC_TYPES.put("string",    Types.VARCHAR);
        NAMES_TO_JDBC_TYPES.put("integer",   Types.INTEGER);
        NAMES_TO_JDBC_TYPES.put("float",     Types.NUMERIC);
        NAMES_TO_JDBC_TYPES.put("boolean",   Types.BOOLEAN);
        NAMES_TO_JDBC_TYPES.put("date",      Types.DATE);
        NAMES_TO_JDBC_TYPES.put("time",      Types.TIME);
        NAMES_TO_JDBC_TYPES.put("timestamp", Types.TIMESTAMP);
    }

    private Adapter adapter;

    ////// METHODS FOR MIGRATIONS TO CALL

    protected void createTable(String name, Closure addContents) {
        TableDefinition definition = new TableDefinition(name, this);
        addContents.call(definition);
        adapter.createTable(definition);
    }

    protected void dropTable(String name) {
        adapter.dropTable(name);
    }

    protected void addColumn(String tableName, String columnName, String columnType) {
        addColumn(null, tableName, columnName, columnType);
    }

    protected void addColumn(Map<String, String> parameters, String tableName, String columnName, String columnType) {
        adapter.addColumn(tableName, createColumn(parameters, columnName, columnType));
    }

    protected void removeColumn(String tableName, String columnName) {
        adapter.removeColumn(tableName, columnName);
    }

    // TODO: maybe this should be moved somewhere else
    // visible to collaborators (e.g., TableDefinition)
    Column createColumn(Map<String, String> parameters, String columnName, String columnType) {
        Column col = new Column();
        col.setName(columnName);
        col.setTypeCode(getTypeCode(columnType));
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                PropertyEditor editor = new CustomBooleanEditor(false);
                editor.setAsText(parameters.get(NULLABLE_KEY));
                col.setRequired(!((Boolean) editor.getValue()));
            }
            if (parameters.containsKey(LIMIT_KEY)) {
                col.setSize(parameters.get(LIMIT_KEY));
            }
            if (parameters.containsKey(PRECISION_KEY)) {
                col.setScale(new Integer(parameters.get(PRECISION_KEY)));
            }
        }
        return col;
    }

    private int getTypeCode(String typeName) {
        Integer code = NAMES_TO_JDBC_TYPES.get(typeName);
        if (code == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return code;
    }

    public final void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }
}
