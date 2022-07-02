package org.example.push.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PushNotificationEntity {

    private String title;
    private String body;
}
