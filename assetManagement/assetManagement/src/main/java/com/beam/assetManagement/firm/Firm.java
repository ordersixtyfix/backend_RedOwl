package com.beam.assetManagement.firm;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@TypeAlias("Firm")
@Document(collection = "Firms")
public class Firm extends Base {

    private String firmName;

    private String firmLocation;

}
