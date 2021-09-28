package io.github.jasonheo;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableKubernetesMockClient(crud = true)
public class RestAssuredTest {
    static String namespace = "ns1";

    KubernetesServer server = new KubernetesServer(false, true);

    @BeforeEach
    public void beforeEach() {
        server.before();

        Pod pod1 = new PodBuilder()
                .withNewMetadata()
                .withName("pod1")
                .withNamespace(namespace)
                .and()
                .build();

        Pod pod2 = new PodBuilder()
                .withNewMetadata()
                .withName("pod2")
                .withNamespace(namespace)
                .and()
                .build();

        server
                .getClient()
                .pods()
                .inNamespace(namespace)
                .create(pod1);
        server
                .getClient()
                .pods()
                .inNamespace(namespace)
                .create(pod2);

    }

    @AfterEach
    public void afterEach() {
        server.after();
    }

    @Test
    public void testListPods() {
        // mock server의 hostname과 port를 조회하는 방법
        String hostname = server.getMockServer().getHostName();
        int port = server.getMockServer().getPort();

        String url = "http://" + hostname + ":" + port + "/api/v1/namespaces/" + namespace + "/pods";

        // mock server의 response content type이 json이 아니다
        // 따라서 response 전체를 string 변수에 저장했다
        String response = RestAssured
                .given()
                    .log()
                    .all()
                .when()
                    .get(url)
                    .asString();

        List<String> items = from(response).getList("items");

        assertEquals(2, items.size());
    }
}
