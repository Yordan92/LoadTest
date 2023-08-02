package com.webint.loadtest.dto;

public class MachineLoadStatisticsDTO {
    MinMaxAvgDTO<Long> ram = new MinMaxAvgDTO<Long>(0L);
    MinMaxAvgDTO<Double> cpuLoad = new MinMaxAvgDTO<>(0.0);

    public MachineLoadStatisticsDTO() {
    }


    public MinMaxAvgDTO<Double> getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(MinMaxAvgDTO<Double> cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public MinMaxAvgDTO<Long> getRam() {
        return ram;
    }

    public void setRam(MinMaxAvgDTO<Long> ram) {
        this.ram = ram;
    }

    public void addCpuLoad(double cpuUsageInPresent) {
        if (cpuUsageInPresent == 0) {
            return;
        }
        cpuLoad.addMinMax(cpuUsageInPresent);
        cpuLoad.setSum(cpuLoad.getSum() + cpuUsageInPresent);
        cpuLoad.setTimes(cpuLoad.getTimes() + 1);
        cpuLoad.setAvg(cpuLoad.getSum() / cpuLoad.getTimes());
    }

    public void addRam(long usedRam) {
        ram.addMinMax(usedRam);
        ram.setSum(ram.getSum() + usedRam);
        ram.setTimes(ram.getTimes() + 1);
        ram.setAvg(ram.getSum() / ram.getTimes());
    }
}