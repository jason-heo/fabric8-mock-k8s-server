package com.naver.io;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

public class NonCrudModeTest {
    static String namespace = "ns1";

    public static void main(String[] args) {
        KubernetesServer server = new KubernetesServer(false, false);

        server.before();

        NonCrudModeTest test = new NonCrudModeTest();

        test.setUp(server);

        test.listPods(server);

        server.after();
    }

    public void setUp(KubernetesServer server) {
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

    public void listPods(KubernetesServer server) {
        KubernetesClient client = server.getClient();
        client
                .pods()
                .inNamespace(namespace)
                .list()
                .getItems()
                .stream()
                .forEach(pod -> {
                    System.out.println(pod.getMetadata().getName());
                });

    }
}
