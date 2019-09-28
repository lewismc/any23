/**
 * 
 */
package org.apache.any23.plugin.rya;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.any23.writer.TurtleWriter;
import org.apache.rya.accumulo.AccumuloRdfConfiguration;
import org.apache.rya.api.client.Install;
import org.apache.rya.api.client.Install.DuplicateInstanceNameException;
import org.apache.rya.api.client.Install.InstallConfiguration;
import org.apache.rya.api.client.RyaClientException;
import org.apache.rya.api.client.accumulo.AccumuloConnectionDetails;
import org.apache.rya.api.client.accumulo.AccumuloInstall;
import org.apache.rya.api.instance.RyaDetailsRepository.AlreadyInitializedException;
import org.apache.rya.api.instance.RyaDetailsRepository.RyaDetailsRepositoryException;
import org.apache.rya.api.persist.RyaDAOException;
import org.apache.rya.indexing.accumulo.ConfigUtils;
import org.apache.rya.indexing.external.PrecomputedJoinIndexerConfig;
import org.apache.rya.indexing.pcj.fluo.app.query.MetadataCacheSupplier;
import org.apache.rya.indexing.pcj.fluo.app.query.StatementPatternIdCacheSupplier;
import org.apache.rya.rdftriplestore.RyaSailRepository;
import org.apache.rya.rdftriplestore.inference.InferenceEngineException;
import org.apache.rya.sail.config.RyaSailFactory;
import org.apache.rya.test.accumulo.MiniAccumuloClusterInstance;
import org.apache.rya.test.accumulo.MiniAccumuloSingleton;
import org.apache.rya.test.accumulo.RyaTestInstanceRule;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lewismc
 *
 */
public class RyaConnectorTest {

  private static final Logger LOG = LoggerFactory.getLogger(RyaConnectorTest.class);

  protected static Connector accumuloConn = null;
  protected static RyaConnector ryaConnector = null;
  // Rya data store and connections.
  protected static RyaSailRepository ryaRepo = null;
  protected static RepositoryConnection ryaConn = null;

  // Mini Accumulo Cluster
  private static MiniAccumuloClusterInstance clusterInstance = MiniAccumuloSingleton.getInstance();
  private static MiniAccumuloCluster cluster;

  private static String instanceName = null;
  private static String zookeepers = null;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    // Setup and start the Mini Accumulo.
    cluster = clusterInstance.getCluster();

    // Store a connector to the Mini Accumulo.
    instanceName = cluster.getInstanceName();
    zookeepers = cluster.getZooKeepers();

    final Instance instance = new ZooKeeperInstance(instanceName, zookeepers);
    accumuloConn = instance.getConnector(clusterInstance.getUsername(), new PasswordToken(clusterInstance.getPassword()));

