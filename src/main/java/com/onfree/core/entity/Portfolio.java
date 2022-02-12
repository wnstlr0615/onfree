package com.onfree.core.entity;

import com.onfree.common.model.BaseEntity;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.user.ArtistUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
        name = "portfolio-details-graph",
        attributeNodes = {
                @NamedAttributeNode("artistUser"),
                @NamedAttributeNode(value = "portfolioDrawingFields", subgraph = "portfolioDrawingFields-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "portfolioDrawingFields-subgraph",
                        attributeNodes = @NamedAttributeNode("drawingField")
                )
        }
)

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Portfolio extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId; //포트폴리오  PK

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistUser artistUser; // 작가

    private String mainImageUrl; //타이틀 이미지
    private String title; //제목

    @OneToMany(mappedBy = "portFolio", cascade = CascadeType.ALL)
    private List<PortfolioContent > portfolioContents = new ArrayList<>();
    private Long view; // 조회 수

    private String tags;  // 검색용 태그

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<PortfolioDrawingField> portfolioDrawingFields = new ArrayList<>(); // 그림 분야

    private boolean representative; // 대표설정
    private boolean temporary; // 임시 저장 유무
    private boolean deleted; // 삭제 여부

    //==연관 관계 메서드 ==//
    public void addPortfolioContent(@NonNull PortfolioContent portFolioContent){
        portfolioContents.add(portFolioContent);

        if(portFolioContent.getPortFolio() != this){
            portFolioContent.setPortfolio(this);
        }
    }

    public void addPortfolioDrawingField(@NonNull PortfolioDrawingField portfolioDrawingField){
        portfolioDrawingFields.add(portfolioDrawingField);

        if(portfolioDrawingField.getPortfolio() != this){
            portfolioDrawingField.setPortfolio(this);
        }
    }

    //== 생성 메서드 ==//
    public static Portfolio createPortfolio(
            ArtistUser artistUser, String mainImageUrl, String title, List<PortfolioContent> contents,
            String tags, List<PortfolioDrawingField> portfolioDrawingFields, boolean representative, boolean temporary
    ) {
        final Portfolio portfolio = Portfolio.builder()
                .artistUser(artistUser)
                .title(title)
                .mainImageUrl(mainImageUrl)
                .tags(tags)
                .view(0L)
                .representative(representative)
                .temporary(temporary)
                .deleted(false)
                .portfolioContents(new ArrayList<>())
                .portfolioDrawingFields(new ArrayList<>())
                .build();

        for (PortfolioContent content : contents) {
            portfolio.addPortfolioContent(content);
        }

        for (PortfolioDrawingField portfolioDrawingField : portfolioDrawingFields) {
            portfolio.addPortfolioDrawingField(portfolioDrawingField);
        }
        return portfolio;
    }


    //== 비즈니스 로직 ==//
    //조회 수 증가
    public void increaseView() {
        if(!isTemporary()){
            this.view++;
        }
    }

    // 삭제 처리
    public void remove() {
        this.deleted = true;
    }

    //대표 설정 취소
    public void representCancel(){
        if(isRepresentative()){
            this.representative = false;
        }
    }
    //대표 설정
    public void represent(){
        if(!isRepresentative()){
            this.representative = true;
        }
    }
    public boolean isWriter(Long userId){
        return artistUser.getUserId().equals(userId);
    }

    public void updatePortfolio(String title, String mainImageUrl, List<PortfolioContent> contents, List<PortfolioDrawingField> portfolioDrawingFields, List<String> tags, Boolean temporary) {
        this.title = title;
        this.mainImageUrl = mainImageUrl;
        this.tags = String.join(",", tags);
        this.temporary = temporary;

        //TODO 삭제 하지 않고 효율적으로 내용 가져오기
        //포트폴리오 내용 설정
        for (PortfolioContent content : contents) {
            this.addPortfolioContent(content);
        }

        this.portfolioDrawingFields.clear();
        for (PortfolioDrawingField portfolioDrawingField : portfolioDrawingFields) {
            this.addPortfolioDrawingField(portfolioDrawingField);
        }


        if(temporary){
            this.representative = false;
        }
    }
}
