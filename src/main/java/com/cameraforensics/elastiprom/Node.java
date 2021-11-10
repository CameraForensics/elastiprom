package com.cameraforensics.elastiprom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Node {

    private String ip;

    @JsonProperty("heap.percent")
    private int heapPercent;

    @JsonProperty("ram.percent")
    private int ramPercent;

    @JsonProperty("cpu")
    private int cpu;

    @JsonProperty("master")
    private String master;

    @JsonProperty("load_1m")
    private Double load1m;

    @JsonProperty("load_5m")
    private Double load5m;

    @JsonProperty("load_15m")
    private Double load15m;

    @JsonProperty("node.role")
    private String role;

    private String name;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getHeapPercent() {
        return heapPercent;
    }

    public void setHeapPercent(int heapPercent) {
        this.heapPercent = heapPercent;
    }

    public int getRamPercent() {
        return ramPercent;
    }

    public void setRamPercent(int ramPercent) {
        this.ramPercent = ramPercent;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public Double getLoad1m() {
        return load1m;
    }

    public void setLoad1m(Double load1m) {
        this.load1m = load1m;
    }

    public Double getLoad5m() {
        return load5m;
    }

    public void setLoad5m(Double load5m) {
        this.load5m = load5m;
    }

    public Double getLoad15m() {
        return load15m;
    }

    public void setLoad15m(Double load15m) {
        this.load15m = load15m;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMaster() {
        return "*".equals(master);
    }
}
