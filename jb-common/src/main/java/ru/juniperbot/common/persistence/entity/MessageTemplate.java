/*
 * This file is part of JuniperBot.
 *
 * JuniperBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBot. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.juniperbot.common.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import ru.juniperbot.common.model.MessageTemplateType;
import ru.juniperbot.common.persistence.entity.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Message template entity used for sending event messages like member join/leave, ranking announce, etc.
 *
 * @see MessageTemplateType
 * @see MessageTemplateField
 */
@Getter
@Setter
@Entity
@Table(name = "message_template")
public class MessageTemplate extends BaseEntity {

    private static final long serialVersionUID = -831681014535402042L;

    public static final int URL_MAX_LENGTH = 2000;

    public static final int TITLE_MAX_LENGTH = 256;

    public static final int VALUE_MAX_LENGTH = 1024;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private MessageTemplateType type = MessageTemplateType.TEXT;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "index")
    private List<MessageTemplateField> fields;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String embedContent;

    @Column(name = "channel_id")
    private String channelId;

    @Column
    private boolean tts;

    @Column(length = 7)
    private String color;

    @Column(name = "image_url", length = URL_MAX_LENGTH)
    private String imageUrl;

    @Column(name = "thumbnail_url", length = URL_MAX_LENGTH)
    private String thumbnailUrl;

    @Column(columnDefinition = "text")
    private String author;

    @Column(name = "author_url", length = URL_MAX_LENGTH)
    private String authorUrl;

    @Column(name = "author_icon_url", length = URL_MAX_LENGTH)
    private String authorIconUrl;

    @Column(columnDefinition = "text")
    private String title;

    @Column(name = "title_url", length = URL_MAX_LENGTH)
    private String titleUrl;

    @Column(columnDefinition = "text")
    private String footer;

    @Column(name = "footer_icon_url", length = URL_MAX_LENGTH)
    private String footerIconUrl;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

}
