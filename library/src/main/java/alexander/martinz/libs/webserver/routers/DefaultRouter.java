/*
 * Copyright 2015 Alexander Martinz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alexander.martinz.libs.webserver.routers;

import android.support.annotation.NonNull;

import alexander.martinz.libs.webserver.BuildConfig;
import alexander.martinz.libs.webserver.WebServerCallbacks;
import alexander.martinz.libs.webserver.handlers.StaticAssetHandler;
import alexander.martinz.libs.webserver.handlers.StaticStringHandler;
import fi.iki.elonen.router.RouterNanoHTTPD;

public class DefaultRouter extends RouterNanoHTTPD {
    private static final String ROUTE_INDEX = "/";
    private static final String ROUTE_INDEX_ALL = "/(.)+";
    private static final String ROUTE_INDEX_HTML = "/index.html";
    private static final String ROUTE_FAVICON = "/favicon.ico";

    private static final String ROUTE_ASSETS = "/assets/";
    private static final String ROUTE_ASSETS_ALL = "/assets/(.)+";
    private static final String ROUTE_VERSION = "/version";

    private final WebServerCallbacks webServerCallbacks;

    public DefaultRouter(@NonNull WebServerCallbacks webServerCallbacks, int port) {
        super(port);
        this.webServerCallbacks = webServerCallbacks;

        addMappings();
    }

    @Override public void addMappings() {
        router.setNotImplemented(NotImplementedHandler.class);
        router.setNotFoundHandler(Error404UriHandler.class);

        final StaticAssetHandler staticAssetHandler = new StaticAssetHandler(webServerCallbacks, "assets", true);
        addRoute(ROUTE_ASSETS, staticAssetHandler);
        addRoute(ROUTE_ASSETS_ALL, staticAssetHandler);
        addRoute(ROUTE_VERSION, new VersionHandler());

        // handle / and /index.html
        final StaticAssetHandler staticIndexHandler = new StaticAssetHandler(webServerCallbacks, "index.html");
        final StaticAssetHandler staticFavIconHandler = new StaticAssetHandler(webServerCallbacks, "favicon.ico");
        addRoute(ROUTE_INDEX, staticIndexHandler);
        addRoute(ROUTE_INDEX_HTML, staticIndexHandler);
        addRoute(ROUTE_FAVICON, staticFavIconHandler);

        // handle index wildcard last, as it matches all
        final StaticAssetHandler staticAssetHandlerGeneric = new StaticAssetHandler(webServerCallbacks);
        addRoute(ROUTE_INDEX_ALL, staticAssetHandlerGeneric);
    }

    public static class VersionHandler extends StaticStringHandler {
        public VersionHandler() {
            super(BuildConfig.VERSION_NAME);
        }
    }
}
