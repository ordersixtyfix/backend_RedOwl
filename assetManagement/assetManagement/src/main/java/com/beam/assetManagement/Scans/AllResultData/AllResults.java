package com.beam.assetManagement.Scans.AllResultData;

import com.beam.assetManagement.common.GenericResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Data
@SuperBuilder
@NoArgsConstructor
@Component
public class AllResults{
    private GenericResponse<?> ftpResult;
    private GenericResponse<?> postGreSqlResult;
    private GenericResponse<?> mySqlResult;

}
