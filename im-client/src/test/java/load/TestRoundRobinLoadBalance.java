package load;

import com.bigyj.entity.ServerNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TestRoundRobinLoadBalance {
    private static final String CLIENT_KEY = "IM_CLIENT" ;
    private List<ServerNode> list = new ArrayList<>();
    Map<String, ConcurrentHashMap<String,WeightedRoundRobin>> nodes = new ConcurrentHashMap<>();
    @BeforeEach
    void initBaseServer(){
        list.add(new ServerNode(1L,"127.0.0.1",8001,100));
        list.add(new ServerNode(2L,"127.0.0.1",8002,200));
        list.add(new ServerNode(3L,"127.0.0.1",8003,300));
        list.add(new ServerNode(4L,"127.0.0.1",8004,400));
        list.add(new ServerNode(5L,"127.0.0.1",8005,500));
    }
    @Test
    void testRoundRobinLoadBalanceSelect(){
        for(int i = 0 ;i<15;i++){
            doSelect();
        }
    }

    private void doSelect() {
        ConcurrentHashMap<String, WeightedRoundRobin> map = nodes.computeIfAbsent(CLIENT_KEY, key ->new ConcurrentHashMap<>());
        long maxValue =Long.MIN_VALUE;
        long now = System.currentTimeMillis();
        int total = 0;
        ServerNode selectedServerNode = null;
        WeightedRoundRobin selectedWRR =null;
        for(ServerNode serverNode:list){
            String serverKey = serverNode.toString();
            Integer weight = serverNode.getWeight();
            WeightedRoundRobin weightObject = map.computeIfAbsent(serverKey, key -> {
                WeightedRoundRobin weightedRoundRobin = new WeightedRoundRobin();
                weightedRoundRobin.setWeight(weight);
                weightedRoundRobin.setLastUpdate(now);
                return weightedRoundRobin;
            });
            //weight changed
            if (weight != weightObject.getWeight()) {
                weightObject.setWeight(weight);
            }
            long nextWeight = weightObject.increaseCurrent();
            //判断加上权重是否大于当前的最大值，
            if(nextWeight > maxValue){
                selectedServerNode = serverNode;
                selectedWRR = weightObject;
                maxValue = nextWeight;
            }
            total+=weight;
        };
        selectedWRR.sel(total);
        if(selectedServerNode!=null){
            System.out.println(selectedServerNode);
        }
    }

    protected static class WeightedRoundRobin {
        private int weight;
        private AtomicLong current = new AtomicLong(0);
        private long lastUpdate;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }

        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        public void sel(int total) {
            current.addAndGet(-1 * total);
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }
}
