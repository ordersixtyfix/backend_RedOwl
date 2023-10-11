package com.beam.assetManagement.PayloadFile;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "PayloadFile")
@TypeAlias("PayloadFile")
@SuperBuilder
public class PayloadFile extends Base {
    private String filename;
    private String content;
    private String serviceType;
    private String userId;
    private String listType;
    private Date uploadDate;

}





