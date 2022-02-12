package com.onfree.core.entity.portfoliocontent;

import com.onfree.core.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "T")
@Builder
public class TextContent extends PortfolioContent {
    private String text;

    public static TextContent createTextContent(String text){
        return TextContent.builder()
                .text(text)
                .build();
    }
}
