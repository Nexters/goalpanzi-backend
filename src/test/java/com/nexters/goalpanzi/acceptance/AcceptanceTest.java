//package com.nexters.goalpanzi.acceptance;
//
//import com.nexters.goalpanzi.application.auth.SocialUserProvider;
//import com.nexters.goalpanzi.application.auth.apple.*;
//import io.restassured.RestAssured;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//public abstract class AcceptanceTest {
//
//    @LocalServerPort
//    private int port;
//
//    @MockBean
//    protected SocialUserProvider socialUserProvider;
//    @MockBean
//    protected AppleTokenProvider appleTokenProvider;
//    @MockBean
//    protected ApplePublicKeyGenerator applePublicKeyGenerator;
//
//    @BeforeEach
//    void setUp() {
//        RestAssured.port = port;
//    }
//
//    @AfterEach
//    void afterEach() {
//    }
//}