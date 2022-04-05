package com.onfree.core.entity.portfoliocontent;

import com.onfree.common.model.BaseEntity;
import com.onfree.core.entity.portfolio.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class PortfolioContent extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portFolioContentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "port_folio_id")
    private Portfolio portFolio;


    //== 연관 관계 메서드 ==//
    public void setPortfolio(Portfolio portFolio) {
        if(this.portFolio != null){
            this.portFolio.getPortfolioContents().remove(this);
        }

        this.portFolio = portFolio;

        if(!portFolio.getPortfolioContents().contains(this)){
            portFolio.getPortfolioContents().add(this);
        }
    }
}
