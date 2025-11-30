package com.elssolution.gridanalysis.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MeterRegisterMap {

    @Value("${meterMap.vL1:0}")      private int vL1;
    @Value("${meterMap.vL2:2}")      private int vL2;
    @Value("${meterMap.vL3:4}")      private int vL3;

    @Value("${meterMap.vL12:6}")     private int vL12;
    @Value("${meterMap.vL23:8}")     private int vL23;
    @Value("${meterMap.vL31:10}")    private int vL31;

    @Value("${meterMap.iL1:12}")     private int iL1;
    @Value("${meterMap.iL2:14}")     private int iL2;
    @Value("${meterMap.iL3:16}")     private int iL3;
    @Value("${meterMap.in:18}")      private int in;

    @Value("${meterMap.pL1:20}")     private int pL1;
    @Value("${meterMap.pL2:22}")     private int pL2;
    @Value("${meterMap.pL3:24}")     private int pL3;
    @Value("${meterMap.pTotal:26}")  private int pTotal;

    @Value("${meterMap.qL1:28}")     private int qL1;
    @Value("${meterMap.qL2:30}")     private int qL2;
    @Value("${meterMap.qL3:32}")     private int qL3;
    @Value("${meterMap.qTotal:34}")  private int qTotal;

    @Value("${meterMap.sL1:36}")     private int sLL1;
    @Value("${meterMap.sL2:38}")     private int sL2;
    @Value("${meterMap.sL3:40}")     private int sL3;
    @Value("${meterMap.sTotal:42}")  private int sTotal;

    @Value("${meterMap.pfL1:44}")    private int pfL1;
    @Value("${meterMap.pfL2:46}")    private int pfL2;
    @Value("${meterMap.pfL3:48}")    private int pfL3;
    @Value("${meterMap.pfTotal:50}") private int pfTotal;

    @Value("${meterMap.frequency:52}") private int frequency;

    @Value("${meterMap.kwhImportL1:54}")     private int kwhImportL1;
    @Value("${meterMap.kwhImportL2:56}")     private int kwhImportL2;
    @Value("${meterMap.kwhImportL3:58}")     private int kwhImportL3;
    @Value("${meterMap.kwhImportTotal:60}")  private int kwhImportTotal;

    @Value("${meterMap.kvarhImportL1:62}")     private int kvarhImportL1;
    @Value("${meterMap.kvarhImportL2:64}")     private int kvarhImportL2;
    @Value("${meterMap.kvarhImportL3:66}")     private int kvarhImportL3;
    @Value("${meterMap.kvarhImportTotal:68}")  private int kvarhImportTotal;

    public int vL1() { return vL1; }
    public int vL2() { return vL2; }
    public int vL3() { return vL3; }
    public int vL12() { return vL12; }
    public int vL23() { return vL23; }
    public int vL31() { return vL31; }

    public int iL1() { return iL1; }
    public int iL2() { return iL2; }
    public int iL3() { return iL3; }
    public int in() { return in; }

    public int pL1() { return pL1; }
    public int pL2() { return pL2; }
    public int pL3() { return pL3; }
    public int pTotal() { return pTotal; }

    public int qL1() { return qL1; }
    public int qL2() { return qL2; }
    public int qL3() { return qL3; }
    public int qTotal() { return qTotal; }

    public int sL1() { return sLL1; }
    public int sL2() { return sL2; }
    public int sL3() { return sL3; }
    public int sTotal() { return sTotal; }

    public int pfL1() { return pfL1; }
    public int pfL2() { return pfL2; }
    public int pfL3() { return pfL3; }
    public int pfTotal() { return pfTotal; }

    public int frequency() { return frequency; }

    public int kwhImportL1() { return kwhImportL1; }
    public int kwhImportL2() { return kwhImportL2; }
    public int kwhImportL3() { return kwhImportL3; }
    public int kwhImportTotal() { return kwhImportTotal; }

    public int kvarhImportL1() { return kvarhImportL1; }
    public int kvarhImportL2() { return kvarhImportL2; }
    public int kvarhImportL3() { return kvarhImportL3; }
    public int kvarhImportTotal() { return kvarhImportTotal; }
}
