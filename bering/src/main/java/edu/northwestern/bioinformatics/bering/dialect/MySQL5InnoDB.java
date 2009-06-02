package edu.northwestern.bioinformatics.bering.dialect;

import java.sql.Types;
import java.util.List;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;

import edu.northwestern.bioinformatics.bering.Column;
import edu.northwestern.bioinformatics.bering.SqlUtils;
import edu.northwestern.bioinformatics.bering.TableDefinition;
import edu.northwestern.bioinformatics.bering.Migration;

public class MySQL5InnoDB extends HibernateBasedDialect {
	//TODO-JMG This primary key type should be configurable.
	private static final int PRIMARY_KEY_TYPE = Types.BIGINT;

	@Override
	protected Dialect createHibernateDialect() {
		return new MySQL5InnoDBDialect();
	}

	@Override
	public String getDialectName() {
		return "mysql";
	}

	public List<String> createTable(TableDefinition table) {
		table.setOptions("ENGINE = InnoDB");
		return super.createTable(table);
	}

	protected String createDefaultValueSql(Column column) {
		// If column type is a string, escape the default value.
		//TODO-JMG: Escape dates and times too?
		int colType = column.getTypeCode();
		if (colType == Types.CHAR || colType == Types.VARCHAR || colType == Types.LONGVARCHAR) {
			return SqlUtils.sqlLiteral(column.getDefaultValue());
		}
		return column.getDefaultValue().toString();
	}

	protected String getAutoPkColumnString() {
		Column primaryKeyColumn = new Column();
		primaryKeyColumn.setName("id");
		primaryKeyColumn.setTypeCode(PRIMARY_KEY_TYPE);
		primaryKeyColumn.setNullable(false);
		primaryKeyColumn.setPrimaryKey(true);
		String typeName = new ColumnDeclaration(primaryKeyColumn).getTypeName();
		StringBuilder sql = new StringBuilder();
		if (getHibernateDialect().supportsIdentityColumns()) {
			if (getHibernateDialect().hasDataTypeInIdentityColumn()) {
				sql.append(typeName).append(' ');
			}
			sql.append(getHibernateDialect().getIdentityColumnString(primaryKeyColumn.getTypeCode()));
		} else {
			sql.append(typeName);
		}
		return sql.toString().toUpperCase();
	}

	@Override
	public List<String> addColumn(String table, Column column) {
		if (column.getTypeCode() == Types.BOOLEAN) {
			column.setTypeCode(Types.TINYINT);
		}
		return super.addColumn(table, column);
	}
}
