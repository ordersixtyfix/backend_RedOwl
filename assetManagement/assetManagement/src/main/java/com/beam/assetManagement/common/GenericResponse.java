package com.beam.assetManagement.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GenericResponse<T> {

    private int code;

    private T data;
}