package org.example.push;

import lombok.RequiredArgsConstructor;
import org.example.push.entity.PushEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceNotificationService {

    private final FirebaseClient firebaseClient;

    public PushEntity send(PushEntity entity) {
        return firebaseClient.send(entity);
    }
}
