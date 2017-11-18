package org.example.entity;

import lombok.*;

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
