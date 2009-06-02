package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.BeringException;
import edu.northwestern.bioinformatics.bering.DataSourceProvider;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

/**
  * @goal cleanup
 */
public class CleanupMojo extends MigrateMojo {
	/**
	 * @parameter
	 */
	private String cleanupVersionTable;

	/**
	 * @parameter
	 */
	private String cleanupMigrationsDir;

	public String getCleanupVersionTable() {
		return cleanupVersionTable;
	}

	public void setCleanupVersionTable(String cleanupVersionTable) {
		this.cleanupVersionTable = cleanupVersionTable;
	}

	public String getCleanupMigrationsDir() {
		return cleanupMigrationsDir;
	}

	public void setCleanupMigrationsDir(String cleanupMigrationsDir) {
		this.cleanupMigrationsDir = cleanupMigrationsDir;
	}

	@Override
	protected void executeInternal() throws MojoExecutionException, MojoFailureException {
		getLog().info("Running cleanup migrations in " + getCleanupMigrationsDir() + " on " + getCleanupVersionTable());
		runMigrations(cleanupMigrationsDir, getTargetVersion(), cleanupVersionTable);
	}
}