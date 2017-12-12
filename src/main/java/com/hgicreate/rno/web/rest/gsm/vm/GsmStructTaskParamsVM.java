package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmStructTaskParamsVM {
    private String SAMEFREQINTERTHRESHOLD;
    private String OVERSHOOTINGIDEALDISMULTIPLE ;
    private String  BETWEENCELLIDEALDISMULTIPLE ;
    private String  CELLCHECKTIMESIDEALDISMULTIPLE;
    private String  CELLDETECTCITHRESHOLD;
    private String  CELLIDEALDISREFERENCECELLNUM ;
    private String  GSM900CELLFREQNUM;
    private String  GSM1800CELLFREQNUM;
    private String  GSM900CELLIDEALCAPACITY;
    private String  GSM1800CELLIDEALCAPACITY;
    private String  DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD;
    private String  ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD;
    private String  INTERFACTORMOSTDISTANT;
    private String  INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD;
    private String  RELATIONNCELLCITHRESHOLD;

    private String  TOTALSAMPLECNTSMALL;
    private String  TOTALSAMPLECNTTOOSMALL ;
    private String  SAMEFREQINTERCOEFBIG ;
    private String  SAMEFREQINTERCOEFSMALL;
    private String  OVERSHOOTINGCOEFRFFERDISTANT;
    private String  NONNCELLSAMEFREQINTERCOEF;
}
