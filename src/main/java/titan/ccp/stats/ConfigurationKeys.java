package titan.ccp.stats;

/**
 * Keys to access configuration parameters.
 */
public final class ConfigurationKeys {

  public static final String APPLICATION_NAME = "application.name";

  public static final String APPLICATION_VERSION = "application.version";

  public static final String CASSANDRA_HOST = "cassandra.host";

  public static final String CASSANDRA_PORT = "cassandra.port";

  public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";

  public static final String CASSANDRA_INIT_TIMEOUT_MS = "cassandra.init.timeout.ms";

  public static final String WEBSERVER_ENABLE = "webserver.enable";

  public static final String WEBSERVER_PORT = "webserver.port";

  public static final String WEBSERVER_CORS = "webserver.cors";

  public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";

  public static final String KAFKA_TOPIC_ACTIVE_POWER = "kafka.topic.activepower";

  public static final String KAFKA_TOPIC_AGGR_ACTIVE_POWER = "kafka.topic.aggractivepower";

  public static final String KAFKA_TOPIC_DAY_OF_WEEK_POWER = "kafka.topic.dayofweekpower";

  public static final String KAFKA_TOPIC_HOUR_OF_DAY_POWER = "kafka.topic.hourofdaypower";

  public static final String KAFKA_TOPIC_HOUR_OF_WEEK_POWER = "kafka.topic.hourofweekpower";

  public static final String NUM_THREADS = "num.threads";

  public static final String COMMIT_INTERVAL_MS = "commit.interval.ms";

  public static final String CACHE_MAX_BYTES_BUFFERING = "cache.max.bytes.buffering";

  public static final String SCHEMA_REGISTRY_URL = "schema.registry.url";

  private ConfigurationKeys() {}

}
