package com.onfree.core.entity.requestapply;

import com.onfree.common.error.code.RequestApplyErrorCode;
import com.onfree.common.error.exception.RequestApplyException;
import com.onfree.common.model.BaseTimeEntity;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class RequestApply extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestApplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id")
    private User clientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_user_id")
    private ArtistUser artistUser;

    @Enumerated(EnumType.STRING)
    private RequestApplyStatus status;

    //== 생성 메서드 ==//
    public RequestApply(User clientUser, ArtistUser artistUser, RequestApplyStatus status) {
        this.clientUser = clientUser;
        this.artistUser = artistUser;
        this.status = status;
    }

    //== 비즈니스 메서드 ==//

    //계약금 결제 가능한 상태인지 확인
    public void verifyStatusIsAcceptableForPayment(){
        switch (status){
            case READY:
                throw new RequestApplyException(RequestApplyErrorCode.DO_NOT_GET_STATEMENT);
            case RECEIVED_STATEMENT:
            case STATEMENT_ACCEPT:
                break;
            case CANCEL:
                throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_DEPOSIT_CANCELED_TRANSACTION);
            default:
                throw new RequestApplyException(RequestApplyErrorCode.ALREADY_DEPOSIT_PAYMENT);
        }
    }

    // 견적서를 받은 상태로 변경
    public void changeStatusToReceivedStatement(){
        verifyTheAvailabilityStatement();
        this.status = RequestApplyStatus.RECEIVED_STATEMENT;
    }

    //명세서를 받을 수 있는 상태인지 검증
    private void verifyTheAvailabilityStatement() {
        switch (status){
            case READY:
            case RECEIVED_STATEMENT:
                break;
            default:
                throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_RECEIVED_STATEMENT);
        }
    }
    // 결제 대금 입금 완료 상태로 변경
    public void changeStatusToDepositDownPayment(){
        verifyStatusIsAcceptableForPayment();
        this.status = RequestApplyStatus.DEPOSIT_DOWN_PAYMENT;
    }


    //해당 의뢰에 연관된 유저 인지 확인
    public boolean notContainUser(User user){
        return !clientUser.isEqualsUserId(user) && !artistUser.isEqualsUserId(user);
    }

    //수신자 찾기
    public User getReceiver(User sender){
        if(notContainUser(sender)){
            throw new RequestApplyException(RequestApplyErrorCode.REQUEST_APPLY_DOES_NOT_CONTAIN_SENDER);
        }
        return clientUser.isEqualsUserId(sender) ? artistUser : clientUser;
    }

    //결제 요청 상태로 변경
    public void changeStatusToPaymentRequest() {
        verifyStatusIsDepositDownPayment();
        this.status = RequestApplyStatus.PAYMENT_REQUEST;
    }

    //입금 요청 가능 상태 여부 확인
    private void verifyStatusIsDepositDownPayment() {
        switch (status){
            case READY:
            case RECEIVED_STATEMENT:
            case STATEMENT_ACCEPT:
                throw new RequestApplyException(RequestApplyErrorCode.NO_DOWN_PAYMENT_HAS_BEEN_MADE);
            case DEPOSIT_DOWN_PAYMENT:
                break;
            case CANCEL:
                throw new RequestApplyException(RequestApplyErrorCode.REQUEST_APPLY_IS_CANCEL);
            default:
                throw new RequestApplyException(RequestApplyErrorCode.ALREADY_REQUEST_PAYMENT);
        }
    }

    // 결제 완료 상태로 변경
    public void changeStatusToPaymentCompleted() {
        verifyStatusIsPaymentRequest();
        this.status = RequestApplyStatus.PAYMENT_COMPLETED;
    }

    //의뢰 상태가 결제 지급 요청인지 확인
    private void verifyStatusIsPaymentRequest() {
        switch (status){
            case READY:
            case RECEIVED_STATEMENT:
            case STATEMENT_ACCEPT:
                throw new RequestApplyException(RequestApplyErrorCode.NO_DOWN_PAYMENT_HAS_BEEN_MADE);
            case DEPOSIT_DOWN_PAYMENT:
                throw new RequestApplyException(RequestApplyErrorCode.DO_NOT_PAYMENT_REQUEST);
            case PAYMENT_REQUEST:
                break;
            case CANCEL:
                throw new RequestApplyException(RequestApplyErrorCode.REQUEST_APPLY_IS_CANCEL);
            case PAYMENT_COMPLETED:
            default:
                throw new RequestApplyException(RequestApplyErrorCode.ALREADY_PAYMENT_REQUEST_COMPLETED);
        }
    }

    //환불 요청 상태로 변경
    public void changeStatusToRefundRequest() {
        verifyStatusIsPaymentCompleted();
        this.status = RequestApplyStatus.REFUND_REQUEST;
    }

    // 의뢰상태가 결제 대금을 입금한 상태인지 확인(환불 요청 가능한 상태인지 확인)
    private void verifyStatusIsPaymentCompleted() {
        switch (status){
            case PAYMENT_COMPLETED:
                break;
            case CANCEL:
                throw new RequestApplyException(RequestApplyErrorCode.REQUEST_APPLY_IS_CANCEL);
            default:
                throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_REFUND_REQUEST);
        }
    }

    public void changeStatusToRefundCompleted() {
        verifyStatusIsRefundRequest();
        this.status = RequestApplyStatus.REFUND_COMPLETED;
    }
    private void verifyStatusIsRefundRequest() {
            switch (status){
                case REFUND_REQUEST:
                    break;
                case REFUND_COMPLETED:
                    throw new RequestApplyException(RequestApplyErrorCode.ALREADY_CANCELED_PAYMENT);
                case CANCEL:
                    throw new RequestApplyException(RequestApplyErrorCode.REQUEST_APPLY_IS_CANCEL);
                default:
                    throw new RequestApplyException(RequestApplyErrorCode.NOT_GET_REFUND_REQUEST);
            }
    }


}