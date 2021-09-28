package io.github.jasonheo;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableKubernetesMockClient(crud = true)
public class CrudModeTest {
    String namespace = "ns1";

    /**
     * 헷갈리는 점
     *   - 아래처럼 생성해도 되는데, @EnableKubernetesMockClient(crud = true) annotation을 사용하라고 한 이유
     *   - KubernetesServer server = new KubernetesServer(false, true);
     */
    KubernetesMockServer server;
    KubernetesClient client;

    @BeforeEach
    public void beforeEach() {
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

        client
                .pods()
                .inNamespace(namespace)
                .create(pod1);

        client
                .pods()
                .inNamespace(namespace)
                .create(pod2);

    }

    @AfterEach
    public void afterEach() {
        server.destroy();
    }

    @Test
    public void testListPods() {
        int numPods = client
                .pods()
                .inNamespace(namespace)
                .list()
                .getItems()
                .size();

        assertEquals(2, numPods);
    }
}
