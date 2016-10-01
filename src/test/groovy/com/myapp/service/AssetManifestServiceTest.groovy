package com.myapp.service

import com.myapp.config.AppConfig
import com.myapp.dto.AssetManifest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared

@ContextConfiguration(classes = [TestConfig, AppConfig, CacheTestConfig])
class AssetManifestServiceTest extends BaseServiceTest {

    @Configuration
    static class TestConfig {
        @Bean
        AssetManifestService assetManifestService(AppConfig appConfig) {
            return new AssetManifestServiceImpl(appConfig);
        }
    }

    @Shared
    MockWebServer server = new MockWebServer()

    @Autowired
    AppConfig appConfig

    @Autowired
    AssetManifestService assetManifestService

    def setup() {
        server.start()
    }

    def cleanup() {
        server.shutdown()
    }

    def "can fetch asset manifest and cache"() {
        AssetManifest assetManifest

        when:
        server.enqueue(new MockResponse().setBody("{\"main.js\": \"main.b2620c3b7bbeeaadcd9d.js\"}"));
        appConfig.setAssetManifestUrl(server.url('/test').toString())
        assetManifest = assetManifestService.fetchAssetManifest()

        then:
        assetManifest.get("main.js") == "main.b2620c3b7bbeeaadcd9d.js"

        when:
        server.enqueue(new MockResponse().setBody("{\"main.js\": \"main.123.js\"}"));
        appConfig.getAssetManifestUrl() >> server.url('/test')
        assetManifest = assetManifestService.fetchAssetManifest()

        then:
        // still using cache
        assetManifest.get("main.js") == "main.b2620c3b7bbeeaadcd9d.js"

        when:
        assetManifestService.invalidateCache()
        assetManifest = assetManifestService.fetchAssetManifest()

        then:
        assetManifest.get("main.js") == "main.123.js"
    }

}
