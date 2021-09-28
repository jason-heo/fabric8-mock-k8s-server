package io.github.jasonheo;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

public class CrudMode {
    static String namespace = "ns1";

    public static void main(String[] args) throws Throwable {
        KubernetesServer server = new KubernetesServer(false, true);

        // mock server 실행
        server.before();

        CrudMode crudMode = new CrudMode();

        // pod 생성: pod 2개를 생성한다
        crudMode.setUp(server);

        // pod 목록 출력: pod 2개가 출력된다
        crudMode.listPods(server);

        // pod 삭제: pod 1개를 삭제한다
        server.getClient().pods().inNamespace(namespace).withName("pod1").delete();

        // pod 목록 출력: pod 1개가 출력된다
        crudMode.listPods(server);

        // mock server 종료
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
        // 아래 코드는 `kubectl get pods`에 대응되는 코드이다
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
