package io.github.jasonheo;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

public class NonCrudMode {
    static String namespace = "ns1";

    public static void main(String[] args) {
        KubernetesServer server = new KubernetesServer(false, false);

        // mock 서버 실행
        server.before();

        NonCrudMode nonCrudMode = new NonCrudMode();

        // API 요청에 대한 응답 정의
        nonCrudMode.setUp(server);

        // pod 목록 출력
        nonCrudMode.listPods(server);

        // mock 서버 종료
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

        // input: "/api/v1/namespaces/ns1/pods" 요청이 인입된 경우
        // response: pod 2개를 return하도록 한다
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
