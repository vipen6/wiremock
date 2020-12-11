package com;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockDemoTest {
    static WireMockServer wireMockServer;

    @BeforeAll
    public static void init(){
        wireMockServer = new WireMockServer(wireMockConfig().port(8089)); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();
        configureFor("localhost",8089);

    }

    @Test
    public void stubMock(){
        try {
            stubFor(get(urlEqualTo("/my/resource"))
                    .withHeader("Accept", equalTo("text/xml"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml")
                            .withBody("<response>Some content</response>")));
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void easy_mock(){
        try {
            stubFor(get(urlEqualTo("/my/resource"))
                    .withHeader("Accept", equalTo("text/xml"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml")
                            .withBody("<response>Some content返回了值</response>")));
            //等待后，重置下
            Thread.sleep(10000);

            reset();

            stubFor(get(urlEqualTo("/my/resource"))
                    .withHeader("Accept", equalTo("text/xml"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml")
                            .withBody("<response>等待10秒后的返回</response>")));

            Thread.sleep(500000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void proxyMockTest(){
        try {
            // Low priority catch-all proxies to otherhost.com by default
            stubFor(get(urlMatching(".*")).atPriority(10)
                    .willReturn(aResponse().proxiedFrom("https://ceshiren.com")));

            // High priority stub will send a Service Unavailable response
// if the specified URL is requested
            stubFor(get(urlEqualTo("/categories_and_latest")).atPriority(10)
                    .willReturn(aResponse().withBody(Files.readAllBytes(Paths.get(WireMockDemoTest.class.getResource("/mock.json").getPath())))));
        Thread.sleep(500000);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
