package com.ctl.security.ips.common.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Kevin.Weber on 10/27/2014.
 */
@ToString
@EqualsAndHashCode()
@Accessors(chain = true)
public class Policy {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private PolicyStatus status;
}
