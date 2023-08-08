package com.beam.assetManagement.assetRecon.SubdomainData;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data

@NoArgsConstructor
@Document(collection = "SubdomainData")
@TypeAlias("SubdomainData")
@SuperBuilder
public class SubdomainData extends Base {


    private String assetName;

    private Set<String> subdomainIds;

    private String firmId;


}
