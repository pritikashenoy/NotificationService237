package com.consumer.domain;

import javax.validation.constraints.NotNull;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications")
public class RSSItem {
    //TODO: Just keep timestamp, topic and notification
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String topic;

    @NotNull
    private String data;
}