package com.onfree.core.entity;

import com.onfree.common.model.BaseEntity;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.portfolio.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PortfolioDrawingField extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioDrawingFieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    private DrawingField drawingField;

    //== 연관 관계 편의 메소드  ==//
    public void setPortfolio(Portfolio portfolio) {
        if(portfolio != null){
            portfolio.getPortfolioDrawingFields().remove(this);
        }

        this.portfolio = portfolio;

        if(!portfolio.getPortfolioDrawingFields().contains(drawingField)){
            portfolio.getPortfolioDrawingFields().add(this);
        }
    }

    //== 생성 메소드 ==//
    public static PortfolioDrawingField createPortfolioDrawingField(@NonNull DrawingField drawingField){
        return PortfolioDrawingField.builder()
                .drawingField(drawingField)
                .build();
    }

    //== 비즈니스 로직 ==//
    //그림분야 식별키 반환
    public Long getDrawingFieldId(){
        return drawingField.getDrawingFieldId();
    }

    //그림분야명 반환
    public String getDrawingFieldName(){
        return drawingField.getFieldName();
    }

}
