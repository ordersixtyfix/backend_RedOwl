package com.beam.assetManagement.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


}