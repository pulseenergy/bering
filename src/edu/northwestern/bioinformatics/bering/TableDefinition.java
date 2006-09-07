package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Moses Hohman
 */
public class TableDefinition {
    private String name;
    private List<Column> columns;

    public TableDefinition(String name) {
        this(name, true);
    }

    public TableDefinition(String name, boolean includeId) {
        this.name = name;
        columns = new LinkedList<Column>();
        if (includeId) columns.add(createPrimaryKeyColumn());
    }

    private Column createPrimaryKeyColumn() {
        Column col = Migration.createColumn(null, "id", "integer");
        col.setPrimaryKey(true);
        col.setAutoIncrement(true);
        return col;
    }

    public void addColumn(String columnName, String type) {
        addColumn(null, columnName, type);
    }

    public void addColumn(Map<String, Object> parameters, String columnName, String type) {
        columns.add(Migration.createColumn(parameters, columnName, type));
    }

    public void addVersionColumn() {
        Column v = Migration.createColumn(null, "version", "integer");
        v.setRequired(true);
        v.setDefaultValue("0");
        columns.add(v);
    }

    public Table toTable() {
        Table t =  new Table();
        t.setName(getName());
        for (Column column : columns) {
            t.addColumn(column);
        }
        return t;
    }

    public String getName() {
        return name;
    }
}
