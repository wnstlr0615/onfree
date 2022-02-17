package com.onfree.core.entity.portfolio;

import com.onfree.common.model.BaseEntity;
import com.onfree.core.entity.PortfolioDrawingField;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.user.ArtistUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Enumerated(EnumType.STRING)
    private PortfolioStatus status; //포트폴리오 상태

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
            String tags, List<PortfolioDrawingField> portfolioDrawingFields, PortfolioStatus status
    ) {
        final String imageUrl = StringUtils.hasText(mainImageUrl) ? mainImageUrl : "defaultImageUrl";

        final Portfolio portfolio = Portfolio.builder()
                .artistUser(artistUser)
                .title(title)
                .mainImageUrl(imageUrl)
                .tags(tags)
                .view(0L)
                .status(status)
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
        if(isStatusEquals(PortfolioStatus.NORMAL) || isStatusEquals(PortfolioStatus.REPRESENTATION)){
            this.view++;
        }
    }

    // 삭제 처리
    public void remove() {
        status = PortfolioStatus.DELETED;
    }

    //대표 설정 취소
    public void representCancel(){
        status = PortfolioStatus.NORMAL;
    }
    //대표 설정
    public void represent(){
        status = PortfolioStatus.REPRESENTATION;
    }

    // 사용자 그림분야를 그림분야 식별자 리스트로 반환
    public List<Long> getDrawingFieldIds(){
        return portfolioDrawingFields.stream()
                .map(PortfolioDrawingField::getDrawingFieldId)
                .collect(Collectors.toList());

    }
    public boolean isStatusEquals(PortfolioStatus portfolioStatus){
        return this.status.equals(portfolioStatus);

    }
    public void updatePortfolio(
            String title, String mainImageUrl, List<PortfolioContent> contents,
            List<PortfolioDrawingField> portfolioDrawingFields, List<String> tags, PortfolioStatus status) {
        this.title = title;
        this.mainImageUrl = mainImageUrl;
        this.tags = String.join(",", tags);
        this.status = status;

        //TODO 삭제 하지 않고 효율적으로 내용 가져오기
        //포트폴리오 내용 설정
        for (PortfolioContent content : contents) {
            this.addPortfolioContent(content);
        }

        this.portfolioDrawingFields.clear();
        for (PortfolioDrawingField portfolioDrawingField : portfolioDrawingFields) {
            this.addPortfolioDrawingField(portfolioDrawingField);
        }

    }
}
