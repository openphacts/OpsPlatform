package eu.ops.services.chemspider.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "ERequestStatus", namespace="http://www.chemspider.com/")
@XmlEnum
public enum ERequestStatus {
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("Created")
    CREATED("Created"),
    @XmlEnumValue("Scheduled")
    SCHEDULED("Scheduled"),
    @XmlEnumValue("Processing")
    PROCESSING("Processing"),
    @XmlEnumValue("Suspended")
    SUSPENDED("Suspended"),
    @XmlEnumValue("PartialResultReady")
    PARTIAL_RESULT_READY("PartialResultReady"),
    @XmlEnumValue("ResultReady")
    RESULT_READY("ResultReady"),
    @XmlEnumValue("Failed")
    FAILED("Failed"),
    @XmlEnumValue("TooManyRecords")
    TOO_MANY_RECORDS("TooManyRecords");
    private final String value;

    ERequestStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERequestStatus fromValue(String v) {
        for (ERequestStatus c: ERequestStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
}

//@XmlRootElement(name = "ERequestStatus", namespace="http://www.chemspider.com/")
//public class ERequestStatus {
//	@XmlValue
//	public String value;
//}