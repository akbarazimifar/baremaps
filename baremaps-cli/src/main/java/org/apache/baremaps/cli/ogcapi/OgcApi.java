/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.baremaps.cli.ogcapi;

import static io.servicetalk.data.jackson.jersey.ServiceTalkJacksonSerializerFeature.newContextResolver;
import static org.apache.baremaps.utils.ObjectMapperUtils.objectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import io.servicetalk.http.api.BlockingStreamingHttpService;
import io.servicetalk.http.netty.HttpServers;
import io.servicetalk.http.router.jersey.HttpJerseyRouterBuilder;
import io.servicetalk.transport.api.ServerContext;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import javax.sql.DataSource;
import org.apache.baremaps.cli.Options;
import org.apache.baremaps.config.ConfigReader;
import org.apache.baremaps.ogcapi.*;
import org.apache.baremaps.server.CorsFilter;
import org.apache.baremaps.tilestore.TileCache;
import org.apache.baremaps.tilestore.TileStore;
import org.apache.baremaps.tilestore.postgres.PostgresTileStore;
import org.apache.baremaps.utils.PostgresUtils;
import org.apache.baremaps.vectortile.tileset.Tileset;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "ogcapi", description = "OGC API server (experimental).")
public class OgcApi implements Callable<Integer> {

  private static final Logger logger = LoggerFactory.getLogger(OgcApi.class);

  @Mixin
  private Options options;

  @Option(names = {"--database"}, paramLabel = "DATABASE",
      description = "The JDBC url of Postgres.", required = true)
  private String database;

  @Option(names = {"--cache"}, paramLabel = "CACHE", description = "The caffeine cache directive.")
  private String cache = "";

  @Option(names = {"--tileset"}, paramLabel = "TILESET", description = "The tileset file.",
      required = true)
  private Path tileset;

  @Option(names = {"--style"}, paramLabel = "STYLE", description = "The style file.",
      required = true)
  private Path style;

  @Option(names = {"--host"}, paramLabel = "HOST", description = "The host of the server.")
  private String host = "localhost";

  @Option(names = {"--port"}, paramLabel = "PORT", description = "The port of the server.")
  private int port = 9000;

  @Override
  public Integer call() throws Exception {
    // Configure serialization
    var objectMapper = objectMapper();
    var configReader = new ConfigReader();
    var config = objectMapper.readValue(configReader.read(this.tileset), Tileset.class);
    var caffeineSpec = CaffeineSpec.parse(cache);
    var dataSource = PostgresUtils.createDataSource(database);

    var tileStore = new PostgresTileStore(dataSource, config);
    var tileCache = new TileCache(tileStore, caffeineSpec);

    // Initialize the application
    var application = new ResourceConfig()
        .registerClasses(
            CorsFilter.class,
            MultiPartFeature.class,
            DefaultResource.class,
            ApiResource.class,
            ConformanceResource.class,
            CollectionsResource.class,
            StylesResource.class,
            TilesResource.class)
        .register(newContextResolver(objectMapper))
        .register(new AbstractBinder() {
          @Override
          protected void configure() {
            bind(tileset).to(Path.class).named("tileset");
            bind(style).to(Path.class).named("style");
            bind(dataSource).to(DataSource.class);
            bind(tileCache).to(TileStore.class);
            bind(objectMapper).to(ObjectMapper.class);
          }
        });

    BlockingStreamingHttpService httpService =
        new HttpJerseyRouterBuilder().buildBlockingStreaming(application);
    ServerContext serverContext =
        HttpServers.forPort(port).listenBlockingStreamingAndAwait(httpService);

    logger.info("Listening on {}", serverContext.listenAddress());

    serverContext.awaitShutdown();

    return 0;
  }
}
