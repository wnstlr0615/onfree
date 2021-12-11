package com.onfree.core.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum BankName {
    KDB_SANUP_BANK("KDB산업은행","002"),
    IBK_BANK("IBK기업은행", "003"),
    KOOKMIN_BANK("KB국민은행", "004"),
    NONGHYUP_BANK ("NH농협은행","011"),
    WOORI_BANK("우리은행", "020"),
    SC_FIRST_BANK_KOREA("SC제일은행", "023"),
    CITIBANK_KOREAD_INC("한국씨티은행", "027"),
    DAEGU_BANK("대구은행", "031"),
    BUSAN_BANK("부산은행", "032"),
    KWANGJU_BANK("광주은행", "034"),
    JEJU_BANK("제주은행", "035"),
    JEONBUK_BANK("전북은행", "037"),
    KYONGNAM_BANK("경남은행", "039"),
    KOREA_POST_OFFICE("우정사업본부(우체국)", "071"),
    KEB_HANA_BANK("하나은행", "081"),
    SHINHAN_BANK("신한은행", "088"),
    K_BANK("케이뱅크", "089"),
    CITIBANK_KOREA_INC_KAKAO("카카오뱅크", "090"),

    /** 상호금융기관 */
    SUHYUP_BANK ("수협중앙회", "007"),
    NONGHYUP_CENTER_BANK("농협중앙회", "012"),
    KFCC_BANK("새마을금고중앙회", "045"),
    SINHYUP_BANK("신협중앙회", "048");

    private final String bankName;
    private final String code;

    public static String joinString(){
        return Arrays.stream(BankName.values()).map(String::valueOf).collect(Collectors.joining(","));
    }
}
