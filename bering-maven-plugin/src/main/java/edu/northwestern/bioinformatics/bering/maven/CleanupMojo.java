package edu.northwestern.bioinformatics.bering.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
	protected void executeInternal() throws MojoExecutionException,
			MojoFailureException {
		getLog().info(
				"Running cleanup migrations in " + getCleanupMigrationsDir()
						+ " on " + getCleanupVersionTable());
		runMigrations(cleanupMigrationsDir, getTargetVersion(),
				cleanupVersionTable);
	}
}