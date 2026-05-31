package com.kset.dubbo.route;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;
import org.apache.dubbo.rpc.cluster.router.RouterResult;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Activate(order = 1)
public class KsetTagRouterFactory implements RouterFactory {

    @Override
    public Router getRouter(org.apache.dubbo.common.URL url) {
        return new KsetTagRouter(url);
    }

    static class KsetTagRouter implements Router {

        private final org.apache.dubbo.common.URL url;

        KsetTagRouter(org.apache.dubbo.common.URL url) {
            this.url = url;
        }

        @Override
        public <T> RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, org.apache.dubbo.common.URL url,
                                                  Invocation invocation, boolean needToPrintMessage) throws RpcException {
            if (invokers == null || invokers.isEmpty()) {
                return new RouterResult<>(invokers);
            }

            String metadataKey = url.getParameter("kset.gray.metadata.key", DubboRouteRuleHolder.getMetadataKey());
            List<Invoker<T>> matched = filterByRouteRules(invokers, metadataKey);
            if (matched.isEmpty()) {
                return new RouterResult<>(invokers);
            }
            return new RouterResult<>(matched);
        }

        private <T> List<Invoker<T>> filterByRouteRules(List<Invoker<T>> invokers, String metadataKey) {
            List<DubboRouteRuleHolder.RouteCondition> conditions = DubboRouteRuleHolder.getConditions();
            if (conditions.isEmpty()) {
                return List.of();
            }

            int totalWeight = conditions.stream().mapToInt(DubboRouteRuleHolder.RouteCondition::getWeight).sum();
            int random = ThreadLocalRandom.current().nextInt(Math.max(totalWeight, 1));
            int current = 0;
            String selectedTag = conditions.get(0).getTag();
            for (DubboRouteRuleHolder.RouteCondition condition : conditions) {
                current += condition.getWeight();
                if (random < current) {
                    selectedTag = condition.getTag();
                    break;
                }
            }

            String tag = selectedTag;
            return invokers.stream()
                    .filter(inv -> tag.equals(inv.getUrl().getParameter(metadataKey)))
                    .collect(Collectors.toList());
        }

        @Override
        public org.apache.dubbo.common.URL getUrl() {
            return url;
        }

        @Override
        public boolean isRuntime() {
            return true;
        }

        @Override
        public boolean isForce() {
            return false;
        }

        @Override
        public int getPriority() {
            return 0;
        }
    }
}
