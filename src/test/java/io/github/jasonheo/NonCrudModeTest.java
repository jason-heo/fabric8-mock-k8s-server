package io.github.jasonheo;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableKubernetesMockClient(crud = false)
public class NonCrudModeTest {
    String namespace = "ns1";

    KubernetesMockServer server;
    KubernetesClient client;

    @BeforeEach
    public void beforeEach() {
        Pod pod1 = new PodBuilder()
                .withNewMetadata()
                .withName("pod1")
                .and()
                .build();

        Pod pod2 = new PodBuilder()
                .withNewMetadata()
                .withName("pod2")
                .and()
                .build();

        server
                .expect()
                .get()
                .withPath(String.format("/api/v1/namespaces/%s/pods", namespace))
                .andReturn(200, new
                        PodListBuilder()
                        .withNewMetadata()
                        .withResourceVersion("1")
                        .endMetadata()
                        .withItems(pod1, pod2)
                        .build())
                .always();
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
