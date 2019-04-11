package titan.ccp.stats.streamprocessing;

import java.util.List;
import java.util.Set;
import titan.ccp.common.cassandra.ExplicitPrimaryKeySelectionStrategy;
import titan.ccp.common.cassandra.PrimaryKeySelectionStrategy;

/**
 * {@link PrimaryKeySelectionStrategy} for storing statistics results. This strategy is configured
 * using {@link RecordDatabaseAdapter}s, which introduce a mapping from a record type to partition
 * keys and clustering columns. If partition keys and clustering columns are requested for
 * unconfigured record types, an {@link IllegalArgumentException} is thrown.
 */
public class CassandraKeySelector implements PrimaryKeySelectionStrategy {

  private final ExplicitPrimaryKeySelectionStrategy keySelectionStrategy;

  public CassandraKeySelector() {
    this.keySelectionStrategy = new ExplicitPrimaryKeySelectionStrategy(new AlwaysFailStrategy());
  }

  /**
   * Add a new {@link RecordDatabaseAdapter}.
   */
  public void addRecordDatabaseAdapter(final RecordDatabaseAdapter<?> adapter) {
    final String className = adapter.getClazz().getSimpleName();
    this.keySelectionStrategy.registerPartitionKeys(className, adapter.getIdentifierField());
    this.keySelectionStrategy.registerClusteringColumns(
        className,
        adapter.getPeriodStartField(),
        adapter.getPeriodEndField(),
        adapter.getTimeUnitField());
  }

  @Override
  public Set<String> selectPartitionKeys(final String tableName,
      final List<String> possibleColumns) {
    return this.keySelectionStrategy.selectPartitionKeys(tableName, possibleColumns);
  }

  @Override
  public Set<String> selectClusteringColumns(final String tableName,
      final List<String> possibleColumns) {
    return this.keySelectionStrategy.selectClusteringColumns(tableName, possibleColumns);
  }

  private static class AlwaysFailStrategy implements PrimaryKeySelectionStrategy {

    @Override
    public Set<String> selectPartitionKeys(final String tableName,
        final List<String> possibleColumns) {
      throw new IllegalArgumentException(
          "Partition keys for " + tableName + " are not registered."); // NOCS
    }

    @Override
    public Set<String> selectClusteringColumns(final String tableName,
        final List<String> possibleColumns) {
      throw new IllegalArgumentException(
          "Clustering columns for " + tableName + " are not registered."); // NOCS
    }

  }

}
