package titan.ccp.stats.streamprocessing;

import com.datastax.driver.core.Session;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.TimeWindows;
import titan.ccp.common.kafka.streams.PropertiesBuilder;
import titan.ccp.model.records.DayOfWeekActivePowerRecord;
import titan.ccp.model.records.HourOfDayActivePowerRecord;
import titan.ccp.model.records.HourOfWeekActivePowerRecord;

/**
 * Builder for the statistics {@link KafkaStreams} configuration.
 */
public class KafkaStreamsBuilder { // NOPMD Builder class

  private String applicationName = "titan-ccp-stats"; // NOPMD
  private String applicationVersion = "dev"; // NOPMD
  private String bootstrapServers; // NOPMD
  private String activePowerTopic; // NOPMD
  private String aggrActivePowerTopic; // NOPMD
  private String dayOfWeekTopic; // NOPMD
  private String hourOfDayTopic; // NOPMD
  private String hourOfWeekTopic; // NOPMD
  private String schemaRegistryUrl; // NOPMD
  private Session cassandraSession; // NOPMD
  private int numThreads = -1; // NOPMD
  private int commitIntervalMs = -1; // NOPMD
  private int cacheMaxBytesBuff = -1; // NOPMD

  /**
   * Sets the application name of the kafka streams application. Used for the ID.
   *
   * @param applicationName Name of the application
   * @return
   */
  public KafkaStreamsBuilder applicationName(final String applicationName) {
    // check if it is the not replaced name for the application (when executed with IDE)
    if ("@application.name@".equals(applicationName)) {
      return this;
    }
    this.applicationName = applicationName;
    return this;
  }

  /**
   * Sets the application version of the kafka streams application. Used for the ID.
   *
   * @param applicationVersion Version of the application
   * @return
   */
  public KafkaStreamsBuilder applicationVersion(final String applicationVersion) {
    // check if it is the not replaced version for the application (when executed with IDE)
    if ("@application.version@".equals(applicationVersion)) {
      return this;
    }
    this.applicationVersion = applicationVersion;
    return this;
  }

  public KafkaStreamsBuilder bootstrapServers(final String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
    return this;
  }

  public KafkaStreamsBuilder cassandraSession(final Session cassandraSession) {
    this.cassandraSession = cassandraSession;
    return this;
  }

  public KafkaStreamsBuilder activePowerTopic(final String activePowerTopic) {
    this.activePowerTopic = activePowerTopic;
    return this;
  }

  public KafkaStreamsBuilder aggrActivePowerTopic(final String aggrActivePowerTopic) {
    this.aggrActivePowerTopic = aggrActivePowerTopic;
    return this;
  }

  public KafkaStreamsBuilder dayOfWeekTopic(final String dayOfWeekTopic) {
    this.dayOfWeekTopic = dayOfWeekTopic;
    return this;
  }

  public KafkaStreamsBuilder hourOfDayTopic(final String hourOfDayTopic) {
    this.hourOfDayTopic = hourOfDayTopic;
    return this;
  }

  public KafkaStreamsBuilder hourOfWeekTopic(final String hourOfWeekTopic) {
    this.hourOfWeekTopic = hourOfWeekTopic;
    return this;
  }

  public KafkaStreamsBuilder schemaRegistry(final String url) {
    this.schemaRegistryUrl = url;
    return this;
  }

  /**
   * Sets the Kafka Streams property for the number of threads (num.stream.threads). Can be minus
   * one for using the default.
   */
  public KafkaStreamsBuilder numThreads(final int numThreads) {
    if (numThreads < -1 || numThreads == 0) {
      throw new IllegalArgumentException("Number of threads must be greater 0 or -1.");
    }
    this.numThreads = numThreads;
    return this;
  }

