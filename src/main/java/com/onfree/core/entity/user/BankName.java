package com.onfree.core.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum BankName {
    KDB("산업","KDB산업은행","002"),
    IBK("기업","IBK기업은행", "003"),
    KOOKMIN("국민","KB국민은행", "004"),
    SUHYEOP ("수협","Sh수협은행", "007"),
    NONGHYEOP ("농형","NH농협은행","011"),
    WOORI("우리","우리은행", "020"),
    SC("SC제일","SC제일은행", "023"),
    CITI("씨티","씨티은행", "027"),
    DAEGUBANK("대구","DGB대구은행", "031"),
    BUSANBANK("부산","부산은행", "032"),
    GWANGJUBANK("광주","광주은행", "034"),
    JEJUBANK("제주","제주은행", "035"),
    JEONBUKBANK("전북","전북은행", "037"),
    KYONGNAMBANK("경남", "경남은행", "039"),
    SAEMAUL("새마을", "새마을금고", "045"),
    SHINHYUP("신협","신협", "048"),
    SAVINGBANK("저축","저축은행중앙회","050"),
    SANLIM("산림", "산림조합", "064"),
    POST("우체국","우체국예금보험", "071"),
    HANA("하나","하나은행", "081"),
    SHINHAN("신한","신한은행", "088"),
    KBANK("케이","케이뱅크", "089"),
    KAKAOBANK("카카오","카카오뱅크", "090"),
    TOSSBANK("토스", "토스뱅크", "092"),
    ;

    private final String korName;
    private final String bankName;
    private final String code;

    public  String joinString(){
        return Arrays.stream(BankName.values()).map(String::valueOf).collect(Collectors.joining(","));
    }
}
