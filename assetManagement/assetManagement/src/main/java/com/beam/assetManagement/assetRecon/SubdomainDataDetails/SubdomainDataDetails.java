package com.beam.assetManagement.assetRecon.SubdomainDataDetails;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "SubdomainDataDetails")
@TypeAlias("SubdomainDataDetails")
@SuperBuilder
public class SubdomainDataDetails extends Base {


    private String subdomain;

    private boolean isRedirected = false;

    private String redirectDomain;

    private boolean isHostDown = false;

    private boolean isSSLValid = false;




}
