package com.beam.assetManagement.assetRecon;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "PortServiceAccess")
public class PortServiceAccess {
    @Id
    private String id;

    private String credentials;


}
