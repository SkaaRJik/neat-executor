package ru.filippov.neatexecutor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExperimentStatusDto {
    Long experimentId;
    Long projectId;
    String status;
}
