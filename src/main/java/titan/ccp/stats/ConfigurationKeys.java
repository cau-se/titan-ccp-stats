package titan.ccp.stats;

/**
 * Keys to access configuration parameters.
 */
public final class ConfigurationKeys {

  public static final String CASSANDRA_HOST = "cassandra.host";

  public static final String CASSANDRA_PORT = "cassandra.port";

  public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";

  public static final String WEBSERVER_ENABLE = "webserver.enable";

  public static final String WEBSERVER_PORT = "webserver.port";

  public static final String WEBSERVER_CORS = "webserver.cors";

  public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";

  public static final String KAFKA_OUTPUT_TOPIC = "kafka.output.topic";

  public static final String KAFKA_INPUT_TOPIC = "kafka.input.topic";

  private ConfigurationKeys() {}

}