  /**
   * Sets the Kafka Streams property for the frequency with which to save the position (offsets in
   * source topics) of tasks (commit.interval.ms). Must be zero for processing all record, for
   * example, when processing bulks of records. Can be minus one for using the default.
   */
  public KafkaStreamsBuilder commitIntervalMs(final int commitIntervalMs) {
    if (commitIntervalMs < -1) {
      throw new IllegalArgumentException("Commit interval must be greater or equal -1.");
    }
    this.commitIntervalMs = commitIntervalMs;
    return this;
  }

  /**
   * Sets the Kafka Streams property for maximum number of memory bytes to be used for record caches
   * across all threads (cache.max.bytes.buffering). Must be zero for processing all record, for
   * example, when processing bulks of records. Can be minus one for using the default.
   */
  public KafkaStreamsBuilder cacheMaxBytesBuffering(final int cacheMaxBytesBuffering) {
    if (cacheMaxBytesBuffering < -1) {
      throw new IllegalArgumentException("Cache max bytes buffering must be greater or equal -1.");
    }
    this.cacheMaxBytesBuff = cacheMaxBytesBuffering;
    return this;
  }

  /**
   * Builds the {@link KafkaStreams} instance.
   */
  public KafkaStreams build() {
    return new KafkaStreams(this.buildTopology(), this.buildProperties());
  }

  private Topology buildTopology() {
    Objects.requireNonNull(this.activePowerTopic,
        "Kafka topic for active power records has not been set.");
    Objects.requireNonNull(this.aggrActivePowerTopic,
        "Kafka topic for aggregated active power records has not been set.");
    Objects.requireNonNull(this.dayOfWeekTopic,
        "Kafka topic for day of week active power records has not been set.");
    Objects.requireNonNull(this.hourOfDayTopic,
        "Kafka topic for hour of day active power records has not been set.");
    Objects.requireNonNull(this.hourOfWeekTopic,
        "Kafka topic for hour of week active power records has not been set.");
    Objects.requireNonNull(this.cassandraSession, "Cassandra session has not been set.");
    // TODO log parameters
    final TopologyBuilder topologyBuilder = new TopologyBuilder(
        new Serdes(this.schemaRegistryUrl),
        this.cassandraSession,
        this.activePowerTopic,
        this.aggrActivePowerTopic);
    topologyBuilder.addStat(
        new DayOfWeekKeyFactory(),
        DayOfWeekKeySerde.create(),
        new DayOfWeekRecordFactory(),
        new RecordDatabaseAdapter<>(DayOfWeekActivePowerRecord.class, "dayOfWeek"), // NOCS
        TimeWindows.of(Duration.ofDays(365)).advanceBy(Duration.ofDays(30)), // NOCS
        this.dayOfWeekTopic);
    topologyBuilder.addStat(
        new HourOfDayKeyFactory(),
        HourOfDayKeySerde.create(),
        new HourOfDayRecordFactory(),
        new RecordDatabaseAdapter<>(HourOfDayActivePowerRecord.class, "hourOfDay"), // NOCS
        TimeWindows.of(Duration.ofDays(30)).advanceBy(Duration.ofDays(1)), // NOCS
        this.hourOfDayTopic);
    topologyBuilder.addStat(
        new HourOfWeekKeyFactory(),
        HourOfWeekKeySerde.create(),
        new HourOfWeekRecordFactory(),
        new RecordDatabaseAdapter<>(
            HourOfWeekActivePowerRecord.class,
            List.of("dayOfWeek", "hourOfDay")), // NOCS
        TimeWindows.of(Duration.ofDays(365)).advanceBy(Duration.ofDays(30)), // NOCS
        this.hourOfWeekTopic);
    return topologyBuilder.build();
  }

  private Properties buildProperties() {
    return PropertiesBuilder
        .bootstrapServers(this.bootstrapServers)
        .applicationId(this.applicationName + '-' + this.applicationVersion)
        .set(StreamsConfig.NUM_STREAM_THREADS_CONFIG, this.numThreads, p -> p > 0)
        .set(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, this.commitIntervalMs, p -> p >= 0)
        .set(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, this.cacheMaxBytesBuff, p -> p >= 0)
        .build();
  }

}
