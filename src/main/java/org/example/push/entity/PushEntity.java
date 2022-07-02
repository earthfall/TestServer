package org.example.push.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PushEntity {

    private PushNotificationEntity notification;
    private PushDataEntity data;
    private String to;
    private String priority;
}
