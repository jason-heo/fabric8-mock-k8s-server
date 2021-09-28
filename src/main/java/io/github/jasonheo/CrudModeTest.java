package com.naver.io;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

public class CrudModeTest {
    static String namespace = "ns1";

    public static void main(String[] args) {
        KubernetesServer server = new KubernetesServer(false, true);

        server.before();

        CrudModeTest test = new CrudModeTest();

        test.setUp(server);

        test.listPods(server);

        server.getClient().pods().inNamespace(namespace).withName("pod1").delete();

        test.listPods(server);

        server.after();
    }

    public void setUp(KubernetesServer server) {
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
