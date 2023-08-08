package com.beam.assetManagement.assetRecon.IpData;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "IpData")
@TypeAlias("IpData")
@SuperBuilder
public class IpData extends Base {

    private String assetId;

    private String ipAddress;

    private Set<String> subdomainShareIp;

    private List<SubdomainPortData> PortScanData;

    private List<AccessData> accessData;

    public void addShareSubdomains(String subdomain) {
        subdomainShareIp.add(subdomain);
    }

    public void insertPortData(List<SubdomainPortData> subdomainPortDataList) {
        this.PortScanData = subdomainPortDataList;
    }
}