    // Initialize the Rya that will be used by the tests.
    ryaRepo = setupRya();
    ryaConn = ryaRepo.getConnection();
  }

  /**
   * Sets up a Rya instance.
   */
  protected static RyaSailRepository setupRya()
          throws AccumuloException, AccumuloSecurityException, RepositoryException, RyaDAOException,
          NumberFormatException, UnknownHostException, InferenceEngineException, AlreadyInitializedException,
          RyaDetailsRepositoryException, DuplicateInstanceNameException, RyaClientException, SailException {
    checkNotNull(instanceName);
    checkNotNull(zookeepers);

    // Setup Rya configuration values.
    final AccumuloRdfConfiguration conf = new AccumuloRdfConfiguration();
    conf.setTablePrefix(getRyaInstanceName());
    conf.setDisplayQueryPlan(true);
    conf.setBoolean(ConfigUtils.USE_MOCK_INSTANCE, false);
    conf.set(ConfigUtils.CLOUDBASE_USER, clusterInstance.getUsername());
    conf.set(ConfigUtils.CLOUDBASE_PASSWORD, clusterInstance.getPassword());
    conf.set(ConfigUtils.CLOUDBASE_INSTANCE, clusterInstance.getInstanceName());
    conf.set(ConfigUtils.CLOUDBASE_ZOOKEEPERS, clusterInstance.getZookeepers());
    conf.set(ConfigUtils.USE_PCJ, "true");
    conf.set(ConfigUtils.FLUO_APP_NAME, getRyaInstanceName());
    conf.set(ConfigUtils.PCJ_STORAGE_TYPE, PrecomputedJoinIndexerConfig.PrecomputedJoinStorageType.ACCUMULO.toString());
    conf.set(ConfigUtils.CLOUDBASE_AUTHS, "");

    // Install the test instance of Rya.
    final Install install = new AccumuloInstall(createConnectionDetails(), accumuloConn);

    final InstallConfiguration installConfig = InstallConfiguration.builder()
            .setEnableTableHashPrefix(true)
            .setEnableEntityCentricIndex(true)
            .setEnableFreeTextIndex(true)
            .setEnableTemporalIndex(true)
            .setEnablePcjIndex(true)
            .setEnableGeoIndex(true)
            .setFluoPcjAppName(getRyaInstanceName())
            .build();
    install.install(getRyaInstanceName(), installConfig);

    // Connect to the instance of Rya that was just installed.
    final Sail sail = RyaSailFactory.getInstance(conf);
    final RyaSailRepository ryaRepo = new RyaSailRepository(sail);

    return ryaRepo;
  }

  public static AccumuloConnectionDetails createConnectionDetails() {
    return new AccumuloConnectionDetails(
            clusterInstance.getUsername(),
            clusterInstance.getPassword().toCharArray(),
            clusterInstance.getInstanceName(),
            clusterInstance.getZookeepers());
  }

  @ClassRule
  public static RyaTestInstanceRule testInstance = new RyaTestInstanceRule();
  public static String getRyaInstanceName() {
    return testInstance.getRyaInstanceName();
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    if (ryaConn != null) {
      try {
        LOG.info("Shutting down Rya Connection.");
        ryaConn.close();
        LOG.info("Rya Connection shut down.");
      } catch (final Exception e) {
        LOG.error("Could not shut down the Rya Connection.", e);
      }

    }

    if (ryaRepo != null) {
      try {
        LOG.info("Shutting down Rya Repo.");
        ryaRepo.shutDown();
        LOG.info("Rya Repo shut down.");
      } catch (final Exception e) {
        LOG.error("Could not shut down the Rya Repo.", e);
      }
    }

    if(ryaConnector != null) {
      ryaConnector.close();
    }

    StatementPatternIdCacheSupplier.clear();
    MetadataCacheSupplier.clear();
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    if(ryaConnector != null) {
      ryaConnector.close();
    }
  }

  /**
   * Test method for {@link org.apache.any23.plugin.rya.RyaConnector#stream(org.apache.any23.source.DocumentSource, java.io.OutputStream, org.eclipse.rdf4j.rio.RDFFormat)}.
   */
  @Test
  public void testStream() {
    ryaConnector = new RyaConnector();
    try {
      ryaConnector.init();
    } catch (IOException e) {
      fail("Failed to initialize RyaConnector");
    }

    Any23 runner = new Any23();
    runner.setHTTPUserAgent("test-user-agent");
    DocumentSource source = new FileDocumentSource(
            new File(getClass().getClassLoader().getResource("PO.DAAC.html").getFile()), 
            "http://any23.apache.org");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TripleHandler handler = new TurtleWriter(out);
    try {
      runner.extract(source, handler);
    } catch (IOException | ExtractionException e) {
      fail("Error during extraction from FileDocumentSource");
    } finally {
      try {
        handler.close();
      } catch (TripleHandlerException e) {
        fail("Error closing TurtleWriter");
      }
    }
    try {
      String n3 = out.toString("UTF-8");
    } catch (UnsupportedEncodingException e) {
      fail("Unsupported UTF-8 encoding in data.");
    }

    ryaConnector.stream(source, out, RDFFormat.TURTLE);

  }

}
