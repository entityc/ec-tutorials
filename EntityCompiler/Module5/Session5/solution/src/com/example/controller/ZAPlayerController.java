package com.example.controller;

import java.util.UUID;
import com.example.ValidationException;
import com.example.dto.ZDPlayerDto;
import com.example.model.ZMPlayer;
import java.util.List;

public class ZAPlayerController {


    // POST /api/player
    public ZDPlayerDto createPlayer(ZDPlayerDto dtoObject) throws ValidationException {
        List<String> firstNameViolations = dtoObject.firstNameConstraintsViolated();
        if (firstNameViolations.size() > 0) {
            if (firstNameViolations.contains("correctLength")) {
                throw new ValidationException("The constraint was not met: length(firstName) >= 1 && length(firstName) <= 15");
            }
        }
        List<String> lastNameViolations = dtoObject.lastNameConstraintsViolated();
        if (lastNameViolations.size() > 0) {
            if (lastNameViolations.contains("correctLength")) {
                throw new ValidationException("The constraint was not met: length(lastName) >= 3 && length(lastName) <= 20");
            }
        }
        List<String> levelViolations = dtoObject.levelConstraintsViolated();
        if (levelViolations.size() > 0) {
            if (levelViolations.contains("levelValue")) {
                throw new ValidationException("The constraint was not met: level <= 12");
            }
        }
        ZMPlayer modelObject = ZMPlayer.fromDTO(dtoObject);
        modelObject.setId(UUID.randomUUID());
        return ZDPlayerDto.fromModel(modelObject);
    }
}
