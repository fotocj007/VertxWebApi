package com.webser.redis;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashWithNode {
    private static final String VIR_NODE_NAME_SEPARATOR = "@VirNode";

    private static final List<String> instanceInfo = new LinkedList<>();

    /**
     * 初始化虚拟节点 key表示服务器虚拟节点的hash值，value表示服务器虚拟节点的名称
     */
    private static final SortedMap<Integer, String> serverHashMap = new TreeMap<>();
    /**
     * 设置每台服务器需要的虚拟节点
     */
    private static final int VIRTUAL_NODES = 1000;

    /**
     * 构建hash环
     * @param servers
     */
    public ConsistentHashWithNode(List<String> servers) {
        //首先本地缓存一份实例信息
        instanceInfo.addAll(servers);
        instanceInfo.forEach(instance -> {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                //构建虚拟节点
                String virNodeName = instance + VIR_NODE_NAME_SEPARATOR + i;
                serverHashMap.put(hash(virNodeName), virNodeName);
            }
        });
    }

    public static void addNode(String server) {
        //首先本地缓存一份实例信息
        instanceInfo.add(server);
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            //构建虚拟节点
            String virNodeName = server + VIR_NODE_NAME_SEPARATOR + i;
            serverHashMap.put(hash(virNodeName), virNodeName);
        }
    }

    /**
     * 根据数据获取真实存储服务节点
     * @param data
     * @return
     */
    public static String getServer(String data) {
        Integer firstKey;
        SortedMap<Integer, String> subSortedMap = serverHashMap.tailMap(hash(data));
        if (subSortedMap.isEmpty()){
            firstKey = serverHashMap.firstKey();
        }else{
            firstKey = subSortedMap.firstKey();
        }

        String virNodeName = serverHashMap.get(firstKey);
        return virNodeName.substring(0, virNodeName.indexOf(VIR_NODE_NAME_SEPARATOR));
    }

    /**
     * FNV1_32_HASH 百度
     * @param str
     * @return
     */
    public static int hash(String str) {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0)
            hash = Math.abs(hash);
        return Math.abs(hash);
    }
}
