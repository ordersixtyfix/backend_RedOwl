package com.beam.assetManagement.assetRecon;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "PortServiceAccess")
public class PortServiceAccess {
    @Id
    private String id;

    private String credentials;


}
