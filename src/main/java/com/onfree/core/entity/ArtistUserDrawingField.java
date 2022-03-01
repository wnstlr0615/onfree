package com.onfree.core.entity;

import com.onfree.common.model.BaseEntity;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.user.ArtistUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ArtistUserDrawingField extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistUserDrawingFieldId;

    @ManyToOne
    private ArtistUser artistUser;

    @ManyToOne
    private DrawingField drawingField;
}
